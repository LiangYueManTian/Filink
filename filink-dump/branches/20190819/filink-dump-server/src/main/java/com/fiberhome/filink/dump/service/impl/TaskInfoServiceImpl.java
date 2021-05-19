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
import com.fiberhome.filink.dump.service.ElasticSearchService;
import com.fiberhome.filink.dump.service.TaskInfoService;
import com.fiberhome.filink.dump.stream.ExportStreams;
import com.fiberhome.filink.dump.utils.*;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    private MongoTemplate alarmTemplate;

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
     * es数据库逻辑处理类
     */
    @Autowired
    private ElasticSearchService elasticSearchService;


    /**
     * 系统语言环境
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;


    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;


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
            return ResultUtils.warn(DumpResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
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
                return ResultUtils.warn(DumpResultCode.TASK_DOES_NOT_EXIST, I18nUtils.getSystemString(ExportI18nConstant.TASK_DOES_NOT_EXIST));
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
     * 更新任务进度
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
        taskInfoDao.updateById(taskInfo);
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
        String key = "task" + taskId;
        RedisUtils.remove(key);
        TaskInfo dbTaskInfo = taskInfoDao.selectById(taskId);
        //如果库中数据已经删除，不做任何操作
        if (dbTaskInfo == null || TaskConstant.DELETED.equals(dbTaskInfo.getIsDeleted())) {
            return true;
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskInfoId(taskId);
        taskInfo.setTaskStatus(TaskStatusConstant.UNUSUAL);
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
        try {
            if (RedisUtils.get(key) == null) {
                return true;
            }
        } catch (Exception e) {
            log.error("select task is stop by id failed", e);
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
    @Override
    @Async
    public void exportData(Export export, int dumpNum, String queryStr, Class clazz, MongoTemplate mongoTemplate, DumpBean dumpBean, DumpData dumpData, String trigger) {
        //创建该任务文件夹
        String taskFolderPath = listExcelFilePath + export.getTaskId();
        log.info("dump data generate file folder path : {}", taskFolderPath);
        File dirFile = new File(taskFolderPath);
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
        if (!ExportApiConstant.DUMP_OPERATION_DELETE.equals(dumpBean.getDumpOperation())) {
            Long startExcelDate = System.currentTimeMillis();
            log.info("dump data generate file : begin generate excel file:" );
            startExport(export, dumpNum, 1, queryStr, clazz, mongoTemplate);
            Long beforeExcelDate = System.currentTimeMillis();
            log.info("dump data generate file : generate excel file end，time is {}" , ((beforeExcelDate - startExcelDate)/1000L) + " second" );
        }

        //导出数据以后将数据删除
        Sort begin = new Sort(Sort.Direction.ASC, queryStr);
        Query query = new Query();
        //查询任务是否存在，存在就删除数据库数据
        TaskInfo taskInfo = taskInfoDao.selectById(export.getTaskId());
        if (!ObjectUtils.isEmpty(taskInfo)) {
            Long startDeleteDate = System.currentTimeMillis();
            log.info("dump data delete data : begin delete data" );
            //删除转储数据库数据
            this.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
            log.info("dump data delete data : end delete data {}" , ((System.currentTimeMillis() - startDeleteDate)/1000L) + " second" );
        } else {
            Long startDeleteDate = System.currentTimeMillis();
            log.info("dump data delete failure file : begin delete file" );
            //删除文件信息
            ExcelListUtil.deleteDir(new File(taskFolderPath));
            log.info("dump data delete failure file : end delete file {}" , ((System.currentTimeMillis() - startDeleteDate)/1000L) + " second" );
        }
    }


    /**
     * 删除告警查询数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 11:17
     * @param alarmHistoryList 历史告警编号集合
     */
    public void deleteElasticSearchInfo(List<String> alarmHistoryList) {
        //删除历史告警在elasticsearch上的数据
        List<String> infoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(alarmHistoryList)) {
            for (int i = 0; i < alarmHistoryList.size(); i++) {
                infoList.add(alarmHistoryList.get(i));
                if (infoList.size() >= 10000 || i == alarmHistoryList.size() - 1) {
                    //有一万个数据调用es方法删除数据
                    elasticSearchService.deleteIndex(infoList);
                    infoList = new ArrayList<>();
                }
            }
        }
    }

    /**
     * 删除转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 13:10
     * @param dumpNum 转储数据数量
     * @param query 查询对象
     * @param mongoTemplate mongo查询对象
     * @param,begin 查询排序条件
     * @param clazz 查询类
     * @param queryStr 排序字段
     */
    public void deleteDumpInfo(int dumpNum, Query query, MongoTemplate mongoTemplate, Sort begin, Class clazz, String queryStr) {
        int diffDumpNum = dumpNum;
        while (diffDumpNum >  0) {
            if (diffDumpNum >= listExcelSize) {
                query = new Query().with(begin).limit(listExcelSize);
                diffDumpNum = diffDumpNum - listExcelSize;
            } else {
                query = new Query().with(begin).limit(diffDumpNum);
                diffDumpNum = diffDumpNum - diffDumpNum;
            }
            this.removeInfo(query, queryStr, clazz, mongoTemplate);
        }
    }

    /**
     * 删除数据
     * @author hedongwei@wistronits.com
     * @param query 查询对象
     * @param queryStr 排序字段
     * @param clazz 对应类对象
     * @param mongoTemplate 对应的mongo模板
     * @date  2019/8/21 9:50
     */
    public void removeInfo(Query query, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
        BasicDBObject dbObject = new BasicDBObject();
        //操作日志名称
        String operateLogName = OperateLog.class.getName();
        //安全日志名称
        String securityLogName = SecurityLog.class.getName();
        //系统日志名称
        String systemLogName = SystemLog.class.getName();
        //历史告警类名称
        String alarmName = AlarmHistory.class.getName();
        //设施日志类名称
        String deviceLogName = DeviceLog.class.getName();
        String collectionName = "";
        String className = clazz.getName();
        boolean isIdIsString = false;
        if (operateLogName.equals(className)) {
            //操作日志查询数据
            collectionName = DumpConstant.OPERATE_LOG_COLLECTION_NAME;
        } else if (securityLogName.equals(className)) {
            //安全日志查询数据
            collectionName = DumpConstant.SECURITY_LOG_COLLECTION_NAME;
        } else if (systemLogName.equals(className)) {
            //系统日志查询数据
            collectionName = DumpConstant.SYSTEM_LOG_COLLECTION_NAME;
        } else if (alarmName.equals(className)) {
            //历史告警查询数据
            isIdIsString = true;
            collectionName = DumpConstant.ALARM_HISTORY_COLLECTION_NAME;
        } else if (deviceLogName.equals(className)) {
            //设施日志查询数据
            isIdIsString = true;
            collectionName = DumpConstant.DEVICE_LOG_COLLECTION_NAME;
        }
        DBCursor dbObjectInfo = mongoTemplate.getCollection(collectionName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).limit(query.getLimit());
        if (isIdIsString) {
            List<String> dataList = this.getDBCursorUUID(dbObjectInfo);
            //只有告警服务才能删除elasticsearch上的数据
            if (alarmName.equals(className)) {
                //历史告警删除es数据
                this.deleteElasticSearchInfo(dataList);
            }
            this.dataRemoveMethod(dataList, mongoTemplate, clazz, collectionName);
        } else {
            List<ObjectId> dataList = this.getDBCursorId(dbObjectInfo);
            this.dataRemoveMethod(dataList, mongoTemplate, clazz, collectionName);
        }
    }


    /**
     * 拼装删除数据的条件，并删除数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 13:19
     * @param dataList 数据编号集合
     * @param mongoTemplate mongo模板
     * @param clazz 类
     * @param collectionName 集合名称
     */
    public <T> void dataRemoveMethod(List<T> dataList, MongoTemplate mongoTemplate, Class clazz, String collectionName) {
        List<T> dataRemoveList = new ArrayList<>();
        Query queryRemove;
        if (!ObjectUtils.isEmpty(dataList)) {
            for (int i = 0 ; i < dataList.size(); i++) {
                dataRemoveList.add(dataList.get(i));
                if (dataRemoveList.size() >= 100 || i == dataList.size() - 1) {
                    queryRemove = new Query();
                    Criteria criteria = Criteria.where("_id").in(dataRemoveList);
                    queryRemove.addCriteria(criteria);
                    dumpService.removeInfo(queryRemove, mongoTemplate, clazz, collectionName);
                    dataRemoveList = new ArrayList<>();
                }
            }
        }
    }


    /**
     * 新增转储日志
     * @author hedongwei@wistronits.com
     * @date  2019/8/19 15:03
     * @param trigger 操作类型
     * @param clazz 操作类
     */
    public void addLogForDump(String trigger, Class clazz) {
        systemLanguageUtil.querySystemLanguage();
        String logType;
        if (DumpConstant.DUMP_TRIGGER_JOB.equals(trigger)) {
            logType = LogConstants.LOG_TYPE_SYSTEM;
        } else {
            logType = LogConstants.LOG_TYPE_OPERATE;
        }
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        //操作为转储
        addLogBean.setDataOptType("dump");
        addLogBean.setDataName("dumpName");
        //操作日志名称
        String operateLogName = OperateLog.class.getName();
        //安全日志名称
        String securityLogName = SecurityLog.class.getName();
        //系统日志名称
        String systemLogName = SystemLog.class.getName();
        //历史告警类名称
        String alarmName = AlarmHistory.class.getName();
        //设施日志类名称
        String deviceLogName = DeviceLog.class.getName();
        String className = clazz.getName();
        if (operateLogName.equals(className)) {
            addLogBean.setDataId("operateLogDump");
            addLogBean.setOptObjId("operateLogDump");
            addLogBean.setOptObj(systemLanguageUtil.getI18nString(ExportI18nConstant.OPERATE_LOG));
            addLogBean.setFunctionCode(DumpFunctionCodeConstant.DUMP_OPERATE_FUNCTION_CODE);
        } else if (securityLogName.equals(className)) {
            addLogBean.setDataId("securityLogDump");
            addLogBean.setOptObjId("securityLogDump");
            addLogBean.setOptObj(systemLanguageUtil.getI18nString(ExportI18nConstant.SECURITY_LOG));
            addLogBean.setFunctionCode(DumpFunctionCodeConstant.DUMP_SECURITY_LOG_FUNCTION_CODE);
        } else if (systemLogName.equals(className)) {
            addLogBean.setDataId("systemLogDump");
            addLogBean.setOptObjId("systemLogDump");
            addLogBean.setOptObj(systemLanguageUtil.getI18nString(ExportI18nConstant.SYSTEM_LOG));
            addLogBean.setFunctionCode(DumpFunctionCodeConstant.DUMP_SYSTEM_LOG_FUNCTION_CODE);
        } else if (alarmName.equals(className)) {
            addLogBean.setDataId("alarmDump");
            addLogBean.setOptObjId("alarmDump");
            addLogBean.setOptObj(systemLanguageUtil.getI18nString(ExportI18nConstant.ALARM));
            addLogBean.setFunctionCode(DumpFunctionCodeConstant.DUMP_ALARM_FUNCTION_CODE);
        } else if (deviceLogName.equals(className)) {
            addLogBean.setDataId("deviceLogDump");
            addLogBean.setOptObjId("deviceLogDump");
            addLogBean.setOptObj(systemLanguageUtil.getI18nString(ExportI18nConstant.DEVICE_LOG));
            addLogBean.setFunctionCode(DumpFunctionCodeConstant.DUMP_DEVICE_LOG_FUNCTION_CODE);
        }
        if (DumpConstant.DUMP_TRIGGER_JOB.equals(trigger)) {
            //新增系统日志
            logProcess.addSystemLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        } else {
            //新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }


    public void startExport(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
        try {
            specificExportData(export, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
        } catch (Exception e) {
            log.error("start export dump data error", e);
        }
        updateTaskInfo(export, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
    }

    /**
     * 获取查询转储的mongo参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 11:01
     * @param export 导出对象
     * @param dumpNum 导出数量
     * @param  pageNum 分页数量
     * @param  queryStr 查询字段
     * @param clazz 查询类
     * @param mongoTemplate mongo查询对象
     * @return 返回query对象
     */
    public Query getExportQuery(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
        Query query = new Query();
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setBizCondition(new Object());
        List<FilterCondition> filterConditionList = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditionList);
        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField(queryStr);
        sortCondition.setSortRule("asc");
        queryCondition.setSortCondition(sortCondition);
        //分页条件
        int pageNumber = pageNum > 0 ? pageNum - 1 : 0;
        int pageSize = 0;
        //查询条件
        if (dumpNum < listExcelSize) {
            pageSize = dumpNum;
        } else {
            pageSize = listExcelSize;
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        query = query.with(pageable);
        query = MongoQueryHelper.buildQuery(query, queryCondition);
        return query;
    }

    /**
     * 具体导出数据
     *
     * @param
     */
    private void specificExportData(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) throws Exception {
        //获取查询转储的mongo参数
        Query query = this.getExportQuery(export, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
        List<Object> listInfo = this.queryDumpDataToListObject(query, clazz.getName(), queryStr);
        ExportApiUtils.handelExportDto(export, listInfo);
        List<List<String>> dataList = export.getDataList();
        List<String> columnNameList = dataList.get(0);
        dataList.remove(0);
        List<List<String>> valueList = dataList;
        String excelFolderName = export.getExcelFolderName();
        //生成文件名称
        String fileName = excelFolderName + "-" + pageNum;
        String excelFolderPath = export.getTaskFolderPath() + "/" + excelFolderName + "/";
        if (export.getExcelType() == 0) {
            //将list文件分割成若干个小list，一个sheet最多能创建65535行
            ExcelListUtil.listExport(columnNameList, valueList, excelFolderPath, fileName);
        } else {
            ExcelListUtil.listExportToCsv(columnNameList, valueList, excelFolderPath, fileName);
        }
    }

    /**
     * 更新任务信息
     */
    private void updateTaskInfo(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
        export.setFileGeneratedNum(export.getFileGeneratedNum() + 1);
        ExportDto exportDto = new ExportDto();
        BeanUtils.copyProperties(export, exportDto);
        Boolean aBoolean = updateTaskFileNumById(exportDto);
        //如果任务停止了 直接return
        if (aBoolean) {
            log.info("dump data generate file : task is failed to dump");
            return;
        }
        if (pageNum == (int) Math.ceil((double) export.getDateSize() / (double) listExcelSize)) {
            log.info("dump data generate file : task is successful to dump");
            String filePath = packageFile(export);
            exportDto.setFileGeneratedNum(export.getFileNum());
            exportDto.setFilePath(filePath);
            //如果更新返回值为true 即更新成功
            //将最原始对象文件数与生成数设置为相等，通知切面此任务已经完成
            export.setFileGeneratedNum(export.getFileNum());
            updateTaskFileNumById(exportDto);
        } else {
            dumpNum = dumpNum - listExcelSize;
            pageNum++;
            startExport(export, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
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
            if (!ObjectUtils.isEmpty(ftpSettings)) {
                //ftp上传文件
                try {
                    log.info("dump data upload file to ftp : begin ftp upload");
                    Long nowDate = System.currentTimeMillis();
                    boolean result = FtpUtil.uploadFile(ftpSettings.getInnerIpAddress(), ftpSettings.getUserName(), ftpSettings.getPassword(), ftpSettings.getPort(),
                            ExportApiConstant.FILE_PATH, file.getName(), inputStream, 100);
                    Long diffDate = (System.currentTimeMillis() - nowDate) /1000L;
                    log.info("dump data upload file to ftp : begin ftp upload the time :" + diffDate);
                    if (result) {
                        //上传成功后删除zip文件
                        ExcelListUtil.deleteDir(new File(taskFolderPath));
                    } else {
                        ExcelListUtil.deleteDir(new File(excelFolderPath));
                    }
                } catch (Exception e) {
                    log.error("dump data upload file to ftp : upload ftp failed", e);
                }
            } else {
                ExcelListUtil.deleteDir(new File(excelFolderPath));
            }
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
        //存入操作用户
        exportDto.setUserId(null);
        exportDto.setListName(listName);
        PageCondition pageCondition = queryCondition.getPageCondition();
        pageCondition.setPageSize(listExcelSize);
        pageCondition.setPageNum(1);
        // 查询count等信息  构造任务数据 queryCount可以修改方法 ，
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
        if (code == DumpResultCode.TASK_NUM_TOO_BIG_CODE) {
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
        export.setTaskId((String) result.getData());
        return export;
    }

    @Override
    public Result queryDumpData(DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime, String listName) {

        DumpBean dumpBean = dumpFeign.queryDump(dumpType.getType());

        //判断是否开启转储
        if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {

            //如果为按条数执行
            if (ExportApiConstant.DUMP_DATA_BY_NUMBER.equals(dumpBean.getTriggerCondition())) {
                long count = mongoTemplate.count(new Query(), clazz);
                log.info("dump data by number : select dump data sum count = {}" , count);
                //查询告警是否达到转储的条件
                String dumpQuantityThreshold = dumpBean.getDumpQuantityThreshold();
                log.info("dump data by number : setting dump data count = {}" , dumpQuantityThreshold);
                if (count >= Long.parseLong(dumpQuantityThreshold)) {

                    int dumpNum = Integer.parseInt(dumpBean.getTurnOutNumber());
                    Sort beginTime = new Sort(Sort.Direction.DESC, createTime);
                    Query query = new Query().with(beginTime).limit(dumpNum);
                    List list = mongoTemplate.find(query, clazz);

                    String dumpPlace = dumpBean.getDumpPlace();
                    DumpDto dumpDto = new DumpDto(list, dumpNum, dumpPlace, query, clazz);
                    return ResultUtils.success(dumpDto);
                }
                return ResultUtils.success(DumpResultCode.NOT_ENOUGH_DUMP_DATA,
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
                List<String> idList = taskInfoDao.queryTaskByCreateTime(type, beginTime, listName);
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
                    return ResultUtils.success(DumpResultCode.NOT_ENOUGH_DUMP_DATA,
                            I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
                }
                return ResultUtils.success(DumpResultCode.SPECIFIED_TIME_HAS_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.SPECIFIED_TIME_HAS_DUMP_DATA));
            }
        }

        //不符合转储条件
        return ResultUtils.success(DumpResultCode.DISABLE_DUMP_DATA,
                I18nUtils.getSystemString(ExportI18nConstant.DISABLE_DUMP_DATA));
    }

    /**
     * 获取被转储操作日志的数据信息
     *
     * @param dumpType      转储类型
     * @param mongoTemplate mongo数据库
     * @param clazz         转储的数据类型
     * @param createTime    排序的字段
     * @param listName      列表名称
     * @param trigger       执行的时机
     * @return
     */
    @Override
    public Result queryDumpDataInfo(DumpBean dumpBean, DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime, String listName, String trigger) {
        //判断是否开启转储
        if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {

            //如果为按条数执行
            if (ExportApiConstant.DUMP_DATA_BY_NUMBER.equals(dumpBean.getTriggerCondition())) {
                long count = mongoTemplate.count(new Query(), clazz);
                log.info("dump data by number : select dump data sum count = {}" , count);
                //查询告警是否达到转储的条件
                String dumpQuantityThreshold = dumpBean.getDumpQuantityThreshold();
                log.info("dump data by number : setting dump data count = {}" , dumpQuantityThreshold);
                if (count > 0 && count >= Long.parseLong(dumpQuantityThreshold)) {
                    DumpDto dumpDto = null;
                    return ResultUtils.success(dumpDto);
                }
                return ResultUtils.warn(DumpResultCode.NOT_ENOUGH_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
            }

            //按照月份来查询数据,先设定每月的1号为自动执行的日期
            if (ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition())) {

                String monthString = dumpBean.getDumpInterval();
                int month = Integer.parseInt(monthString);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1 * month);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long beginTime = calendar.getTimeInMillis();
                //如果在开始时间之后有进行导出任务，则不进行数据存储
                String type = dumpType.getType();
                //定时任务执行时才判断有没有执行过转储数据
                if (DumpConstant.DUMP_TRIGGER_JOB.equals(trigger)) {
                    int monthParam = 0;
                    int dayHour = 0;
                    Long nowDateTime = System.currentTimeMillis();
                    Long getMonthOneDayTime = CastDateUtil.getMonthOneDay(monthParam, nowDateTime, dayHour);
                    if (nowDateTime < getMonthOneDayTime) {
                        //未到达转储时间
                        return ResultUtils.warn(DumpResultCode.DUMP_TIME_NOT_REACHED,
                                I18nUtils.getSystemString(ExportI18nConstant.DUMP_TIME_NOT_REACHED));
                    }
                    List<String> idList = taskInfoDao.queryTaskByCreateTime(type, beginTime, listName);
                    if (!ObjectUtils.isEmpty(idList)) {
                        return ResultUtils.warn(DumpResultCode.SPECIFIED_TIME_HAS_DUMP_DATA,
                                I18nUtils.getSystemString(ExportI18nConstant.SPECIFIED_TIME_HAS_DUMP_DATA));
                    }
                }
                long count = mongoTemplate.count(new Query(), clazz);
                log.info("dump data by scheduled : select scheduled task dump data sum count = {}" , count);

                //查询是否达到被转储的数量
                String dumpNumString = dumpBean.getTurnOutNumber();
                log.info("dump data by scheduled : scheduled task setting dump data count = {}" , count);
                if (count > 0 && count >= Long.parseLong(dumpNumString)) {
                    DumpDto dumpDto = null;
                    return ResultUtils.success(dumpDto);
                }
                return ResultUtils.warn(DumpResultCode.NOT_ENOUGH_DUMP_DATA,
                        I18nUtils.getSystemString(ExportI18nConstant.NOT_ENOUGH_DUMP_DATA));
            }
        }

        //不符合转储条件
        return ResultUtils.warn(DumpResultCode.DISABLE_DUMP_DATA,
                I18nUtils.getSystemString(ExportI18nConstant.DISABLE_DUMP_DATA));
    }


    @Override
    public Result dumpOperaLogData(@RequestBody DumpData dumpData, String trigger) {

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
        Result dumpDataResult = queryDumpDataInfo(dumpBean, dumpType, mongoTemplate, clazz, queryStr, dumpData.getListName(), trigger);
        if (dumpDataResult.getCode() == ResultCode.SUCCESS) {
            dumpService.searchDumpData(dumpBean, dumpData, exportDto, trigger);
            //插入日志信息
            this.addLogForDump(trigger, clazz);
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(ExportI18nConstant.DUMP_DATA_SUCCESS));
        }
        return dumpDataResult;
    }



    @Override
    public Result queryDumpInfoById(String taskId) {
        TaskInfo taskInfo = taskInfoDao.selectById(taskId);
        if (taskInfo == null || TaskConstant.DELETED.equals(taskInfo.getIsDeleted())) {
            return ResultUtils.warn(DumpResultCode.TASK_DOES_NOT_EXIST, I18nUtils.getSystemString(ExportI18nConstant.TASK_DOES_NOT_EXIST));
        }
        Long dumpNumber = taskInfoDao.queryTotalTaskNum(taskInfo.getTaskInfoType());
        List<TaskInfo> taskInfos = taskInfoDao.queryTaskInfosByType(taskInfo.getTaskInfoType());
        int fileNum = 0;
        int fileGeneratedNum = 0;
        for (TaskInfo taskInfo1 : taskInfos) {
            fileNum += taskInfo1.getFileNum();
            fileGeneratedNum += taskInfo1.getFileGeneratedNum();
        }
        taskInfo.setFileNum(fileNum);
        taskInfo.setFileGeneratedNum(fileGeneratedNum);
        taskInfo.setDumpAllNumber(dumpNumber);
        return ResultUtils.success(taskInfo);
    }

    @Override
    public Result dumpData(Integer dumpType, String trigger) {
        //告警
        if (ExportApiConstant.ALARM_LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            //获取告警参数
            DumpData dumpData = this.alarmDumpDataParam();
            deleteTaskByType(dumpData);
            if (ExportApiConstant.ALARM_LOG_TYPE.equals(dumpType)) {
                return dumpOperaLogData(dumpData, trigger);
            } else {
                dumpOperaLogData(dumpData, trigger);
            }
        }
        //导出日志
        if (ExportApiConstant.LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            String operateLogName = I18nUtils.getSystemString(ExportI18nConstant.OPERATE_LOG);
            String securityLogName = I18nUtils.getSystemString(ExportI18nConstant.SECURITY_LOG);
            String systemLogName = I18nUtils.getSystemString(ExportI18nConstant.SYSTEM_LOG);
            String msg = "";
            //操作日志
            DumpData dumpOperaLogData = this.operateDumpDataParam();
            deleteTaskByType(dumpOperaLogData);
            int failedLogNumber = 0;
            Result operaLogDataResult = dumpOperaLogData(dumpOperaLogData, trigger);
            if (operaLogDataResult.getCode() != ResultCode.SUCCESS) {
                failedLogNumber ++;
                msg += operateLogName + ",";
                msg += operaLogDataResult.getMsg();
            }
            //系统日志
            DumpData dumpSysLogData = this.systemDumpDataParam();
            Result sysLogDataResult = dumpOperaLogData(dumpSysLogData, trigger);
            if (sysLogDataResult.getCode() != ResultCode.SUCCESS) {
                failedLogNumber ++;
                msg += "\n\r" + systemLogName + ",";
                msg += sysLogDataResult.getMsg();
            }
            //安全日志
            DumpData dumpSecLogData = this.securityDumpDataParam();
            Result securityLogDataResult = dumpOperaLogData(dumpSecLogData, trigger);
            if (securityLogDataResult.getCode() != ResultCode.SUCCESS) {
                failedLogNumber ++;
                msg += "\n\r" + securityLogName + ",";
                msg += securityLogDataResult.getMsg();
            }
            int allLogNumber = 3;
            if (allLogNumber != failedLogNumber) {
                msg = "";
            }
            Result result = this.getDumpResult(dumpType, msg);
            if (!ObjectUtils.isEmpty(result)) {
                return result;
            }
        }
        //设施日志
        if (ExportApiConstant.DEVICE_LOG_TYPE.equals(dumpType) || ExportApiConstant.ALL_LOG_TYPE.equals(dumpType)) {
            DumpData dumpDeviceLogData = this.deviceLogDumpDataParam();
            deleteTaskByType(dumpDeviceLogData);
            return dumpOperaLogData(dumpDeviceLogData, trigger);
        }
        return ResultUtils.warn(ResultCode.FAIL);
    }

    /**
     * 转储告警参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 15:06
     * @return 返回转储数据类
     */
    public DumpData alarmDumpDataParam() {
        DumpData dumpData = new DumpData();
        dumpData.setClazz(AlarmHistory.class);
        dumpData.setDumpType(DumpType.ALARM_LOG_DUMP);
        dumpData.setMongoTemplate(alarmTemplate);
        dumpData.setQueryStr(ExportApiConstant.ALARM_QUERY);
        dumpData.setListName(ExportApiConstant.ALARM_LIST_NAME);
        return dumpData;
    }

    /**
     * 转储操作日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 15:06
     * @return 返回转储数据类
     */
    public DumpData operateDumpDataParam() {
        DumpData dumpOperaLogData = new DumpData();
        dumpOperaLogData.setClazz(OperateLog.class);
        dumpOperaLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
        dumpOperaLogData.setMongoTemplate(filikLog);
        dumpOperaLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
        dumpOperaLogData.setListName(ExportApiConstant.OPERA_LOG_LIST_NAME);
        return dumpOperaLogData;
    }

    /**
     * 转储系统日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 15:06
     * @return 返回转储数据类
     */
    public DumpData systemDumpDataParam() {
        DumpData dumpSysLogData = new DumpData();
        dumpSysLogData.setClazz(SystemLog.class);
        dumpSysLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
        dumpSysLogData.setMongoTemplate(filikLog);
        dumpSysLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
        dumpSysLogData.setListName(ExportApiConstant.SYS_LOG_LIST_NAME);
        return dumpSysLogData;
    }

    /**
     * 转储安全日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 15:06
     * @return 返回转储数据类
     */
    public DumpData securityDumpDataParam() {
        DumpData dumpSecLogData = new DumpData();
        dumpSecLogData.setClazz(SecurityLog.class);
        dumpSecLogData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
        dumpSecLogData.setMongoTemplate(filikLog);
        dumpSecLogData.setQueryStr(ExportApiConstant.LOG_QUERY);
        dumpSecLogData.setListName(ExportApiConstant.SEC_LOG_LIST_NAME);
        return dumpSecLogData;
    }

    /**
     * 转储设施日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 15:06
     * @return 返回转储数据类
     */
    public DumpData deviceLogDumpDataParam() {
        DumpData dumpDeviceLogData = new DumpData();
        dumpDeviceLogData.setClazz(DeviceLog.class);
        dumpDeviceLogData.setDumpType(DumpType.DEVICE_LOG_DUMP);
        dumpDeviceLogData.setMongoTemplate(filinkDevice);
        dumpDeviceLogData.setQueryStr(ExportApiConstant.DEVICE_LOG_QUERY);
        dumpDeviceLogData.setListName(ExportApiConstant.DEVICE_LOG_LIST_NAME);
        return dumpDeviceLogData;
    }

    /**
     * 获取转储返回值
     *
     * @param dumpType 转储类别
     * @param msg      返回消息
     * @return 返回转储返回值
     * @author hedongwei@wistronits.com
     * @date 2019/7/8 17:27
     */
    public Result getDumpResult(Integer dumpType, String msg) {
        if (ExportApiConstant.LOG_TYPE.equals(dumpType) && !ObjectUtils.isEmpty(msg)) {
            return ResultUtils.warn(DumpResultCode.ERROR_CODE, msg);
        } else if (ExportApiConstant.LOG_TYPE.equals(dumpType) && ObjectUtils.isEmpty(msg)) {
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(ExportI18nConstant.DUMP_DATA_SUCCESS));
        }
        return null;
    }

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 13:19
     * @param dumpType 转储类型
     * @return 返回数据结果
     */
    @Override
    public Result queryDumpInfo(String dumpType) {

        TaskInfo taskInfo = taskInfoDao.queryLastTaskInfo(dumpType);
        Long dumpNumber = taskInfoDao.queryTotalTaskNum(dumpType);
        //查询系统配置的值
        DumpBean dumpBean = dumpFeign.queryDump(dumpType);
        ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition());
        Long nowDateTime = System.currentTimeMillis();
        if (taskInfo != null) {
            taskInfo.setDumpAllNumber(dumpNumber);
        } else {
            taskInfo = new TaskInfo();
            taskInfo.setDumpAllNumber(0L);
            taskInfo.setFileGeneratedNum(0);
            taskInfo.setFileNum(0);
            //转储状态为未执行
            taskInfo.setTaskStatus(TaskStatusConstant.NON_EXECUTION);
        }
        if (!ObjectUtils.isEmpty(dumpBean)) {
            if (ExportApiConstant.ENABLE_DUMP_DATA.equals(dumpBean.getEnableDump())) {
                //启用才有下次执行时间
                if (ExportApiConstant.DUMP_DATA_BY_MONTH.equals(dumpBean.getTriggerCondition())) {
                    int month = 0;
                    int hour = DumpConstant.DUMP_TASK_EXEC_HOUR;
                    Long dateTime = 0L;
                    Long nextExecutionTime = this.getNextMonthTime(month, hour, dateTime, taskInfo, dumpBean, nowDateTime);
                    taskInfo.setNextExecutionTime(nextExecutionTime);
                } /*else {
                    int day = 0;
                    int hour = DumpConstant.DUMP_TASK_EXEC_HOUR;
                    Long date = CastDateUtil.getNowDayTimeAdd(day, hour);
                    if (date >= nowDateTime) {
                        //现在的时间小于当天定时任务的执行时间 下次任务的执行时间为今天定时任务执行的时间
                        taskInfo.setNextExecutionTime(date);
                    } else {
                        //现在的时间大于当天定时任务的执行时间 下次任务的执行时间为明天定时任务执行的时间
                        day = 1;
                        date = CastDateUtil.getNowDayTimeAdd(day, hour);
                        taskInfo.setNextExecutionTime(date);
                    }
                }*/
            }
        }
        //如果没有任务信息，返回为空
        return ResultUtils.success(taskInfo);
    }


    /**
     * 获取下次任务执行时间
     * @author hedongwei@wistronits.com
     * @date  2019/8/22 23:22
     * @param month 月份
     * @param hour 小时数
     * @param dateTime 时间
     * @param taskInfo 任务类
     * @param dumpBean 转储类
     * @param nowDateTime 当前时间
     * @return 获取下次任务执行时间
     */
    public long getNextMonthTime(int month, int hour, Long dateTime, TaskInfo taskInfo, DumpBean dumpBean, Long nowDateTime) {
        if (!ObjectUtils.isEmpty(taskInfo)) {
            dateTime = taskInfo.getCreateTime();
            //当月1日晚上一点的时间
            Long localMonthExecTime = CastDateUtil.getMonthOneDay(month, dateTime, hour);
            Long nextMonthExecTime;
            //最后一次任务的执行时间的1日晚上一点的时间大于等于最后一次任务的执行时间
            if (localMonthExecTime >= dateTime) {
                //则下次任务执行的时间为最后一次任务的执行时间加上周期的月份，并取得该月1日晚上一点的时间
                month = Integer.parseInt(dumpBean.getDumpInterval());
                nextMonthExecTime = CastDateUtil.getMonthOneDay(month, dateTime, hour);
                return nextMonthExecTime;
            } else {
                //则下次任务执行的时间为最后一次任务的执行时间加上周期的月份，并取得该月1日晚上一点的时间
                month = Integer.parseInt(dumpBean.getDumpInterval()) + 1;
                nextMonthExecTime = CastDateUtil.getMonthOneDay(month, dateTime, hour);
                return nextMonthExecTime;
            }
        } else {
            dateTime = nowDateTime;
            //当月1日晚上一点的时间
            Long localMonthExecTime = CastDateUtil.getMonthOneDay(month, dateTime, hour);
            //最后一次任务的执行时间的1日晚上一点的时间大于等于最后一次任务的执行时间
            if (localMonthExecTime >= dateTime) {
                //则下次任务的执行时间为当前月1日晚上一点
                return localMonthExecTime;
            } else {
                //则下次任务的执行时间为下个月1日晚上一点
                month = 1;
                Long nextMonthExecTime = CastDateUtil.getMonthOneDay(month, dateTime, hour);
                return nextMonthExecTime;
            }
        }

    }

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 12:22
     * @param query 查询数据
     * @param className 类名称
     * @param queryStr 查询字段
     * @return 转储数据
     */
    @Override
    public List<Object> queryDumpDataToListObject(Query query, String className, String queryStr) {
        //操作日志名称
        String operateLogName = OperateLog.class.getName();
        //安全日志名称
        String securityLogName = SecurityLog.class.getName();
        //系统日志名称
        String systemLogName = SystemLog.class.getName();
        //历史告警类名称
        String alarmName = AlarmHistory.class.getName();
        //设施日志类名称
        String deviceLogName = DeviceLog.class.getName();
        //查询转储的数据
        List<Object> listInfo = new ArrayList<>();
        int skipNumber = query.getSkip();
        int limit = query.getLimit();
        BasicDBObject dbObject = new BasicDBObject();
        if (operateLogName.equals(className)) {
            //操作日志查询数据
            String collectName = DumpConstant.OPERATE_LOG_COLLECTION_NAME;
            DBCursor dbObjectInfo = filikLog.getCollection(collectName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).skip(skipNumber).limit(limit);
            List<OperateLog> operateLogList = this.operateLogList(dbObjectInfo);
            if (!ObjectUtils.isEmpty(operateLogList)) {
                listInfo.addAll(operateLogList);
            }
        } else if (securityLogName.equals(className)) {
            //安全日志查询数据
            String collectName = DumpConstant.SECURITY_LOG_COLLECTION_NAME;
            DBCursor dbObjectInfo = filikLog.getCollection(collectName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).skip(skipNumber).limit(limit);
            List<SecurityLog> securityLogList = this.securityLogList(dbObjectInfo);
            if (!ObjectUtils.isEmpty(securityLogList)) {
                listInfo.addAll(securityLogList);
            }
        } else if (systemLogName.equals(className)) {
            //系统日志查询数据
            String collectName = DumpConstant.SYSTEM_LOG_COLLECTION_NAME;
            DBCursor dbObjectInfo = filikLog.getCollection(collectName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).skip(skipNumber).limit(limit);
            List<SystemLog> systemLogList = this.systemLogList(dbObjectInfo);
            if (!ObjectUtils.isEmpty(systemLogList)) {
                listInfo.addAll(systemLogList);
            }
        } else if (alarmName.equals(className)) {
            //历史告警查询数据
            String collectName = DumpConstant.ALARM_HISTORY_COLLECTION_NAME;
            DBCursor dbObjectInfo = alarmTemplate.getCollection(collectName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).skip(skipNumber).limit(limit);
            List<AlarmHistory> alarmHistoryList = this.alarmHistoryList(dbObjectInfo);
            if (!ObjectUtils.isEmpty(alarmHistoryList)) {
                listInfo.addAll(alarmHistoryList);
            }
        } else if (deviceLogName.equals(className)) {
            //设施日志查询数据
            String collectName = DumpConstant.DEVICE_LOG_COLLECTION_NAME;
            DBCursor dbObjectInfo = filinkDevice.getCollection(collectName).find(dbObject).sort(new BasicDBObject(queryStr, 1)).skip(skipNumber).limit(limit);
            List<DeviceLog> deviceLogList = this.deviceLogList(dbObjectInfo);
            if (!ObjectUtils.isEmpty(deviceLogList)) {
                listInfo.addAll(deviceLogList);
            }
        }
        return listInfo;
    }

    /**
     * 删除数据库中正在执行的任务
     *
     * @param dumpData
     */
    private void deleteTaskByType(DumpData dumpData) {
        List<TaskInfo> taskInfos = taskInfoDao.queryNoCompleteTaskInfosByType(dumpData.getDumpType().getType());
        if (taskInfos == null || taskInfos.size() == 0) {
            return;
        }
        List<String> taskIdList = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfos) {
            String taskInfoId = taskInfo.getTaskInfoId();
            //删除缓存中的标志
            String key = "task" + taskInfoId;
            RedisUtils.remove(key);
            taskIdList.add(taskInfoId);
        }
        //此处为直接删除
        taskInfoDao.deleteBatchIds(taskIdList);
    }

    /**
     * 获取id编号
     * @author hedongwei@wistronits.com
     * @date  2019/8/20 14:44
     * @param dbObjectInfo 查询出来结果的值
     */
    public List<ObjectId> getDBCursorId(DBCursor dbObjectInfo) {
        List<ObjectId> idList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            idList.add(dbObjectOne.get("_id") != null ? (ObjectId) dbObjectOne.get("_id") : null);
        }
        return idList;
    }

    /**
     * 获取id编号
     * @author hedongwei@wistronits.com
     * @date  2019/8/20 14:44
     * @param dbObjectInfo 查询出来结果的值
     */
    public List<String> getDBCursorUUID(DBCursor dbObjectInfo) {
        List<String> idList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            idList.add(dbObjectOne.get("_id") != null ? dbObjectOne.get("_id") + "" : "");
        }
        return idList;
    }

    /**
     * 获取操作日志集合
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 15:06
     * @param dbObjectInfo 数据对象
     * @return 操作日志集合
     */
    public List<OperateLog> operateLogList(DBCursor dbObjectInfo) {
        List<OperateLog> operateLogList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            OperateLog operateLog = new OperateLog();
            operateLog.setId(dbObjectOne.get("_id") != null ? (ObjectId) dbObjectOne.get("_id") : null);
            operateLog.setLogId(dbObjectOne.get("logId") != null ? dbObjectOne.get("logId") + "" : "");
            operateLog.setOptName(dbObjectOne.get("optName") != null ? dbObjectOne.get("optName") + "" : "");
            operateLog.setOptType(dbObjectOne.get("optType") != null ? dbObjectOne.get("optType") + "" : "");
            operateLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            operateLog.setDangerLevel(dbObjectOne.get("dangerLevel") != null ? (int)dbObjectOne.get("dangerLevel") : null);
            operateLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            operateLog.setOptUserRole(dbObjectOne.get("optUserRole") != null ? dbObjectOne.get("optUserRole") + "" : "");
            operateLog.setOptUserRoleName(dbObjectOne.get("optUserRoleName") == null ? dbObjectOne.get("optUserRoleName") + "" : "");
            operateLog.setOptUserCode(dbObjectOne.get("optUserCode") != null ? dbObjectOne.get("optUserCode") + "" : "");
            operateLog.setOptUserName(dbObjectOne.get("optUserName") != null ? dbObjectOne.get("optUserName") + "" : "");
            operateLog.setOptTerminal(dbObjectOne.get("optTerminal") != null ? dbObjectOne.get("optTerminal") + "" : "");
            operateLog.setOptTime(dbObjectOne.get("optTime") != null ? Long.valueOf(dbObjectOne.get("optTime") + "") : null);
            operateLog.setOptObj(dbObjectOne.get("optObj") != null ? dbObjectOne.get("optObj") + "" : "");
            operateLog.setOptObjId(dbObjectOne.get("optObjId") != null ? dbObjectOne.get("optObjId") + "" : "");
            operateLog.setOptResult(dbObjectOne.get("optResult") != null ? dbObjectOne.get("optResult") + "" : "");
            operateLog.setDetailInfo(dbObjectOne.get("detailInfo") != null ? dbObjectOne.get("detailInfo") + "" : "");
            operateLog.setRemark(dbObjectOne.get("remark") == null ? dbObjectOne.get("remark") + "" : "");
            operateLog.setCreateUser(dbObjectOne.get("createUser") != null ? dbObjectOne.get("createUser") + "" : "");
            operateLog.setCreateTime(dbObjectOne.get("createTime") != null ? Long.valueOf(dbObjectOne.get("createTime") + "") : null);
            operateLog.setUpdateUser(dbObjectOne.get("updateUser") != null ? dbObjectOne.get("updateUser") + "" : "");
            operateLog.setUpdateTime(dbObjectOne.get("updateTime") != null ? Long.valueOf(dbObjectOne.get("updateTime") + "") : null);
            operateLogList.add(operateLog);
        }
        return operateLogList;
    }


    /**
     * 获取安全日志集合
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 15:06
     * @param dbObjectInfo 数据对象
     * @return 操作日志集合
     */
    public List<SecurityLog> securityLogList(DBCursor dbObjectInfo) {
        List<SecurityLog> securityLogList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            SecurityLog securityLog = new SecurityLog();
            securityLog.setId(dbObjectOne.get("_id") != null ? (ObjectId) dbObjectOne.get("_id") : null);
            securityLog.setLogId(dbObjectOne.get("logId") != null ? dbObjectOne.get("logId") + "" : "");
            securityLog.setOptName(dbObjectOne.get("optName") != null ? dbObjectOne.get("optName") + "" : "");
            securityLog.setOptType(dbObjectOne.get("optType") != null ? dbObjectOne.get("optType") + "" : "");
            securityLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            securityLog.setDangerLevel(dbObjectOne.get("dangerLevel") != null ? (int)dbObjectOne.get("dangerLevel") : null);
            securityLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            securityLog.setOptUserRole(dbObjectOne.get("optUserRole") != null ? dbObjectOne.get("optUserRole") + "" : "");
            securityLog.setOptUserRoleName(dbObjectOne.get("optUserRoleName") != null ? dbObjectOne.get("optUserRoleName") + "" : "");
            securityLog.setOptUserCode(dbObjectOne.get("optUserCode") != null ? dbObjectOne.get("optUserCode") + "" : "");
            securityLog.setOptUserName(dbObjectOne.get("optUserName") != null ? dbObjectOne.get("optUserName") + "" : "");
            securityLog.setOptTerminal(dbObjectOne.get("optTerminal") != null ? dbObjectOne.get("optTerminal") + "" : "");
            securityLog.setOptTime(dbObjectOne.get("optTime") != null ? Long.valueOf(dbObjectOne.get("optTime") + "") : null);
            securityLog.setOptObj(dbObjectOne.get("optObj") != null ? dbObjectOne.get("optObj") + "" : "");
            securityLog.setOptObjId(dbObjectOne.get("optObjId") != null ? dbObjectOne.get("optObjId") + "" : "");
            securityLog.setOptResult(dbObjectOne.get("optResult") != null ? dbObjectOne.get("optResult") + "" : "");
            securityLog.setDetailInfo(dbObjectOne.get("detailInfo") != null ? dbObjectOne.get("detailInfo") + "" : "");
            securityLog.setRemark(dbObjectOne.get("remark") != null ? dbObjectOne.get("remark") + "" : "");
            securityLog.setCreateUser(dbObjectOne.get("createUser") != null ? dbObjectOne.get("createUser") + "" : "");
            securityLog.setCreateTime(dbObjectOne.get("createTime") != null ? Long.valueOf(dbObjectOne.get("createTime") + "") : null);
            securityLog.setUpdateUser(dbObjectOne.get("updateUser") != null ? dbObjectOne.get("updateUser") + "" : "");
            securityLog.setUpdateTime(dbObjectOne.get("updateTime") != null ? Long.valueOf(dbObjectOne.get("updateTime") + "") : null);
            securityLogList.add(securityLog);
        }
        return securityLogList;
    }



    /**
     * 获取系统日志集合
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 15:06
     * @param dbObjectInfo 数据对象
     * @return 系统日志集合
     */
    public List<SystemLog> systemLogList(DBCursor dbObjectInfo) {
        List<SystemLog> systemLogList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            SystemLog systemLog = new SystemLog();
            systemLog.setId(dbObjectOne.get("_id") != null ? (ObjectId) dbObjectOne.get("_id") : null);
            systemLog.setLogId(dbObjectOne.get("logId") != null ? dbObjectOne.get("logId") + "" : "");
            systemLog.setOptName(dbObjectOne.get("optName") != null ? dbObjectOne.get("optName") + "" : "");
            systemLog.setOptType(dbObjectOne.get("optType") != null ? dbObjectOne.get("optType") + "" : "");
            systemLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            systemLog.setDangerLevel(dbObjectOne.get("dangerLevel") != null ? (int)dbObjectOne.get("dangerLevel") : null);
            systemLog.setDataOptType(dbObjectOne.get("dataOptType") != null ? dbObjectOne.get("dataOptType") + "" : "");
            systemLog.setOptUserRole(dbObjectOne.get("optUserRole") != null ? dbObjectOne.get("optUserRole") + "" : "");
            systemLog.setOptUserRoleName(dbObjectOne.get("optUserRoleName") != null ? dbObjectOne.get("optUserRoleName") + "" : "");
            systemLog.setOptUserCode(dbObjectOne.get("optUserCode") != null ? dbObjectOne.get("optUserCode") + "" : "");
            systemLog.setOptUserName(dbObjectOne.get("optUserName") != null ? dbObjectOne.get("optUserName") + "" : "");
            systemLog.setOptTerminal(dbObjectOne.get("optTerminal") != null ? dbObjectOne.get("optTerminal") + "" : "");
            systemLog.setOptTime(dbObjectOne.get("optTime") != null ? Long.valueOf(dbObjectOne.get("optTime") + "") : null);
            systemLog.setOptObj(dbObjectOne.get("optObj") != null ? dbObjectOne.get("optObj") + "" : "");
            systemLog.setOptObjId(dbObjectOne.get("optObjId") != null ? dbObjectOne.get("optObjId") + "" : "");
            systemLog.setOptResult(dbObjectOne.get("optResult") != null ? dbObjectOne.get("optResult") + "" : "");
            systemLog.setDetailInfo(dbObjectOne.get("detailInfo") != null ? dbObjectOne.get("detailInfo") + "" : "");
            systemLog.setRemark(dbObjectOne.get("remark") != null ? dbObjectOne.get("remark") + "" : "");
            systemLog.setCreateUser(dbObjectOne.get("createUser") != null ? dbObjectOne.get("createUser") + "" : "");
            systemLog.setCreateTime(dbObjectOne.get("createTime") != null ? Long.valueOf(dbObjectOne.get("createTime") + "") : null);
            systemLog.setUpdateUser(dbObjectOne.get("updateUser") != null ? dbObjectOne.get("updateUser") + "" : "");
            systemLog.setUpdateTime(dbObjectOne.get("updateTime") != null ? Long.valueOf(dbObjectOne.get("updateTime") + "") : null);
            systemLogList.add(systemLog);
        }
        return systemLogList;
    }



    /**
     * 获取设施日志集合
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 15:06
     * @param dbObjectInfo 数据对象
     * @return 设施日志集合
     */
    public List<DeviceLog> deviceLogList(DBCursor dbObjectInfo) {
        List<DeviceLog> deviceLogList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setLogId(dbObjectOne.get("_id") != null ? dbObjectOne.get("_id") + "" : "");
            deviceLog.setType(dbObjectOne.get("type") != null ? dbObjectOne.get("type") + "" : "");
            deviceLog.setLogName(dbObjectOne.get("logName") != null ? dbObjectOne.get("logName") + "" : "");
            deviceLog.setLogType(dbObjectOne.get("logType") != null ? dbObjectOne.get("logType") + "" : "");
            deviceLog.setDeviceId(dbObjectOne.get("deviceId") != null ? dbObjectOne.get("deviceId") + "" : "");
            deviceLog.setDeviceType(dbObjectOne.get("deviceType") != null ? dbObjectOne.get("deviceType") + "" : "");
            deviceLog.setDeviceCode(dbObjectOne.get("deviceCode") != null ? dbObjectOne.get("deviceCode") + "" : "");
            deviceLog.setDeviceName(dbObjectOne.get("deviceName") != null ? dbObjectOne.get("deviceName") + "" : "");
            deviceLog.setNodeObject(dbObjectOne.get("nodeObject") != null ? dbObjectOne.get("nodeObject") + "" : "");
            deviceLog.setAreaId(dbObjectOne.get("areaId") != null ? dbObjectOne.get("areaId") + "" : "");
            deviceLog.setAreaName(dbObjectOne.get("areaName") != null ? dbObjectOne.get("areaName") + "" : "");
            deviceLog.setCurrentTime(dbObjectOne.get("currentTime") != null ? Long.valueOf(dbObjectOne.get("currentTime") + "") : null);
            deviceLog.setRemarks(dbObjectOne.get("remarks") != null ? dbObjectOne.get("remarks") + "" : "");
            deviceLogList.add(deviceLog);
        }
        return deviceLogList;
    }

    /**
     * 获取历史告警集合
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 15:06
     * @param dbObjectInfo 数据对象
     * @return 历史集合
     */
    public List<AlarmHistory> alarmHistoryList(DBCursor dbObjectInfo) {
        List<AlarmHistory> alarmHistoryList = new ArrayList<>();
        while (dbObjectInfo.hasNext()) {
            DBObject dbObjectOne = dbObjectInfo.next();
            AlarmHistory alarmHistory = new AlarmHistory();
            alarmHistory.setId(dbObjectOne.get("_id") != null ? dbObjectOne.get("_id") + "" : "");
            alarmHistory.setTrapOid(dbObjectOne.get("trap_oid") != null ? dbObjectOne.get("trap_oid") + "" : "");
            alarmHistory.setAlarmName(dbObjectOne.get("alarm_name") != null ? dbObjectOne.get("alarm_name") + "" : "");
            alarmHistory.setAlarmNameId(dbObjectOne.get("alarm_name_id") != null ? dbObjectOne.get("alarm_name_id") + "" : "");
            alarmHistory.setAlarmCode(dbObjectOne.get("alarm_code") != null ? dbObjectOne.get("alarm_code") + "" : "");
            alarmHistory.setAlarmContent(dbObjectOne.get("alarm_content") != null ? dbObjectOne.get("alarm_content") + "" : "");
            alarmHistory.setAlarmType(dbObjectOne.get("alarm_type") != null ? (int)dbObjectOne.get("alarm_type") : null);
            alarmHistory.setAlarmSource(dbObjectOne.get("alarm_source") != null ? dbObjectOne.get("alarm_source") + "" : "");
            alarmHistory.setAlarmSourceType(dbObjectOne.get("alarm_source_type") != null ? dbObjectOne.get("alarm_source_type") + "" : "");
            alarmHistory.setAlarmSourceTypeId(dbObjectOne.get("alarm_source_type_id") != null ? dbObjectOne.get("alarm_source_type_id") + "" : "");
            alarmHistory.setAreaId(dbObjectOne.get("area_id") != null ? dbObjectOne.get("area_id") + "" : "");
            alarmHistory.setAreaName(dbObjectOne.get("area_name") != null ? dbObjectOne.get("area_name") + "" : "");
            alarmHistory.setIsOrder(dbObjectOne.get("is_order") != null ? (Boolean) dbObjectOne.get("is_order") : null);
            alarmHistory.setAddress(dbObjectOne.get("address") != null ? dbObjectOne.get("address") + "" : "");
            alarmHistory.setAlarmFixedLevel(dbObjectOne.get("alarm_fixed_level") != null ? dbObjectOne.get("alarm_fixed_level") + "" : "");
            alarmHistory.setAlarmObject(dbObjectOne.get("alarm_object") != null ? dbObjectOne.get("alarm_object") + "" : "");
            alarmHistory.setResponsibleDepartmentId(dbObjectOne.get("responsible_department_id") != null ? dbObjectOne.get("responsible_department_id") + "" : "");
            alarmHistory.setResponsibleDepartment(dbObjectOne.get("responsible_department") != null ? dbObjectOne.get("responsible_department") + "" : "");
            alarmHistory.setPrompt(dbObjectOne.get("prompt") != null ? dbObjectOne.get("prompt") + "" : "");
            alarmHistory.setAlarmBeginTime(dbObjectOne.get("alarm_begin_time") != null ? Long.valueOf(dbObjectOne.get("alarm_begin_time") + "") : null);
            alarmHistory.setAlarmBeginTime(dbObjectOne.get("alarm_near_time") != null ? Long.valueOf(dbObjectOne.get("alarm_near_time") + "") : null);
            alarmHistory.setAlarmSystemTime(dbObjectOne.get("alarm_system_time") != null ? Long.valueOf(dbObjectOne.get("alarm_system_time") + "") : null);
            alarmHistory.setAlarmSystemNearTime(dbObjectOne.get("alarm_system_near_time") != null ? Long.valueOf(dbObjectOne.get("alarm_system_near_time") + "") : null);
            alarmHistory.setAlarmContinousTime(dbObjectOne.get("alarm_continous_time") != null ? (int)dbObjectOne.get("alarm_continous_time") : null);
            alarmHistory.setAlarmHappenCount(dbObjectOne.get("alarm_happen_count") != null ? (int)dbObjectOne.get("alarm_happen_count") : null);
            alarmHistory.setAlarmCleanStatus(dbObjectOne.get("alarm_clean_status") != null ? (int)dbObjectOne.get("alarm_clean_status") : null);
            alarmHistory.setAlarmCleanTime(dbObjectOne.get("alarm_clean_time") != null ?  Long.valueOf(dbObjectOne.get("alarm_clean_time") + "") : null);
            alarmHistory.setAlarmCleanType(dbObjectOne.get("alarm_clean_type") != null ?  (int)dbObjectOne.get("alarm_clean_type") : null);
            alarmHistory.setAlarmCleanPeopleId(dbObjectOne.get("alarm_clean_people_id") != null ? dbObjectOne.get("alarm_clean_people_id") + "" : "");
            alarmHistory.setAlarmCleanPeopleNickname(dbObjectOne.get("alarm_clean_people_nickname") != null ? dbObjectOne.get("alarm_clean_people_nickname") + "" : "");
            alarmHistory.setAlarmConfirmStatus(dbObjectOne.get("alarm_confirm_status") != null ? (int)dbObjectOne.get("alarm_confirm_status") : null);
            alarmHistory.setAlarmConfirmTime(dbObjectOne.get("alarm_confirm_time") != null ? Long.valueOf(dbObjectOne.get("alarm_confirm_time") + "") : null);
            alarmHistory.setAlarmConfirmPeopleId(dbObjectOne.get("alarm_confirm_people_id") != null ? dbObjectOne.get("alarm_confirm_people_id") + "" : "");
            alarmHistory.setAlarmConfirmPeopleNickname(dbObjectOne.get("alarm_confirm_people_nickname") != null ? dbObjectOne.get("alarm_confirm_people_nickname") + "" : "");
            alarmHistory.setExtraMsg(dbObjectOne.get("extra_msg") != null ? dbObjectOne.get("extra_msg") + "" : "");
            alarmHistory.setAlarmProcessing(dbObjectOne.get("alarm_processing") != null ? dbObjectOne.get("alarm_processing") + "" : "");
            alarmHistory.setRemark(dbObjectOne.get("remark") != null ? dbObjectOne.get("remark") + "" : "");
            alarmHistory.setDoorNumber(dbObjectOne.get("door_number") != null ? dbObjectOne.get("door_number") + "" : "");
            alarmHistory.setDoorName(dbObjectOne.get("door_name") != null ? dbObjectOne.get("door_name") + "" : "");
            alarmHistory.setIsPicture(dbObjectOne.get("is_picture") != null ? (Boolean) dbObjectOne.get("is_picture") : null);
            alarmHistory.setControlId(dbObjectOne.get("control_id") != null ? dbObjectOne.get("control_id") + "" : "");
            alarmHistoryList.add(alarmHistory);
        }
        return alarmHistoryList;
    }
}