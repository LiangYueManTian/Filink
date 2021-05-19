package com.fiberhome.filink.export.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.export.bean.ExportDto;
import com.fiberhome.filink.export.bean.TaskInfo;
import com.fiberhome.filink.export.bean.TaskInfoDto;
import com.fiberhome.filink.export.constant.ExportI18nConstant;
import com.fiberhome.filink.export.constant.ExportResultCodeConstant;
import com.fiberhome.filink.export.constant.TaskConstant;
import com.fiberhome.filink.export.constant.TaskStatusConstant;
import com.fiberhome.filink.export.dao.TaskInfoDao;
import com.fiberhome.filink.export.exception.FilinkExportDateBaseException;
import com.fiberhome.filink.export.exception.FilinkExportDirtyDataException;
import com.fiberhome.filink.export.service.TaskInfoService;
import com.fiberhome.filink.export.stream.ExportStreams;
import com.fiberhome.filink.export.utils.CheckUtil;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.fiberhome.filink.export.constant.TaskConstant.TASK;
import static com.fiberhome.filink.mysql.MpQueryHelper.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-02-25
 */
@RefreshScope
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoDao, TaskInfo> implements TaskInfoService {
    /**
     * 自动注入taskInfoDao
     */
    @Autowired
    private TaskInfoDao taskInfoDao;
    /**
     * 远程调用文件服务器实体
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * 过期天数
     */
    @Value(TaskConstant.DAYS_OVERDUE)
    private Integer daysOverdue;
    /**
     * 发送消息类
     */
    @Autowired
    private ExportStreams exportStreams;
    /**
     * 用户最大导出任务数量
     */
    @Value(TaskConstant.MAX_TASK_NUM)
    private Integer maxTaskNum;

    /**
     * 新增任务 或重试任务
     *
     * @param exportDto 传入数据
     * @return 新增结果
     */
    @Override
    public Result addTask(ExportDto exportDto) {
        //任务实体变量
        TaskInfo taskInfo;
        //数据库操作返回数据
        Integer result;
        String taskId = exportDto.getTaskId();
        //判断是否超过最大任务数限制
        int i = taskInfoDao.selectOngoingTaskCountByUserId(exportDto.getUserId());
        if (i == maxTaskNum || i > maxTaskNum) {
            return ResultUtils.warn(ExportResultCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
        }
        //任务id为null 新增任务
        if (StringUtils.isEmpty(taskId)) {
            //创建任务对象并添加到数据库
            taskInfo = new TaskInfo();
            taskInfo.setCreateTime(new Date());
            //生成id
            taskInfo.setTaskInfoId(NineteenUUIDUtils.uuid());
            //获取文件总数
            taskInfo.setFileNum(exportDto.getFileNum());
            //将任务设置为准备
            taskInfo.setTaskStatus(TaskStatusConstant.READY);
            //设置操作用户id
            taskInfo.setCreateUser(exportDto.getUserId());
            //设置表格类型
            taskInfo.setExcelType(exportDto.getExcelType());
            //设置任务名称
            taskInfo.setListName(exportDto.getListName());
            //设置重试条件
            String s = JSON.toJSONString(exportDto.getQueryCondition());
            String columnInfos = JSON.toJSONString(exportDto.getColumnInfoList());
            String methodPath = (exportDto.getMethodPath());
            List<Object> objectList = exportDto.getObjectList();
            if (objectList != null && objectList.size() > 0) {
                taskInfo.setObjectList(JSON.toJSONString(objectList));
            }
            taskInfo.setQueryCondition(s);
            taskInfo.setColumnInfos(columnInfos);
            taskInfo.setMethodPath(methodPath);
            result = taskInfoDao.insert(taskInfo);
            //不为空 重试
        } else {
            taskInfo = taskInfoDao.selectById(exportDto.getTaskId());
            taskInfo.setUpdateTime(new Date());
            if (taskInfo == null) {
                return ResultUtils.warn(ExportResultCodeConstant.TASK_DOES_NOT_EXIST);
            }
            taskInfo.setTaskStatus(TaskStatusConstant.READY);
            taskInfo.setFileGeneratedNum(0);
            result = taskInfoDao.updateById(taskInfo);
        }
        if (1 == result) {
            //设置redis中的key值
            String key = "task" + taskInfo.getTaskInfoId();
            RedisUtils.set(key, TaskStatusConstant.START);
            return ResultUtils.success(taskInfo.getTaskInfoId());
        }
        throw new FilinkExportDateBaseException();
    }

    /**
     * 停止任务
     *
     * @param taskId 要停止的任务
     * @return 停止结果
     */
    @Override
    public Result stopTask(String taskId) {
        RedisUtils.remove("task" + taskId);
        TaskInfo taskInfo = taskInfoDao.selectById(taskId);
        if (taskInfo == null || TaskConstant.DELETED.equals(taskInfo.getIsDeleted())) {
            return ResultUtils.warn(ExportResultCodeConstant.TASK_DOES_NOT_EXIST, I18nUtils.getSystemString(ExportI18nConstant.TASK_DOES_NOT_EXIST));
        }
        String url = taskInfo.getFilePath();
        List<String> urlList = new ArrayList<>();
        if (!StringUtils.isEmpty(url)) {
            urlList.add(url);
            fdfsFeign.deleteFilesPhy(urlList);
        }
        taskInfo.setTaskStatus(TaskStatusConstant.STOP);
        taskInfo.setFileGeneratedNum(0);
        taskInfo.setUpdateTime(new Date());
        Integer integer = taskInfoDao.updateById(taskInfo);
        if (1 == integer) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ExportI18nConstant.STOP_MISSION_SUCCESSFUL));
        }
        throw new FilinkExportDateBaseException();
    }

    /**
     * 分页查询任务列表
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result exportTaskList(QueryCondition queryCondition) {
        //逻辑删除过滤
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("neq");
        filterCondition.setFilterValue(1);
        //当前用户过滤
        FilterCondition userFilterCondition = new FilterCondition();
        userFilterCondition.setFilterField("create_user");
        userFilterCondition.setOperator("eq");
        userFilterCondition.setFilterValue(RequestInfoUtils.getUserId());
        List filterConditions = queryCondition.getFilterConditions();
        if (filterConditions == null) {
            return ResultUtils.warn(ExportResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(ExportI18nConstant.PARAM_NULL));
        }
        filterConditions.add(filterCondition);
        filterConditions.add(userFilterCondition);
        // 构造分页条件
        if (queryCondition.getPageCondition() == null || CheckUtil.checkPageConditionNull(queryCondition.getPageCondition())) {
            return ResultUtils.warn(ExportResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(ExportI18nConstant.PARAM_NULL));
        }
        // 无排序时的默认排序（当前按照创建时间降序）
        if (queryCondition.getSortCondition() == null || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("create_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        Page page = myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
        List<TaskInfo> taskInfoList = taskInfoDao.selectPage(page, wrapper);
        Integer count = taskInfoDao.selectCount(wrapper);
        List<TaskInfoDto> taskInfoDtoList = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfoList) {
            TaskInfoDto taskInfoDto = new TaskInfoDto();
            BeanUtils.copyProperties(taskInfo, taskInfoDto);
            taskInfoDto.setObjectList(JSONArray.parseArray(taskInfo.getObjectList()));
            taskInfoDtoList.add(taskInfoDto);
        }
        PageBean pageBean = myBatiesBuildPageBean(page, count, taskInfoDtoList);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 批量删除任务
     *
     * @param taskIdList 传入任务id集合
     * @return 删除结果
     */
    @Override
    public Result deleteTask(List<String> taskIdList) {
        if (taskIdList.size() == 0) {
            return null;
        }
        for (String id : taskIdList) {
            RedisUtils.remove(TASK + id);
        }
        List<TaskInfo> taskInfoList = taskInfoDao.selectBatchIds(taskIdList);
        List<String> urlList = new ArrayList<>();
        if (taskInfoList == null || taskInfoList.size() == 0) {
            throw new FilinkExportDirtyDataException(I18nUtils.getSystemString(ExportI18nConstant.TASK_DOES_NOT_EXIST));
        }
        for (TaskInfo taskInfo : taskInfoList) {
            String url = taskInfo.getFilePath();
            if (!StringUtils.isEmpty(url)) {
                urlList.add(url);
            }
        }
        if (urlList.size() > 0) {
            fdfsFeign.deleteFilesPhy(urlList);
        }
        taskInfoDao.deleteTaskByTaskIds(taskIdList);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ExportI18nConstant.DELETE_MISSION_SUCCESSFUL));
    }

    /**
     * 查询任务是否被停止
     *
     * @param taskId 任务id
     * @return
     */
    @Override
    public Boolean selectTaskIsStopById(String taskId) {
        String key = "task" + taskId;
        if (RedisUtils.get(key) == null) {
            return true;
        }
        return false;
    }

    /**
     * 删除过期任务
     */
    @Override
    public void deleteOverdueTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - daysOverdue);
        Date time = calendar.getTime();
        List<String> taskIds = taskInfoDao.selectOverdueTask(time);
        deleteTask(taskIds);
    }

    /**
     * 跟新任务进度
     *
     * @param exportDto 传入参数
     * @return 任务是否停止
     */
    @Override
    public Boolean updateTaskFileNumById(ExportDto exportDto) {
        TaskInfo taskInfo = new TaskInfo();
        String taskId = exportDto.getTaskId();
        //如果生成数量与总数相等
        Integer fileGeneratedNum = exportDto.getFileGeneratedNum();
        boolean flag = fileGeneratedNum.equals(exportDto.getFileNum());
        if (flag) {
            taskInfo.setTaskStatus(TaskStatusConstant.COMPLETE);
            taskInfo.setFilePath(exportDto.getFilePath());
            //删除缓存中的标志
            String key = "task" + taskId;
            RedisUtils.remove(key);
        } else {
            taskInfo.setTaskStatus(TaskStatusConstant.START);
        }
        taskInfo.setTaskInfoId(taskId);
        taskInfo.setFileGeneratedNum(fileGeneratedNum);
        taskInfo.setUpdateTime(new Date());
        Integer integer = taskInfoDao.updateById(taskInfo);
        //如果更新成功 返回任务是否被停止
        if (integer == 1) {
            if (flag) {
                WebSocketMessage socketMessage = new WebSocketMessage();
                socketMessage.setChannelKey("export");
                socketMessage.setChannelId(exportDto.getToken());
                socketMessage.setMsgType(0);
                socketMessage.setMsg(ResultUtils.success(ExportResultCodeConstant.ONE_EXPORT_TASK_COMPLETED,null));
                Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
                exportStreams.exportWebSocketOutput().send(message);
            }
        }
        return selectTaskIsStopById(exportDto.getTaskId());
    }

    /**
     * 将任务设置为异常
     *
     * @param taskId 传入参数
     * @return 设置结果
     */
    @Override
    public Boolean changeTaskStatusToUnusual(String taskId) {
        TaskInfo dbTaskInfo = taskInfoDao.selectById(taskId);
        //如果库中数据已经删除，不做任何操作
        if (dbTaskInfo == null || TaskConstant.DELETED.equals(dbTaskInfo.getIsDeleted())) {
            return true;
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskInfoId(taskId);
        taskInfo.setTaskStatus(TaskStatusConstant.UNUSUAL);
        String key = "task" + taskId;
        RedisUtils.remove(key);
        taskInfo.setUpdateTime(new Date());
        Integer integer = taskInfoDao.updateById(taskInfo);
        if (integer != 1) {
            throw new FilinkExportDateBaseException();
        }
        return true;
    }

}