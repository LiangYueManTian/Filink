package com.fiberhome.filink.logserver.service.impl;


import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logserver.bean.*;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.export.statistics.LogTypeExport;
import com.fiberhome.filink.logserver.export.statistics.OperateTypeExport;
import com.fiberhome.filink.logserver.export.statistics.SecurityLevelExport;
import com.fiberhome.filink.logserver.logexport.OperateLogExport;
import com.fiberhome.filink.logserver.logexport.SecurityLogExport;
import com.fiberhome.filink.logserver.logexport.SysLogExport;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;
import com.fiberhome.filink.logserver.service.FunctionDangerLevelConfigService;
import com.fiberhome.filink.logserver.utils.ExportLogI18nCast;
import com.fiberhome.filink.logserver.utils.ExportStatisticUtil;
import com.fiberhome.filink.logserver.utils.LogI18nCast;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * LogService
 *
 * @author hedongwei@wistronits.com
 * create on  2019/1/24
 */
@RunWith(JMockit.class)
public class LogServiceTest {
    /**
     * ???????????? LogService
     */
    @Tested
    private LogServiceImpl logService;

    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;


    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;
    /**
     * ???????????????????????????
     */
    @Injectable
    private OperateLogExport operateLogExport;
    /**
     * ???????????????????????????
     */
    @Injectable
    private SecurityLogExport securityLogExport;
    /**
     * ???????????????????????????
     */
    @Injectable
    private SysLogExport sysLogExport;
    /**
     * ?????????
     */
    private static String SERVER_NAME = "filink-log-server";
    /**
     * ??????????????????
     */
    @Injectable
    private Integer maxExportDataSize;
    /**
     * ????????????????????????
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * ????????????
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;

    /**
     * ???????????????????????????
     */
    @Injectable
    private LogTypeExport logTypeExport;

    /**
     * ???????????????????????????
     */
    @Injectable
    private OperateTypeExport operateTypeExport;

    /**
     * ???????????????????????????
     */
    @Injectable
    private SecurityLevelExport securityLevelExport;


    /**
     * ??????????????????
     */
    @Test
    public void addOperateLog() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        AddOperateLogReq operateLogReq = new AddOperateLogReq();
        operateLogReq.setFunctionCode("1000");
        operateLogReq.setOptType("add");
        this.getFunctionDangerLevelConfigInfo();
        logService.insertOperateLog(operateLogReq);


        AddOperateLogReq addReq = new AddOperateLogReq();
        addReq.setFunctionCode("11111");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptName("111");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptTerminal("127.0.0.1");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptUserCode("1");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptTime(new Date().getTime());
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptObj("1");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setOptResult("success");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setDetailInfo("1111");
        AddOperateLogReq.checkAppOperateLogReq(addReq);
        addReq.setDangerLevel(1);


        User user = new User();
        user.setId("1");
        user.setUserName("1");
        user.setRoleId("1");
        Role role = new Role();
        role.setId("1");
        role.setRoleName("name");
        user.setRole(role);
        AddOperateLogReq.generateAppOperateLogReq(addReq, user);


        LogTypeExportBean bean = new LogTypeExportBean();
        bean.getOperateLog();
        bean.getSecurityLog();
        bean.getSystemLog();
        OperateTypeExportBean exportBean = new OperateTypeExportBean();
        exportBean.getPdaOperate();
        exportBean.getWebOperate();
        SecurityLevelExportBean levelBean = new SecurityLevelExportBean();
        levelBean.getDanger();
        levelBean.getGeneral();
        levelBean.getPrompt();
        OperateLogExportBean operateLogExportBean = new OperateLogExportBean();
        operateLogExportBean.getTranslationDangerLevel();
        operateLogExportBean.getTranslationOptResult();
        operateLogExportBean.getTranslationOptType();

        List<OperateLog> operateLogList = new ArrayList<>();
        OperateLog operateLog = new OperateLog();
        operateLog.setLogId("1");
        operateLogList.add(operateLog);
        operateLogExportBean.getOperateLogExportBeanForOperateLog(operateLogList);

        SystemLogExportBean systemBean = new SystemLogExportBean();
        systemBean.getTranslationDangerLevel();
        systemBean.getTranslationOptResult();
        systemBean.getTranslationOptType();

        List<SystemLog> systemLogList = new ArrayList<>();
        SystemLog systemLog = new SystemLog();
        systemLog.setLogId("1");
        systemLogList.add(systemLog);
        systemBean.getSystemLogExportBeanForSystemLog(systemLogList);

        SecurityLogExportBean securityBean = new SecurityLogExportBean();
        securityBean.getTranslationDangerLevel();
        securityBean.getTranslationOptResult();
        securityBean.getTranslationOptType();

        List<SecurityLog> securityLogList = new ArrayList<>();
        SecurityLog securityLog = new SecurityLog();
        securityLog.setLogId("1");
        securityLogList.add(securityLog);
        securityBean.getSecurityLogExportBeanForSecurityLog(securityLogList);

        ExportLogI18nCast.getOptResult(LogConstants.OPT_RESULT_SUCCESS);
        ExportLogI18nCast.getOptResult(LogConstants.OPT_RESULT_FAILURE);
        ExportLogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
        ExportLogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
        ExportLogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
        ExportLogI18nCast.getOptType(LogConstants.OPT_TYPE_WEB);
        ExportLogI18nCast.getOptType(LogConstants.OPT_TYPE_PDA);
    }

    /**
     * ??????????????????
     */
    @Test
    public void addSecurityLog() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        AddSecurityLogReq securityLogReq = new AddSecurityLogReq();
        securityLogReq.setFunctionCode("1000");
        securityLogReq.setOptType("add");
        this.getFunctionDangerLevelConfigInfo();
        logService.insertSecurityLog(securityLogReq);
    }

    /**
     * ??????????????????
     */
    @Test
    public void addSystemLog() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        AddSystemLogReq systemLogReq = new AddSystemLogReq();
        systemLogReq.setFunctionCode("1000");
        systemLogReq.setOptType("add");
        this.getFunctionDangerLevelConfigInfo();
        logService.insertSystemLog(systemLogReq);
    }

    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:20
     */
    public void getFunctionDangerLevelConfigInfo() {
        new Expectations() {
            {
                functionDangerLevelConfigService.getDangerLevelConfigByFunctionCode(anyString);
                FunctionDangerLevelConfig functionDangerLevelConfig = new FunctionDangerLevelConfig();
                functionDangerLevelConfig.setDangerLevel("1");
                result = functionDangerLevelConfig;
            }
        };
    }

    /**
     * ??????????????????
     */
    @Test
    public void getDangerLevel() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
    }

    /**
     * ?????????????????????
     */
    @Test
    public void getOptResult() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        LogI18nCast.getOptResult(LogConstants.OPT_RESULT_SUCCESS);
        LogI18nCast.getOptResult(LogConstants.OPT_RESULT_FAILURE);
        logService.getOptResult();
    }

    /**
     * ??????????????????
     */
    @Test
    public void getOptType() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        LogI18nCast.getOptType(LogConstants.OPT_TYPE_PDA);
        LogI18nCast.getOptType(LogConstants.OPT_TYPE_WEB);
        logService.getOptType(LogConstants.OPT_TYPE_WEB);
    }




    /**
     * ????????????????????????
     */
    @Test
    public void queryListOperateLogByPage() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        Result result = logService.queryListOperateLogByPage(new QueryCondition());

        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("logId");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("eq");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);

        Result results = logService.queryListOperateLogByPage(queryCondition);
    }

    /**
     * ????????????????????????
     */
    @Test
    public void queryListSecurityLogByPage() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        Result result = logService.queryListSecurityLogByPage(new QueryCondition());

        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("logId");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("eq");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);

        Result results = logService.queryListSecurityLogByPage(queryCondition);
    }

    /**
     * ????????????????????????
     */
    @Test
    public void queryListSystemLogByPage() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        Result result = logService.queryListSystemLogByPage(new QueryCondition());

        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("logId");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("eq");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);

        Result results = logService.queryListSystemLogByPage(queryCondition);
    }

    /**
     * ????????????????????????
     */
    @Test
    public void getOperateLogByLogId() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        String logId = "";
        Result result = logService.getOperateLogByLogId(logId);

        new Expectations() {
            {
                OperateLog log = mongoTemplate.findOne((Query) any, OperateLog.class);
                result = null;
            }
        };
        String logIdInfo = "222";
        Result result1 = logService.getOperateLogByLogId(logIdInfo);

    }

    /**
     * ????????????????????????
     */
    @Test
    public void getSecurityLogByLogId() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        String logId = "";
        Result result = logService.getSecurityLogByLogId(logId);
        new Expectations() {
            {
                SecurityLog log = mongoTemplate.findOne((Query) any, SecurityLog.class);
                result = null;
            }
        };
        String logIdInfo = "222";
        Result result1 = logService.getSecurityLogByLogId(logIdInfo);
    }


    /**
     * ????????????????????????
     */
    @Test
    public void getSystemLogByLogId() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(logService, "maxExportDataSize", maxSize);
        String logId = "";
        Result result = logService.getSystemLogByLogId(logId);
        new Expectations() {
            {
                SystemLog log = mongoTemplate.findOne((Query) any, SystemLog.class);
                result = null;
            }
        };
        String logIdInfo = "222";
        Result result1 = logService.getSystemLogByLogId(logIdInfo);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:41
     */
    @Test
    public void countByLogType() {
        QueryCondition queryCondition = new QueryCondition();
        logService.countByLogType(queryCondition);


       queryCondition.setFilterConditions(new ArrayList<>());
        logService.countByLogType(queryCondition);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:41
     */
    @Test
    public void countByOperateType() {
        QueryCondition queryCondition = new QueryCondition();
        logService.countByOperateType(queryCondition);


        queryCondition.setFilterConditions(new ArrayList<>());
        logService.countByOperateType(queryCondition);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:41
     */
    @Test
    public void countBySecurityType() {
        QueryCondition queryCondition = new QueryCondition();
        logService.countBySecurityType(queryCondition);


        queryCondition.setFilterConditions(new ArrayList<>());
        logService.countBySecurityType(queryCondition);
    }

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportOperateLog() {
        ExportDto exportDto = new ExportDto();
        logService.exportOperateLog(exportDto);
        new Expectations() {
            {
                operateLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        try {
            logService.exportOperateLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                operateLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("");
            }
        };
        try {
            logService.exportOperateLog(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                operateLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        try {
            logService.exportOperateLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                operateLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new Exception();
            }
        };
        try {
            logService.exportOperateLog(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportSysLog() {
        ExportDto exportDto = new ExportDto();
        logService.exportSysLog(exportDto);
        new Expectations() {
            {
                sysLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        try {
            logService.exportSysLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                sysLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("");
            }
        };
        try {
            logService.exportSysLog(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                sysLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        try {
            logService.exportSysLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                sysLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new Exception();
            }
        };
        try {
            logService.exportSysLog(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportSecurityLog() {
        ExportDto exportDto = new ExportDto();
        logService.exportSecurityLog(exportDto);
        new Expectations() {
            {
                securityLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        try {
            logService.exportSecurityLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                securityLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("");
            }
        };
        try {
            logService.exportSecurityLog(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                securityLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        try {
            logService.exportSecurityLog(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                securityLogExport.insertTask((ExportDto) any, anyString, anyString);
                result = new Exception();
            }
        };
        try {
            logService.exportSecurityLog(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * ??????????????????????????????
     *
     */
    @Test
    public void countDangerLeverLog() {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setFilterConditions(new ArrayList<>());
        logService.countDangerLeverLog(queryCondition);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportLogType() {
        ExportDto exportDto = new ExportDto();
        new Expectations(I18nUtils.class, ExportStatisticUtil.class) {
            {
                ExportStatisticUtil.exportProcessing((LogTypeExport) any, (ExportDto) any,
                        anyString, anyString);
                result = ResultUtils.success("1111");
            }
        };
        try {
            logService.exportLogType(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportOperateType() {
        ExportDto exportDto = new ExportDto();
        new Expectations(ExportStatisticUtil.class) {
            {
                ExportStatisticUtil.exportProcessing((OperateTypeExport)any, (ExportDto) any,
                        anyString, anyString);
                result = ResultUtils.success("1111");
            }
        };
        try {
            logService.exportOperateType(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 9:34
     */
    @Test
    public void exportSecurityLevel() {
        ExportDto exportDto = new ExportDto();
        new Expectations(ExportStatisticUtil.class) {
            {
                ExportStatisticUtil.exportProcessing((SecurityLevelExport)any, (ExportDto) any,
                        anyString, anyString);
                result = ResultUtils.success("1111");
            }
        };
        try {
            logService.exportSecurityLevel(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * ?????????mongo????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/10 10:05
     */
    @Test
    public void castToMongoQuery() {
        QueryCondition queryCondition = new QueryCondition();
        Query query = new Query();
        logService.castToMongoQuery(queryCondition , query);
    }
}