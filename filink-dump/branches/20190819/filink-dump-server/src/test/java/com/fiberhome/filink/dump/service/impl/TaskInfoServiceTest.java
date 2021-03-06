package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dump.api.DumpFeign;
import com.fiberhome.filink.dump.bean.*;
import com.fiberhome.filink.dump.constant.DumpConstant;
import com.fiberhome.filink.dump.constant.ExportApiConstant;
import com.fiberhome.filink.dump.constant.ExportI18nConstant;
import com.fiberhome.filink.dump.dao.TaskInfoDao;
import com.fiberhome.filink.dump.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.dump.exception.FilinkExportDirtyDataException;
import com.fiberhome.filink.dump.exception.FilinkExportStopException;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.dump.service.ElasticSearchService;
import com.fiberhome.filink.dump.stream.ExportStreams;
import com.fiberhome.filink.dump.utils.CheckUtil;
import com.fiberhome.filink.dump.utils.ZipsUtil;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.mongodb.*;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.operation.ReadOperation;
import com.mongodb.operation.WriteOperation;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ?????????????????????
 * @author hedongwei@wistronits.com
 * @date 2019/7/25 20:54
 */
@RunWith(JMockit.class)
public class TaskInfoServiceTest {


    @Tested
    private TaskInfoServiceImpl taskInfoService;

    /**
     * es????????????????????????
     */
    @Injectable
    private ElasticSearchService elasticSearchService;

    /**
     * ????????????taskInfoDao
     */
    @Injectable
    private TaskInfoDao taskInfoDao;
    /**
     * ?????????????????????????????????
     */
    @Injectable
    private FdfsFeign fdfsFeign;
    /**
     * ????????????
     */
    @Injectable
    private Integer daysOverdue;
    /**
     * ???????????????
     */
    @Injectable
    private ExportStreams exportStreams;
    /**
     * ??????????????????????????????
     */
    @Injectable
    private Integer maxTaskNum;


    @Injectable
    private MongoTemplate filikLog;


    @Injectable
    private MongoTemplate alarmTemplate;

    @Injectable
    private MongoTemplate filinkDevice;

    @Injectable
    private DumpFeign dumpFeign;

    @Injectable
    private ParameterFeign parameterFeign;

    @Injectable
    private DumpService dumpService;
    /**
     * ???????????????????????????????????????
     */
    @Injectable
    private Integer listExcelSize;
    /**
     * ?????????????????????
     */
    @Injectable
    private String listExcelFilePath;
    /**
     * ???????????????????????????
     */
    @Injectable
    private UploadFile uploadFile;
    /**
     * ????????????
     */
    @Injectable
    private ZipsUtil zipsUtil;
    /**
     * ??????????????????
     */
    @Injectable
    private Integer maxExportDataSize;

    @Injectable
    private ExportApiConstant exportApiConstant;

    /**
     * ????????????
     */
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ????????????????????????
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * ?????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/6 16:57
     */
    @Test
    public void callTaskInfoClass() {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.getFilePath();
        taskInfo.getTaskStatus();
        taskInfo.getCreateTime();
        taskInfo.getCreateUser();
        taskInfo.getUpdateTime();
        taskInfo.getUpdateUser();
        taskInfo.getListName();
        taskInfo.getQueryCondition();
        taskInfo.getColumnInfos();
        taskInfo.getMethodPath();
        taskInfo.getExcelType();
        taskInfo.getDumpAllNumber();
        taskInfo.setUpdateTime(new Date());
        taskInfo.setCreateTime(new Date().getTime());
        taskInfo.getTsCreateTime();
        taskInfo.getTsUpDateTime();
        taskInfo.setUpdateTime(null);
        taskInfo.getTsUpDateTime();
        UserParameter userParameter = new UserParameter();
        userParameter.toString();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.getColumnName();
        columnInfo.getPropertyName();

        Export export = new Export();
        export.getDataList();
        export.getTimeZone();

        OperateLog operateLog = new OperateLog();
        operateLog.getRemark();

        SecurityLog securityLog = new SecurityLog();
        securityLog.getRemark();

        SystemLog systemLog = new SystemLog();
        systemLog.getRemark();

        int dumpDataNum = 0;
        String dumpPlace = "";
        DumpDto dumpDto = new DumpDto(new ArrayList(), dumpDataNum, dumpPlace, new Query(), SystemLog.class);
        dumpDto.getClazz();
        dumpDto.getDumpDataList();
        dumpDto.getDumpDataNum();
        dumpDto.getDumpPlace();
        dumpDto.getQuery();

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(0);
        pageCondition.setPageNum(1);
        CheckUtil.checkPageConditionNull(pageCondition);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/6 16:46
     */
    @Test
    public void callAlarmHistoryClass() {
        try {
            new FilinkExportDataTooLargeException("msg");
        } catch (Exception e) {

        }
        try {
            new FilinkExportDirtyDataException();
        } catch (Exception e) {

        }
        try {
            new FilinkExportDirtyDataException("msg");
        } catch (Exception e) {

        }
        try {
            new FilinkExportStopException();
        } catch (Exception e) {

        }
        ExportI18nConstant exportI18nConstant = new ExportI18nConstant();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.getId();
        alarmHistory.getAddress();
        alarmHistory.getAlarmBeginTime();
        alarmHistory.getAlarmCleanPeopleId();
        alarmHistory.getAlarmCleanPeopleNickname();
        alarmHistory.getAlarmCleanStatus();
        alarmHistory.getAlarmCleanTime();
        alarmHistory.getAlarmCleanType();
        alarmHistory.getAlarmCode();
        alarmHistory.getAlarmConfirmPeopleId();
        alarmHistory.getAlarmConfirmPeopleNickname();
        alarmHistory.getAlarmConfirmStatus();
        alarmHistory.getAlarmConfirmTime();
        alarmHistory.getAlarmContent();
        alarmHistory.getAlarmContinousTime();
        alarmHistory.getAlarmFixedLevel();
        alarmHistory.getAlarmHappenCount();
        alarmHistory.getAlarmName();
        alarmHistory.getAlarmNameId();
        alarmHistory.getAlarmNearTime();
        alarmHistory.getAlarmObject();
        alarmHistory.getAlarmProcessing();
        alarmHistory.getAlarmSource();
        alarmHistory.getAlarmSourceType();
        alarmHistory.getAlarmSourceTypeId();
        alarmHistory.getAlarmSystemNearTime();
        alarmHistory.getAlarmSystemTime();
        alarmHistory.getAlarmType();
        alarmHistory.getAreaId();
        alarmHistory.getAreaName();
        alarmHistory.getControlId();
        alarmHistory.getDoorName();
        alarmHistory.getDoorNumber();
        alarmHistory.getExtraMsg();
        alarmHistory.getIsOrder();
        alarmHistory.getIsPicture();
        alarmHistory.getPrompt();
        alarmHistory.getRemark();
        alarmHistory.getResponsibleDepartment();
        alarmHistory.getResponsibleDepartmentId();
        alarmHistory.getTrapOid();
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void addTask() {
        int maxSize = 10000;
        ExportDto exportDto = new ExportDto();
        ReflectionTestUtils.setField(taskInfoService, "maxTaskNum", maxSize);

        try {
            taskInfoService.addTask(exportDto);
        } catch (Exception e) {

        }

        exportDto.setTaskId("");

        new Expectations() {
            {
                taskInfoDao.insert((TaskInfo) any);
                result = 1;
            }
        };

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, anyInt);
            }
        };

       try {
           taskInfoService.addTask(exportDto);
       } catch (Exception e) {

       }

       exportDto.setTaskId("1");

       new Expectations() {
           {
               taskInfoDao.selectById(anyString);
               result = null;
           }
       };

       try {
        taskInfoService.addTask(exportDto);
       } catch (Exception e) {

       }

       new Expectations() {
          {
              taskInfoDao.selectById(anyString);
              TaskInfo taskInfo = new TaskInfo();
              taskInfo.setTaskInfoId("1");
              result = taskInfo;
          }
       };

       new Expectations() {
           {
               taskInfoDao.updateById((TaskInfo) any);
               result = 1;
           }
       };

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, anyInt);
            }
        };

       try {
           taskInfoService.addTask(exportDto);
       } catch (Exception e) {

       }

       int minSize = 1;
       ReflectionTestUtils.setField(taskInfoService, "maxTaskNum", minSize);
       new Expectations() {
           {
               taskInfoDao.selectOngoingTaskCountByUserId(exportDto.getUserId());
               result = 2;
           }
       };

       try {
           taskInfoService.addTask(exportDto);
       } catch (Exception e) {

       }

    }

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void updateTaskFileNumById() {
        ExportDto exportDto = new ExportDto();
        exportDto.setTaskId("1");
        exportDto.setFileGeneratedNum(1);
        exportDto.setFileNum(1);

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);

            }
        };

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(anyString);

            }
        };
        taskInfoService.updateTaskFileNumById(exportDto);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void changeTaskStatusToUnusual() {
        String taskId = "1";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);

            }
        };

        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                TaskInfo dbTaskInfo = new TaskInfo();
                dbTaskInfo.setTaskInfoId("1");
                result = dbTaskInfo;
                taskInfoDao.updateById((TaskInfo) any);
                result = 1;
            }
        };
        taskInfoService.changeTaskStatusToUnusual(taskId);


        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);

            }
        };


        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = null;
            }
        };
        try {
            taskInfoService.changeTaskStatusToUnusual(taskId);
        } catch (Exception e) {

        }


        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);

            }
        };

        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                TaskInfo dbTaskInfo = new TaskInfo();
                dbTaskInfo.setTaskInfoId("1");
                result = dbTaskInfo;
                taskInfoDao.updateById((TaskInfo) any);
                result = 0;
            }
        };
        try {
            taskInfoService.changeTaskStatusToUnusual(taskId);
        } catch (Exception e) {

        }
    }


    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void selectTaskIsStopById() {
        String taskId = "1";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(anyString);
                result = null;
            }
        };
        taskInfoService.selectTaskIsStopById(taskId);

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(anyString);
                int i = 1;
                result = i;
            }
        };
        taskInfoService.selectTaskIsStopById(taskId);
    }


    /**
     * ????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void exportData() {
        ReflectionTestUtils.setField(taskInfoService, "listExcelFilePath", "aaa/nnn");
        ReflectionTestUtils.setField(taskInfoService, "listExcelSize", 20000);

        DumpData dumpData = new DumpData();
        dumpData.setClazz(AlarmHistory.class);
        dumpData.setDumpType(DumpType.ALARM_LOG_DUMP);
        dumpData.setMongoTemplate(alarmTemplate);
        dumpData.setQueryStr(ExportApiConstant.ALARM_QUERY);
        dumpData.setListName(ExportApiConstant.ALARM_LIST_NAME);

        Export export = new Export();
        int dumpNum = 1;
        String queryStr = ExportApiConstant.ALARM_QUERY;
        Class clazz = dumpData.getClazz();
        MongoTemplate mongoTemplate = alarmTemplate;
        DumpBean dumpBean = new DumpBean();

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void startExport(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
                return;
            }
        };

        dumpBean.setDumpOperation(ExportApiConstant.DUMP_OPERATION_DELETE);

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void deleteDumpInfo(int dumpNum, Query query, MongoTemplate mongoTemplate, Sort begin, Class clazz, String queryStr) {
                return;
            }
        };



        new MockUp<MongoTemplate>(){
            @Mock
            public DBCollection getCollection(final String collectionName) {

                return null;
            }
        };

        String trigger = "1";
        try {
            taskInfoService.exportData( export,  dumpNum, queryStr,  clazz,  mongoTemplate,  dumpBean, dumpData, trigger);
        } catch (Exception e) {

        }



        queryStr = "create_time";

        dumpData = new DumpData();
        dumpData.setClazz(OperateLog.class);
        dumpData.setDumpType(DumpType.SYSTEM_LOG_DUMP);
        dumpData.setMongoTemplate(filikLog);
        dumpData.setQueryStr(ExportApiConstant.LOG_QUERY);
        dumpData.setListName(ExportApiConstant.OPERA_LOG_LIST_NAME);

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void startExport(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
                return;
            }
        };

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void deleteDumpInfo(int dumpNum, Query query, MongoTemplate mongoTemplate, Sort begin, Class clazz, String queryStr) {
                return;
            }
        };

        taskInfoService.exportData( export,  dumpNum, queryStr,  clazz,  mongoTemplate,  dumpBean, dumpData, trigger);


        dumpData.setClazz(SecurityLog.class);
        dumpData.setListName(ExportApiConstant.SEC_LOG_LIST_NAME);
        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void startExport(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
                return;
            }
        };

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void deleteDumpInfo(int dumpNum, Query query, MongoTemplate mongoTemplate, Sort begin, Class clazz, String queryStr) {
                return;
            }
        };
        try {
            taskInfoService.exportData( export,  dumpNum, queryStr,  clazz,  mongoTemplate,  dumpBean, dumpData, trigger);
        } catch (Exception e) {

        }


        dumpData.setClazz(SystemLog.class);
        dumpData.setListName(ExportApiConstant.SYS_LOG_LIST_NAME);
        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void startExport(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
                return;
            }
        };

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public void deleteDumpInfo(int dumpNum, Query query, MongoTemplate mongoTemplate, Sort begin, Class clazz, String queryStr) {
                return;
            }
        };

        try {
            taskInfoService.exportData( export,  dumpNum, queryStr,  clazz,  mongoTemplate,  dumpBean, dumpData, trigger);
        } catch (Exception e) {

        }
    }


    /**
     * ????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:00
     */
    @Test
    public void startExport() {
        ReflectionTestUtils.setField(taskInfoService, "listExcelFilePath", "aaa/nnn");
        int maxNumber = 100000;
        ReflectionTestUtils.setField(taskInfoService, "listExcelSize", maxNumber);

        DumpData dumpData = new DumpData();
        dumpData.setClazz(AlarmHistory.class);
        dumpData.setDumpType(DumpType.ALARM_LOG_DUMP);
        dumpData.setMongoTemplate(alarmTemplate);
        dumpData.setQueryStr(ExportApiConstant.ALARM_QUERY);
        dumpData.setListName(ExportApiConstant.ALARM_LIST_NAME);

        Export export = new Export();
        int dumpNum = 1;
        String queryStr = "1";
        Class clazz = dumpData.getClazz();
        MongoTemplate mongoTemplate = alarmTemplate;
        DumpBean dumpBean = new DumpBean();

        int pageNum = 0;

        Export exportDto = new Export();
        QueryCondition queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        queryCondition.setPageCondition(pageCondition);
        exportDto.setQueryCondition(queryCondition);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        //??????class????????????
        for (Field field : AlarmHistory.class.getDeclaredFields()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(field.getName());
            columnInfo.setPropertyName(field.getName());
            columnInfoList.add(columnInfo);
        }

        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);


        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public List<Object> queryDumpDataToListObject(Query query, String className, String queryStr) {
                return  TaskInfoServiceTest.getAlarmHistory();
            }
        };

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public Boolean updateTaskFileNumById(ExportDto exportDto) {
                return false;
            }
        };

        new Expectations() {
            {
                parameterFeign.queryFtpSettings();
                FtpSettings ftpSettings = new FtpSettings();
                ftpSettings.setIpAddress("address");
                result = ftpSettings;
            }
        };

        exportDto.setDateSize(1);
        pageNum = 1;
        exportDto.setDumpSite(1);

        try {
            taskInfoService.startExport(exportDto, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
        } catch (Exception e) {

        }


        exportDto.setDateSize(null);
        pageNum = 0;
        exportDto.setDumpSite(null);
        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            private void specificExportData(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) throws Exception {
                throw new Exception();
            }
        };

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            private void updateTaskInfo(Export export, int dumpNum, int pageNum, String queryStr, Class clazz, MongoTemplate mongoTemplate) {
                return;
            }
        };

        try {
            taskInfoService.startExport(export, dumpNum, pageNum, queryStr, clazz, mongoTemplate);
        } catch (Exception e) {

        }





    }


    /**
     * ????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:04
     */
    @Test
    public void insertTask() {
        ExportDto exportDto = new ExportDto();
        String serverName = "serverName";
        String listName = "listName";
        Integer count = 100000 ;

        ReflectionTestUtils.setField(taskInfoService, "listExcelFilePath", "aaa/nnn");

        ReflectionTestUtils.setField(taskInfoService, "listExcelSize", 1000000);

        ReflectionTestUtils.setField(taskInfoService, "maxExportDataSize", 1000000);

        ReflectionTestUtils.setField(taskInfoService, "listExcelFilePath", "aaa/nnn");

        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        exportDto.setQueryCondition(queryCondition);

        new MockUp<TaskInfoServiceImpl>(){
            @Mock
            public Result addTask(ExportDto exportDto) {
                return ResultUtils.success();
            }
        };

        taskInfoService.insertTask(exportDto, serverName, listName, count) ;
    }



    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:04
     */
    @Test
    public void queryDumpData() {
        DumpType dumpType = DumpType.ALARM_LOG_DUMP;
        MongoTemplate mongoTemplate = alarmTemplate;
        Class clazz = AlarmHistory.class;
        String createTime = ExportApiConstant.ALARM_QUERY;
        String listName = "aaa";
        new Expectations() {
            {
                dumpFeign.queryDump(dumpType.getType());
                DumpBean dumpBean = new DumpBean();
                dumpBean.setEnableDump(ExportApiConstant.ENABLE_DUMP_DATA);
                dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_NUMBER);
                dumpBean.setDumpQuantityThreshold("10000");
                dumpBean.setTurnOutNumber("1");
                result = dumpBean;

                mongoTemplate.count((Query) any, (Class) any);
                result = 10001;
            }
        };

        taskInfoService.queryDumpData(dumpType, mongoTemplate, clazz, createTime, listName);

        new Expectations() {
            {
                dumpFeign.queryDump(dumpType.getType());
                DumpBean dumpBean = new DumpBean();
                dumpBean.setEnableDump(ExportApiConstant.ENABLE_DUMP_DATA);
                dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_NUMBER);
                dumpBean.setDumpQuantityThreshold("10000");
                dumpBean.setTurnOutNumber("1");
                dumpBean.setDumpInterval("2");
                dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_MONTH);
                result = dumpBean;

                mongoTemplate.count((Query) any, (Class) any);
                result = 10001;
            }
        };

        taskInfoService.queryDumpData(dumpType, mongoTemplate, clazz, createTime, listName);
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:04
     */
    @Test
    public void queryDumpDataInfo() {
        DumpType dumpType = DumpType.ALARM_LOG_DUMP;
        MongoTemplate mongoTemplate = alarmTemplate;
        Class clazz = AlarmHistory.class;
        String createTime = ExportApiConstant.ALARM_QUERY;
        String listName = "a";

        DumpBean dumpBean = new DumpBean();
        dumpBean.setEnableDump(ExportApiConstant.ENABLE_DUMP_DATA);
        dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_NUMBER);
        dumpBean.setDumpQuantityThreshold("10000");
        dumpBean.setTurnOutNumber("1");
        dumpBean.setDumpInterval("2");
        dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_MONTH);

        new Expectations() {
            {
                mongoTemplate.count((Query) any, (Class) any);
                result = 10001;
            }
        };

        String trigger = "0";
        taskInfoService.queryDumpDataInfo(dumpBean ,dumpType, mongoTemplate, clazz, createTime, listName, trigger);

        dumpBean.setTriggerCondition(ExportApiConstant.DUMP_DATA_BY_NUMBER);
        new Expectations() {
            {
                mongoTemplate.count((Query) any, (Class) any);
                result = 10001;
            }
        };

        taskInfoService.queryDumpDataInfo(dumpBean ,dumpType, mongoTemplate, clazz, createTime, listName, trigger);
    }

    @Test
    public void queryDumpDataToListObject() {
        Query query = new Query();
        String className = OperateLog.class.getName();
        String queryStr = "1";
        try {
            taskInfoService.queryDumpDataToListObject(query, className, queryStr);
        } catch (Exception e) {

        }


        className = SecurityLog.class.getName();
        try {
            taskInfoService.queryDumpDataToListObject(query, className, queryStr);
        } catch (Exception e) {

        }


        className = SystemLog.class.getName();
        try {
            taskInfoService.queryDumpDataToListObject(query, className, queryStr);
        } catch (Exception e) {

        }

        className = DeviceLog.class.getName();
        try {
            taskInfoService.queryDumpDataToListObject(query, className, queryStr);
        } catch (Exception e) {

        }


        className = OperateLog.class.getName();
        try {
            taskInfoService.queryDumpDataToListObject(query, className, queryStr);
        } catch (Exception e) {

        }
    }


    /**
     * ?????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 10:44
     */
    public static List<Object> getAlarmHistory() {
        List<Object> objectList = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("22222222222");
        alarmHistory.setTrapOid("1");
        alarmHistory.setAlarmName("1");
        alarmHistory.setAlarmNameId("1");
        alarmHistory.setAlarmCode("1");
        alarmHistory.setAlarmContent("1");
        alarmHistory.setAlarmType(1);
        alarmHistory.setAlarmSource("1");
        alarmHistory.setAlarmSourceType("1");
        alarmHistory.setAlarmSourceTypeId("1");
        alarmHistory.setAreaId("1");
        alarmHistory.setAreaName("1");
        alarmHistory.setIsOrder(true);
        alarmHistory.setAddress("1");
        alarmHistory.setAlarmFixedLevel("1");
        alarmHistory.setAlarmObject("1");
        alarmHistory.setResponsibleDepartmentId("1");
        alarmHistory.setResponsibleDepartment("1");
        alarmHistory.setPrompt("1");
        alarmHistory.setAlarmBeginTime(new Date().getTime());
        alarmHistory.setAlarmNearTime(new Date().getTime());
        alarmHistory.setAlarmContinousTime(1);
        alarmHistory.setAlarmHappenCount(1);
        alarmHistory.setAlarmCleanStatus(1);
        alarmHistory.setAlarmCleanTime(new Date().getTime());
        alarmHistory.setAlarmCleanType(1);
        alarmHistory.setAlarmCleanPeopleId("1");
        alarmHistory.setAlarmCleanPeopleNickname("1");
        alarmHistory.setAlarmConfirmStatus(1);
        alarmHistory.setAlarmConfirmTime(new Date().getTime());
        alarmHistory.setAlarmConfirmPeopleId("1");
        alarmHistory.setAlarmConfirmPeopleNickname("1");
        alarmHistory.setExtraMsg("1");
        alarmHistory.setAlarmProcessing("1");
        alarmHistory.setRemark("1");
        alarmHistory.setDoorName("1");
        alarmHistory.setDoorNumber("1");
        alarmHistory.setIsPicture(true);
        objectList.add(alarmHistory);
        return objectList;
    }


    /**
     * ??????id??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:04
     */
    @Test
    public void queryDumpInfoById() {
        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = null;
            }
        };
        String taskId = "1";
        taskInfoService.queryDumpInfoById(taskId);

        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setIsDeleted("0");
                taskInfo.setTaskInfoId("1");
                result = taskInfo;

                taskInfoDao.queryTaskInfosByType(anyString);
                List<TaskInfo> taskInfoList = new ArrayList<>();
                TaskInfo taskInfoOne = new TaskInfo();
                taskInfoOne.setFileNum(1);
                taskInfoOne.setFileGeneratedNum(1);
                taskInfoList.add(taskInfoOne);
                result = taskInfoList;
            }
        };
        taskInfoService.queryDumpInfoById(taskId);
    }


    /**
     * ????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:04
     */
    @Test
    public void dumpData() {
        Integer dumpData = ExportApiConstant.ALARM_LOG_TYPE;

        new Expectations() {
            {
                taskInfoDao.queryNoCompleteTaskInfosByType(anyString);
                List<TaskInfo> taskInfoLiat = new ArrayList<>();
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskInfoId("1");
                taskInfoLiat.add(taskInfo);
                result = taskInfoLiat;
            }
        };


        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);
            }
        };

        new Expectations() {
            {
                taskInfoDao.deleteBatchIds((List<String>) any);
            }
        };
        String trigger = "0";
        taskInfoService.dumpData(dumpData, trigger);

        dumpData = ExportApiConstant.LOG_TYPE;


        new Expectations() {
            {
                taskInfoDao.queryNoCompleteTaskInfosByType(anyString);
                List<TaskInfo> taskInfoLiat = new ArrayList<>();
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskInfoId("1");
                taskInfoLiat.add(taskInfo);
                result = taskInfoLiat;
            }
        };


        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);
            }
        };

        new Expectations() {
            {
                taskInfoDao.deleteBatchIds((List<String>) any);
            }
        };
        trigger = "0";
        taskInfoService.dumpData(dumpData, trigger);

        dumpData = ExportApiConstant.DEVICE_LOG_TYPE;

        new Expectations() {
            {
                taskInfoDao.queryNoCompleteTaskInfosByType(anyString);
                List<TaskInfo> taskInfoLiat = new ArrayList<>();
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskInfoId("1");
                taskInfoLiat.add(taskInfo);
                result = taskInfoLiat;
            }
        };


        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(anyString);
            }
        };

        new Expectations() {
            {
                taskInfoDao.deleteBatchIds((List<String>) any);
            }
        };
        trigger = "0";
        taskInfoService.dumpData(dumpData, trigger);
    }


    /**
     * ?????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:07
     */
    @Test
    public void getDumpResult() {
        Integer dumpType = ExportApiConstant.LOG_TYPE;
        String msg = "11";
        taskInfoService.getDumpResult(dumpType, msg);

        msg = "";
        taskInfoService.getDumpResult(dumpType, msg);

        dumpType = ExportApiConstant.ALARM_LOG_TYPE;
        msg = "11";
        taskInfoService.getDumpResult(dumpType, msg);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:07
     */
    @Test
    public void queryDumpInfo() {
        String dumpType = "11";

        new Expectations() {
            {
                taskInfoDao.queryLastTaskInfo(anyString);
                result = null;
            }
        };

        taskInfoService.queryDumpInfo(dumpType);

        new Expectations() {
            {
                taskInfoDao.queryLastTaskInfo(anyString);
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskInfoId("1");
                result = taskInfo;

                taskInfoDao.queryTotalTaskNum(anyString);
                result = 1L;
            }
        };
        taskInfoService.queryDumpInfo(dumpType);
    }


    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:47
     */
    @Test
    public void operateLogList() {
        //???????????????
        this.hasNextValue(false);
        DBCursor dbCursor = this.getSearchDBCurosr();
        taskInfoService.operateLogList(dbCursor);

        //??????????????????????????????
        this.hasNextValue(true);

        //??????????????????
        this.logDBCursorData();

        //???????????????????????????????????????
        this.cursorReturnWhile();
        try {
            taskInfoService.operateLogList(dbCursor);
        } catch (Exception e) {

        }
    }


    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:47
     */
    @Test
    public void securityLogList() {
        //???????????????
        this.hasNextValue(false);
        DBCursor dbCursor = this.getSearchDBCurosr();
        taskInfoService.securityLogList(dbCursor);

        //??????????????????????????????
        this.hasNextValue(true);

        //??????????????????
        this.logDBCursorData();

        //???????????????????????????????????????
        this.cursorReturnWhile();
        try {
            taskInfoService.securityLogList(dbCursor);
        } catch (Exception e) {

        }
    }



    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:47
     */
    @Test
    public void systemLogList() {
        //???????????????
        this.hasNextValue(false);
        DBCursor dbCursor = this.getSearchDBCurosr();
        taskInfoService.systemLogList(dbCursor);

        //??????????????????????????????
        this.hasNextValue(true);

        //??????????????????
        this.logDBCursorData();

        //???????????????????????????????????????
        this.cursorReturnWhile();
        try {
            taskInfoService.systemLogList(dbCursor);
        } catch (Exception e) {

        }
    }



    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:47
     */
    @Test
    public void deviceLogList() {
        //???????????????
        this.hasNextValue(false);
        DBCursor dbCursor = this.getSearchDBCurosr();
        taskInfoService.deviceLogList(dbCursor);

        //??????????????????????????????
        this.hasNextValue(true);

        //??????????????????
        this.deviceDBCursorData();

        //???????????????????????????????????????
        this.cursorReturnWhile();
        try {
            taskInfoService.deviceLogList(dbCursor);
        } catch (Exception e) {

        }
    }


    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:47
     */
    @Test
    public void alarmHistoryList() {
        //???????????????
        this.hasNextValue(false);
        DBCursor dbCursor = this.getSearchDBCurosr();
        taskInfoService.alarmHistoryList(dbCursor);

        //??????????????????????????????
        this.hasNextValue(true);

        //??????????????????
        this.alarmCurrentDBCursorData();

        //???????????????????????????????????????
        this.cursorReturnWhile();
        try {
            taskInfoService.alarmHistoryList(dbCursor);
        } catch (Exception e) {

        }
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 10:01
     */
    @Test
    public void alarmCurrentDBCursorData() {
        new MockUp<DBCursor> (DBCursor.class) {
            @Mock
            public DBObject next() {
                BasicDBObject dbObject = new BasicDBObject();
                String hexString = "5d49691a6248b000071f176a";
                dbObject.put("_id" ,new ObjectId(hexString));
                dbObject.put("trap_oid", "1");
                dbObject.put("alarm_name","1");
                dbObject.put("alarm_name_id", "1");
                dbObject.put("alarm_code", "1");
                dbObject.put("alarm_content", "1");
                dbObject.put("alarm_type", 1);
                dbObject.put("alarm_source", "1");
                dbObject.put("alarm_source_type", "1");
                dbObject.put("alarm_source_type_id", "1");
                dbObject.put("area_id", "1");
                dbObject.put("area_name", "1");
                dbObject.put("is_order", true);
                dbObject.put("address", "1");
                dbObject.put("alarm_fixed_level", "1");
                dbObject.put("alarm_object", "1");
                dbObject.put("responsible_department_id", "1");
                dbObject.put("responsible_department", "1");
                dbObject.put("prompt", "1");
                dbObject.put("alarm_begin_time", new Date().getTime());
                dbObject.put("alarm_near_time", new Date().getTime());
                dbObject.put("alarm_system_time", new Date().getTime());
                dbObject.put("alarm_system_near_time", new Date().getTime());
                dbObject.put("alarm_continous_time", 1);
                dbObject.put("alarm_happen_count", 1);
                dbObject.put("alarm_clean_status", 1);
                dbObject.put("alarm_clean_time", new Date().getTime());
                dbObject.put("alarm_clean_type", 1);
                dbObject.put("alarm_clean_people_id", "1");
                dbObject.put("alarm_clean_people_nickname", "1");
                dbObject.put("alarm_confirm_status", 1);
                dbObject.put("alarm_confirm_time", new Date().getTime());
                dbObject.put("alarm_confirm_people_id", "1");
                dbObject.put("alarm_confirm_people_nickname", "1");
                dbObject.put("extra_msg", "1");
                dbObject.put("alarm_processing", "1");
                dbObject.put("remark", "1");
                dbObject.put("door_number", "1");
                dbObject.put("door_name", "1");
                dbObject.put("is_picture", true);
                return dbObject;
            }
        };
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 10:01
     */
    public void deviceDBCursorData() {
        new MockUp<DBCursor> (DBCursor.class) {
            @Mock
            public DBObject next() {
                BasicDBObject dbObject = new BasicDBObject();
                String hexString = "5d49691a6248b000071f176a";
                dbObject.put("_id" ,new ObjectId(hexString));
                dbObject.put("type", "1");
                dbObject.put("logName","1");
                dbObject.put("logType", "1");
                dbObject.put("deviceId", "1");
                dbObject.put("deviceType", 1);
                dbObject.put("deviceCode", "1");
                dbObject.put("deviceName", "1");
                dbObject.put("nodeObject", "1");
                dbObject.put("areaId", "1");
                dbObject.put("areaName", "1");
                dbObject.put("currentTime", new Date().getTime());
                dbObject.put("remarks", "1");
                return dbObject;
            }
        };
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:55
     */
    public void logDBCursorData() {
        new MockUp<DBCursor> (DBCursor.class) {
            @Mock
            public DBObject next() {
                BasicDBObject dbObject = new BasicDBObject();
                String hexString = "5d49691a6248b000071f176a";
                dbObject.put("_id" ,new ObjectId(hexString));
                dbObject.put("logId", "1");
                dbObject.put("optName","1");
                dbObject.put("optType", "1");
                dbObject.put("dataOptType", "1");
                dbObject.put("dangerLevel", 1);
                dbObject.put("optUserRole", "1");
                dbObject.put("optUserRoleName", "1");
                dbObject.put("optUserCode", "1");
                dbObject.put("optUserName", "1");
                dbObject.put("optTerminal", "1");
                dbObject.put("optTime", new Date().getTime());
                dbObject.put("optObj", "1");
                dbObject.put("optObjId", "1");
                dbObject.put("optResult", "1");
                dbObject.put("detailInfo", "1");
                dbObject.put("remark", "1");
                dbObject.put("createUser", "11");
                dbObject.put("createTime", new Date().getTime());
                dbObject.put("updateUser", "222");
                dbObject.put("updateTime", new Date().getTime());
                return dbObject;
            }
        };
    }


    /**
     * ??????es?????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 14:26
     */
    @Test
    public void deleteElasticSearchInfo() {
        List<String> alarmHistoryList = new ArrayList<>();
        for (int i = 0 ; i < 10000 ; i ++) {
            alarmHistoryList.add(i + "");
        }
        try {
            taskInfoService.deleteElasticSearchInfo(alarmHistoryList);
        } catch (Exception e) {

        }
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 14:35
     */
    @Test
    public void deleteDumpInfo() {
        Query query = new Query();
        MongoTemplate mongoTemplate = alarmTemplate;
        Class clazz = AlarmHistory.class;
        String queryStr = "_id";
        int dumpNum = 20001;
        ReflectionTestUtils.setField(taskInfoService, "listExcelSize", 20000);
        Sort begin = new Sort(Sort.Direction.ASC, queryStr);
        taskInfoService.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
        mongoTemplate = filikLog;
        clazz = OperateLog.class;
        taskInfoService.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
        clazz = SystemLog.class;
        taskInfoService.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
        clazz = SecurityLog.class;
        taskInfoService.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
        mongoTemplate = filinkDevice;
        clazz = DeviceLog.class;
        taskInfoService.deleteDumpInfo(dumpNum, query, mongoTemplate, begin, clazz, queryStr);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 14:35
     */
    @Test
    public void dataRemoveMethod() {
        List<String> alarmHistoryList = new ArrayList<>();
        for (int i = 0 ; i < 10000 ; i ++) {
            alarmHistoryList.add(i + "");
        }
        MongoTemplate mongoTemplate = alarmTemplate;
        Class clazz = AlarmHistory.class;
        String collectionName = DumpConstant.ALARM_HISTORY_COLLECTION_NAME;
        taskInfoService.dataRemoveMethod(alarmHistoryList, mongoTemplate, clazz, collectionName);
    }
    
    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 14:55
     */
    @Test
    public void addLogForDump() {
        String trigger = "0";
        Class clazz = AlarmHistory.class;
        taskInfoService.addLogForDump(trigger, clazz);
        trigger = "1";
        clazz = OperateLog.class;
        taskInfoService.addLogForDump(trigger, clazz);
        clazz = SystemLog.class;
        taskInfoService.addLogForDump(trigger, clazz);
        clazz = SecurityLog.class;
        taskInfoService.addLogForDump(trigger, clazz);
        clazz = DeviceLog.class;
        taskInfoService.addLogForDump(trigger, clazz);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:52
     */
    public void cursorReturnWhile() {
        //???????????????????????????????????????
        new MockUp<ArrayList> (ArrayList.class) {
            @Mock
            public boolean add(Object e) {
                throw  new NullPointerException();
            }
        };
    }

    /**
     * ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:49
     * @param value ????????????
     */
    public void hasNextValue(boolean value) {
        new MockUp<DBCursor> (DBCursor.class) {
            @Mock
            public boolean hasNext() {
                return value;
            }
        };
    }

    /**
     * ??????????????????mongo??????
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 9:45
     */
    public DBCursor getSearchDBCurosr() {
        OperationExecutor executor = new OperationExecutor() {
            @Override
            public <T> T execute(ReadOperation<T> operation, ReadPreference readPreference) {
                return null;
            }

            @Override
            public <T> T execute(WriteOperation<T> operation) {
                return null;
            }
        };


        DB db = new DB(new Mongo(), "operateLog");
        DBCollection dbCollection = db.getCollection("operateLog");
        DBObject query = new BasicDBObject();
        DBObject fields = new BasicDBObject();
        ReadPreference readPreference = null;
        try {
            readPreference = ReadPreference.valueOf("name");
        } catch (Exception e) {

        }
        DBCursor dbCursor = new DBCursor(dbCollection, query, fields, readPreference);
        return dbCursor;
    }
}
