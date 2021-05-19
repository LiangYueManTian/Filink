package com.fiberhome.filink.dump.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.dump.api.DumpFeign;
import com.fiberhome.filink.dump.bean.*;
import com.fiberhome.filink.dump.constant.*;
import com.fiberhome.filink.dump.dao.TaskInfoDao;
import com.fiberhome.filink.dump.exception.*;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.dump.service.TaskInfoService;
import com.fiberhome.filink.dump.stream.ExportStreams;
import com.fiberhome.filink.dump.utils.ExcelListUtil;
import com.fiberhome.filink.dump.utils.ExportApiUtils;
import com.fiberhome.filink.dump.utils.ZipsUtil;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.FtpUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;


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
@Slf4j
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


    @Qualifier("filink_log")
    @Autowired(required = false)
    private MongoTemplate filikLog;

    @Qualifier("filink_alarm")
    @Autowired(required = false)
    private MongoTemplate alarm_template;

    @Qualifier("filink_device")
    @Autowired(required = false)
    private MongoTemplate filinkDevice;

    @Autowired
    private DumpFeign dumpFeign;

    @Autowired
    private ParameterFeign parameterFeign;

    @Autowired
    private DumpService dumpService;


    /**
     * 新增任务 或重试任务
     *
     * @param exportDto 传入数据
     * @return 新增结果
     */
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
            taskInfo.setQueryCondition(s);
            taskInfo.setColumnInfos(columnInfos);
            taskInfo.setMethodPath(methodPath);
            taskInfo.setFileGeneratedNum(0);
            taskInfo.setCreateTime(System.currentTimeMillis());
            taskInfo.setTaskInfoType(exportDto.getDumpType());
            result = taskInfoDao.insert(taskInfo);
            //不为空 重试
        } else {
            taskInfo = taskInfoDao.selectById(exportDto.getTaskId());
            if (taskInfo == null) {
                return ResultUtils.warn(ExportResultCodeConstant.TASK_DOES_NOT_EXIST, I18nUtils.getSystemString(ExportI18nConstant.TASK_DOES_NOT_EXIST));
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
        Integer integer = taskInfoDao.updateById(taskInfo);
        //如果更新成功 返回任务是否被停止
//        if (integer == 1) {
//            if (flag) {
//                WebSocketMessage socketMessage = new WebSocketMessage();
//                socketMessage.setChannelKey("dump");
//                socketMessage.setChannelId(exportDto.getToken());
//                socketMessage.setMsgType(0);
//                socketMessage.setMsg(I18nUtils.getSystemString(ExportI18nConstant.ONE_EXPORT_TASK_COMPLETED));
//                Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
//                exportStreams.exportWebSocketOutput().send(message);
//            }
//        }
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
        Integer integer = taskInfoDao.updateById(taskInfo);
        if (integer != 1) {
            throw new FilinkExportDateBaseException();
        }
        return true;
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
     * 每个文件记录数据的最大条数
     */
    @Value(ExportApiConstant.LIST_EXCEL_SIZE)
    private Integer listExcelSize;
    /**
     * 临时文件的路径
     */
    @Value(ExportApiConstant.LIST_EXCEL_FILE_PATH)
    private String listExcelFilePath;
    /**
     * 上传文件服务类实体
     */
    @Autowired
    private UploadFile uploadFile;
    /**
     * 打包实体
     */
    @Autowired
    private ZipsUtil zipsUtil;
    /**
     * 最大导出条数
     */
    @Value(ExportApiConstant.MAX_EXPORT_DATA_SIZE)
    private Integer maxExportDataSize;

    /**
     * 导出数据
     *
     * @param
     */
    @Async
    @Override
    public void exportData(Export export, List list) {

        System.out.println("------->" + Thread.currentThread().getName());
        Map<String, String> map = new HashMap<>(64);
        //存入时区信息
//        map.put("timeZone", dump.getTimeZone());
//        map.put("userId", dump.getUserId());
//        ExportApiUtils.RESOURCE.set(map);
        QueryCondition queryCondition = export.getQueryCondition();
        PageCondition pageCondition = queryCondition.getPageCondition();
        //pageCondition.setPageSize(listExcelSize);
        pageCondition.setPageSize(list.size());
        pageCondition.setPageNum(1);
        //pageCondition.setBeginNum((pageCondition.getPageNum() - 1) * pageCondition.getPageSize());
        pageCondition.setBeginNum(0);
        //创建该任务文件夹
        String taskFolderPath = listExcelFilePath + export.getTaskId();
        System.out.println("taskFolderPath ----->" + taskFolderPath);
        File dirFile = new File(taskFolderPath);
        System.out.println("dirFile.exists()" + dirFile.exists());
        //如果文件夹不存在，则创建新的文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(date);
        String excelFolderName = export.getListName() + time;
        export.setExcelFolderName(excelFolderName);
        export.setTaskFolderPath(taskFolderPath);
        startExport(export, list);
    }

    public void startExport(Export export, List list) {
        try {
            specificExportData(export, list);
        } catch (Exception e) {
            log.error("start export dump data error", e);
        }
        //updateTaskInfo(export, list);
    }

    /**
     * 具体导出数据
     *
     * @param
     */
    @Async
    protected void specificExportData(Export export, List list) throws Exception {
        QueryCondition queryCondition = export.getQueryCondition();
//        List list = queryData(queryCondition);
        ExportApiUtils.handelExportDto(export, list);
        List<List<String>> dataList = export.getDataList();
        List<String> columnNameList = dataList.get(0);
        dataList.remove(0);
        List<List<String>> valueList = dataList;
        String excelFolderName = export.getExcelFolderName();
        //生成文件名称
        String fileName = excelFolderName + "-" + queryCondition.getPageCondition().getPageNum();
        String excelFolderPath = export.getTaskFolderPath() + "/" + excelFolderName + "/";
        if (export.getExcelType() == 0) {
            //将list文件分割成若干个小list，一个sheet最多能创建65535行
            ExcelListUtil.listExport(columnNameList, valueList, excelFolderPath, fileName);
        } else {
            ExcelListUtil.listExportToCsv(columnNameList, valueList, excelFolderPath, fileName);
        }

        //添加用户的时候加锁
        String lockKey = ExportApiConstant.LOCK_NAME;
        //等待获取锁的时间，单位ms
        int acquireTimeout = 10000;
        //拿到锁的超时时间
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        updateTaskInfo(export, list);
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
    }

    /**
     * 更新任务信息
     */
    private void updateTaskInfo(Export export, List list) {

        PageCondition pageCondition = export.getQueryCondition().getPageCondition();
        Integer pageNum = pageCondition.getPageNum();
        export.setFileGeneratedNum(export.getFileGeneratedNum() + 1);
        ExportDto exportDto = new ExportDto();
        BeanUtils.copyProperties(export, exportDto);

        if (pageNum == (int) Math.ceil((double) export.getDateSize() / (double) listExcelSize)) {
            String filePath = packageFile(export);
            exportDto.setFileGeneratedNum(export.getFileNum());
            exportDto.setFilePath(filePath);
            //如果更新返回值为true 即更新成功
            if (updateTaskFileNumById(exportDto)) {
                //将最原始对象文件数与生成数设置为相等，通知切面此任务已经完成
                export.setFileGeneratedNum(export.getFileNum());
            }
        } else {
            pageCondition.setPageNum(pageCondition.getPageNum() + 1);
            pageCondition.setBeginNum((pageCondition.getPageNum() - 1) * pageCondition.getPageSize());
            startExport(export, list);
        }
    }

    /**
     * 文件上传打包
     */
    private String packageFile(Export export) {
        String taskFolderPath = export.getTaskFolderPath();
        String excelFolderName = export.getExcelFolderName();
        String excelFolderPath = taskFolderPath + "/" + excelFolderName;
        zipsUtil.createZip(excelFolderPath, taskFolderPath + "/" + excelFolderName, export.getTaskId());
        File file = new File(taskFolderPath + "/" + excelFolderName + ".zip");
        if (export.getDumpSite() == 1) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                log.error("dump zip util packageFile error", e);
            }
            //获取ftp参数信息
            FtpSettings ftpSettings = parameterFeign.queryFtpSettings();
            //ftp上传文件

            FtpUtil.uploadFile(ftpSettings.getInnerIpAddress(),ftpSettings.getUserName(),ftpSettings.getPassword(),ftpSettings.getPort(),
                    ExportApiConstant.FILE_PATH,file.getName(),inputStream,100);

            ExcelListUtil.deleteDir(new File(taskFolderPath));
            return ExportApiConstant.FILE_PATH + file.getName();
        }
        ExcelListUtil.deleteDir(new File(excelFolderPath));
        return taskFolderPath + "/" + excelFolderName + ".zip";
    }


    /**
     * 插入任务信息
     *
     * @return
     */
    @Override
    public Export insertTask(ExportDto exportDto, String serverName, String listName, Integer count) {
        QueryCondition queryCondition = exportDto.getQueryCondition();
        //拼接服务名称前缀
//        String requestURI = serverName + ExportApiUtils.getRequestUri();
//        exportDto.setMethodPath(requestURI);
        //存入操作用户
        exportDto.setUserId(null);
        exportDto.setListName(listName);
        PageCondition pageCondition = queryCondition.getPageCondition();
        pageCondition.setPageSize(listExcelSize);
        pageCondition.setPageNum(1);
        // 查询count等信息  构造任务数据 queryCount可以修改方法 ，
//        Integer count = queryCount(queryCondition);
        if (count == 0) {
            throw new FilinkExportNoDataException();
        }
        if (count > maxExportDataSize) {
            throw new FilinkExportDataTooLargeException(String.valueOf(count));
        }
        double fileRealNum = (double) count / (double) listExcelSize;
        int fileNum = (int) (Math.ceil(fileRealNum + fileRealNum * 0.1 + 1));
        exportDto.setFileNum(fileNum);
        Result result = addTask(exportDto);
        int code = result.getCode();
        if (code == ExportApiConstant.TASK_NUM_TOO_BIG_CODE) {
            throw new FilinkExportTaskNumTooBigException();
        }//调用失败
        else if (code != 0) {
            throw new FilinkExportException();
        }
        Export export = new Export<>();
        BeanUtils.copyProperties(exportDto, export);
        if (export.getFileGeneratedNum() == null) {
            export.setFileGeneratedNum(0);
        }
        export.setDateSize(count);
        //从请求头中获取时区、token
//        export.setTimeZone(ExportApiUtils.getZone());
//        export.setToken(RequestInfoUtils.getToken());
        export.setTaskId((String) result.getData());
        return export;
    }

//    @Override
//    public DumpDto queryDumpAlarm() {
//
//        DumpBean dumpBean = dumpFeign.queryDump(DumpType.ALARM_LOG_DUMP.getType());
//
//        //判断是否开启转储
//        if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {
//
//            //如果为按条数执行
//            if (ExportApiConstant.DUMP_DATA_BY_NUMBER.equals(dumpBean.getTriggerCondition())) {
//                long count = alarm_template.count(new Query(), AlarmHistory.class);
//
//                //查询告警是否达到转储的条件
//                String dumpQuantityThreshold = dumpBean.getDumpQuantityThreshold();
//                if (count >= Long.parseLong(dumpQuantityThreshold)) {
//
//                    int dumpNum = Integer.parseInt(dumpBean.getTurnOutNumber());
//                    Sort alarmBeginTime = new Sort(Sort.Direction.DESC, "alarm_begin_time");
//                    Query query = new Query().with(alarmBeginTime).limit(dumpNum);
//                    List<AlarmHistory> alarmHistories = alarm_template.find(query, AlarmHistory.class);
//
//                    String dumpPlace = dumpBean.getDumpPlace();
//                    return new DumpDto(alarmHistories, dumpNum, dumpPlace, query, AlarmHistory.class);
//                }
//            }
//
//            //按照月份来查询数据,先设定每月的1号为自动执行的日期
//            if (ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition())) {
//
//                String monthString = dumpBean.getDumpInterval();
//                int month = Integer.parseInt(monthString);
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.MONTH, -1 * month);
//                long beginTime = calendar.getTimeInMillis();
//                //如果在开始时间之后有进行导出任务，则不进行数据存储
//
//                List<String> idList = taskInfoDao.queryTaskByCreateTime(beginTime);
//                if (idList == null) {
//
//                    long count = alarm_template.count(new Query(), AlarmHistory.class);
//
//                    //查询告警是否达到被转储的数量
//                    String dumpNumString = dumpBean.getTurnOutNumber();
//                    if (count >= Long.parseLong(dumpNumString)) {
//
//                        int dumpNum = Integer.parseInt(dumpNumString);
//                        Sort alarmBeginTime = new Sort(Sort.Direction.DESC, "alarm_begin_time");
//                        Query query = new Query().with(alarmBeginTime).limit(dumpNum);
//                        String dumpPlace = dumpBean.getDumpPlace();
//                        List<AlarmHistory> alarmHistories = alarm_template.find(query, AlarmHistory.class);
//                        return new DumpDto(alarmHistories, dumpNum, dumpPlace, query, AlarmHistory.class);
//                    }
//                }
//            }
//        }
//
//        //不符合转储条件
//        return null;
//    }

    @Override
    public Result queryDumpData(DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime) {

        DumpBean dumpBean = dumpFeign.queryDump(dumpType.getType());

        //判断是否开启转储
        if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {

            //如果为按条数执行
            if (ExportApiConstant.DUMP_DATA_BY_NUMBER.equals(dumpBean.getTriggerCondition())) {
                long count = mongoTemplate.count(new Query(), clazz);
                log.info("查询到的转储总数量 = " + count);
                //查询告警是否达到转储的条件
                String dumpQuantityThreshold = dumpBean.getDumpQuantityThreshold();
                log.info("设置的转储数量 = " + dumpQuantityThreshold);
                if (count >= Long.parseLong(dumpQuantityThreshold)) {

                    int dumpNum = Integer.parseInt(dumpBean.getTurnOutNumber());
                    Sort beginTime = new Sort(Sort.Direction.DESC, createTime);
                    Query query = new Query().with(beginTime).limit(dumpNum);
                    List list = mongoTemplate.find(query, clazz);

                    String dumpPlace = dumpBean.getDumpPlace();
                    DumpDto dumpDto = new DumpDto(list, dumpNum, dumpPlace, query, clazz);
                    return ResultUtils.success(dumpDto);
                }
                return ResultUtils.success(ExportApiConstant.NOT_ENOUGH_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
            }

            //按照月份来查询数据,先设定每月的1号为自动执行的日期
            if (ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition())) {

                String monthString = dumpBean.getDumpInterval();
                int month = Integer.parseInt(monthString);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1 * month);
                long beginTime = calendar.getTimeInMillis();
                //如果在开始时间之后有进行导出任务，则不进行数据存储
                String type = dumpType.getType();
                List<String> idList = taskInfoDao.queryTaskByCreateTime(type,beginTime);
                if (idList == null || idList.size() == 0) {

                    long count = mongoTemplate.count(new Query(), clazz);

                    //查询告警是否达到被转储的数量
                    String dumpNumString = dumpBean.getTurnOutNumber();
                    if (count >= Long.parseLong(dumpNumString)) {

                        int dumpNum = Integer.parseInt(dumpNumString);
                        Sort sort = new Sort(Sort.Direction.DESC, createTime);
                        Query query = new Query().with(sort).limit(dumpNum);
                        String dumpPlace = dumpBean.getDumpPlace();
                        List list = mongoTemplate.find(query, clazz);
                        DumpDto dumpDto = new DumpDto(list, dumpNum, dumpPlace, query, clazz);
                        return ResultUtils.success(dumpDto);
                    }
                    return ResultUtils.success(ExportApiConstant.NOT_ENOUGH_DUMP_DATA,
                            I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
                }
                return ResultUtils.success(ExportApiConstant.SPECIFIED_TIME_HAS_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.SPECIFIED_TIME_HAS_DUMP_DATA));
            }
        }

        //不符合转储条件
        return ResultUtils.success(ExportApiConstant.DISABLE_DUMP_DATA,
                I18nUtils.getSystemString(ExportI18nConstant.DISABLE_DUMP_DATA));
    }

    /**
     * 获取被转储操作日志的数据信息
     * @param dumpType 转储类型
     * @param mongoTemplate mongo数据库
     * @param clazz 转储的数据类型
     * @param createTime 排序的字段
     * @return
     */
    @Override
    public Result queryDumpDataInfo(DumpBean dumpBean ,DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime) {
        //判断是否开启转储
        if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {

            //如果为按条数执行
            if (ExportApiConstant.DUMP_DATA_BY_NUMBER.equals(dumpBean.getTriggerCondition())) {
                long count = mongoTemplate.count(new Query(), clazz);
                log.info("查询到的转储总数量 = " + count);
                //查询告警是否达到转储的条件
                String dumpQuantityThreshold = dumpBean.getDumpQuantityThreshold();
                log.info("设置的转储数量 = " + dumpQuantityThreshold);
                if (count >= Long.parseLong(dumpQuantityThreshold)) {
                    DumpDto dumpDto = null;
                    return ResultUtils.success(dumpDto);
                }
                return ResultUtils.warn(ExportApiConstant.NOT_ENOUGH_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
            }

            //按照月份来查询数据,先设定每月的1号为自动执行的日期
            if (ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition())) {

                String monthString = dumpBean.getDumpInterval();
                int month = Integer.parseInt(monthString);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1 * month);
                long beginTime = calendar.getTimeInMillis();
                //如果在开始时间之后有进行导出任务，则不进行数据存储
                String type = dumpType.getType();
                List<String> idList = taskInfoDao.queryTaskByCreateTime(type,beginTime);
                if (idList == null || idList.size() == 0) {

                    long count = mongoTemplate.count(new Query(), clazz);

                    //查询告警是否达到被转储的数量
                    String dumpNumString = dumpBean.getTurnOutNumber();
                    if (count >= Long.parseLong(dumpNumString)) {
                        DumpDto dumpDto = null;
                        return ResultUtils.success(dumpDto);
                    }
                    return ResultUtils.warn(ExportApiConstant.NOT_ENOUGH_DUMP_DATA,
                            I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
                }
                return ResultUtils.warn(ExportApiConstant.SPECIFIED_TIME_HAS_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.SPECIFIED_TIME_HAS_DUMP_DATA));
            }
        }

        //不符合转储条件
        return ResultUtils.warn(ExportApiConstant.DISABLE_DUMP_DATA,
                I18nUtils.getSystemString(ExportI18nConstant.DISABLE_DUMP_DATA));
    }


    @Override
    public Result dumpOperaLogData(@RequestBody DumpData dumpData) {

        DumpType dumpType = dumpData.getDumpType();
        MongoTemplate mongoTemplate = dumpData.getMongoTemplate();
        Class clazz = dumpData.getClazz();
        String queryStr = dumpData.getQueryStr();

        ExportDto exportDto = new ExportDto();
        QueryCondition queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        queryCondition.setPageCondition(pageCondition);
        exportDto.setDumpType(dumpData.getDumpType().getType());
        exportDto.setQueryCondition(queryCondition);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        //根据class获取属性
        for (Field field : clazz.getDeclaredFields()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(field.getName());
            columnInfo.setPropertyName(field.getName());
            columnInfoList.add(columnInfo);
        }

        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);

        DumpBean dumpBean = dumpFeign.queryDump(dumpType.getType());
        Result dumpDataResult = queryDumpDataInfo(dumpBean ,dumpType, mongoTemplate, clazz, queryStr);
        if (dumpDataResult.getCode() == ResultCode.SUCCESS) {
            dumpService.searchDumpData(dumpBean, dumpData, exportDto);
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(ExportI18nConstant.DUMP_DATA_SUCCESS));
        }
        return dumpDataResult;
    }

    @Override
    public Result queryDumpInfoById(String taskId) {

        TaskInfo taskInfo = taskInfoDao.selectById(taskId);
        Long dumpNumber = taskInfoDao.queryTotalTaskNum(taskInfo.getTaskInfoType());
        taskInfo.setDumpAllNumber(dumpNumber);
        return ResultUtils.success(taskInfo);
    }

    @Override
    public Result dumpData(Integer dumpType) {
        //告警
        if (ExportApiConstant.ALARM_LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            DumpData dumpData = new DumpData();
            dumpData.setClazz(AlarmHistory.class);
            dumpData.setDumpType(DumpType.ALARM_LOG_DUMP);
            dumpData.setMongoTemplate(alarm_template);
            dumpData.setQueryStr(ExportApiConstant.ALARM_QUERY);
            dumpData.setListName(ExportApiConstant.ALARM_LIST_NAME);
            if (ExportApiConstant.ALARM_LOG_TYPE.equals(dumpType)) {
                return dumpOperaLogData(dumpData);
            } else {
                dumpOperaLogData(dumpData);
            }
        }


        if (ExportApiConstant.LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            String operateLogName = I18nUtils.getSystemString(ExportI18nConstant.OPERATE_LOG);
            String securityLogName = I18nUtils.getSystemString(ExportI18nConstant.SECURITY_LOG);
            String systemLogName = I18nUtils.getSystemString(ExportI18nConstant.SYSTEM_LOG);
            String msg = "";

            //操作日志
            DumpData dumpOperaLogData = new DumpData();
            dumpOperaLogData.setClazz(OperateLog.class);
            dumpOperaLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
            dumpOperaLogData.setMongoTemplate(filikLog);
            dumpOperaLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
            dumpOperaLogData.setListName(ExportApiConstant.OPERA_LOG_LIST_NAME);
            Result operaLogDataResult = dumpOperaLogData(dumpOperaLogData);
            if (operaLogDataResult.getCode() != ResultCode.SUCCESS) {
                msg += operateLogName + ",";
                msg += operaLogDataResult.getMsg();
            }

            //系统日志
            DumpData dumpSysLogData = new DumpData();
            dumpSysLogData.setClazz(SystemLog.class);
            dumpSysLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
            dumpSysLogData.setMongoTemplate(filikLog);
            dumpSysLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
            dumpSysLogData.setListName(ExportApiConstant.SYS_LOG_LIST_NAME);
            Result sysLogDataResult = dumpOperaLogData(dumpSysLogData);
            if (sysLogDataResult.getCode() != ResultCode.SUCCESS) {
                msg += "\n\r" + systemLogName + ",";
                msg += sysLogDataResult.getMsg();
            }

            //安全日志
            DumpData dumpSecLogData = new DumpData();
            dumpSecLogData.setClazz(SecurityLog.class);
            dumpSecLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
            dumpSecLogData.setMongoTemplate(filikLog);
            dumpSecLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
            dumpSecLogData.setListName(ExportApiConstant.SEC_LOG_LIST_NAME);
            Result securityLogDataResult = dumpOperaLogData(dumpSecLogData);
            if (securityLogDataResult.getCode() != ResultCode.SUCCESS) {
                msg += "\n\r" + securityLogName + ",";
                msg += securityLogDataResult.getMsg();
            }
            Result result = this.getDumpResult(dumpType, msg);
            if (!ObjectUtils.isEmpty(result)) {
                return result;
            }
        }

        //设施日志
        if (ExportApiConstant.DEVICE_LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            DumpData dumpDeviceLogData = new DumpData();
            dumpDeviceLogData.setClazz(DeviceLog.class);
            dumpDeviceLogData.setDumpType(DumpType.DEVICE_LOG_DUMP);
            dumpDeviceLogData.setMongoTemplate(filinkDevice);
            dumpDeviceLogData.setQueryStr(ExportApiConstant.DEVICE_LOG_QUERY);
            dumpDeviceLogData.setListName(ExportApiConstant.DEVICE_LOG_LIST_NAME);
            return dumpOperaLogData(dumpDeviceLogData);
        }
        return ResultUtils.warn(ResultCode.FAIL);
    }

    /**
     * 获取转储返回值
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 17:27
     * @param dumpType 转储类别
     * @param msg 返回消息
     * @return 返回转储返回值
     */
    public Result getDumpResult(Integer dumpType, String msg) {
        if (ExportApiConstant.LOG_TYPE.equals(dumpType) && !ObjectUtils.isEmpty(msg)) {
            return ResultUtils.warn(ExportResultCodeConstant.ERROR_CODE, msg);
        } else if (ExportApiConstant.LOG_TYPE.equals(dumpType) && ObjectUtils.isEmpty(msg)) {
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(ExportI18nConstant.DUMP_DATA_SUCCESS));
        }
        return null;
    }

    @Override
    public Result queryDumpInfo(String dumpType) {

        TaskInfo taskInfo = taskInfoDao.queryLastTaskInfo(dumpType);
        Long dumpNumber = taskInfoDao.queryTotalTaskNum(dumpType);
        if(taskInfo != null) {
            taskInfo.setDumpAllNumber(dumpNumber);
            return ResultUtils.success(taskInfo);
        }

        //如果没有任务信息，返回为空
        return ResultUtils.success(null);
    }
}