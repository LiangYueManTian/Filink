package com.fiberhome.filink.log_server.service.impl;


import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.log_server.bean.OperateLog;
import com.fiberhome.filink.log_server.bean.SecurityLog;
import com.fiberhome.filink.log_server.bean.SystemLog;
import com.fiberhome.filink.log_server.req.AddOperateLogReq;
import com.fiberhome.filink.log_server.req.AddSecurityLogReq;
import com.fiberhome.filink.log_server.req.AddSystemLogReq;
import com.fiberhome.filink.log_server.service.FunctionDangerLevelConfigService;
import com.fiberhome.filink.log_server.utils.LogConstants;
import com.fiberhome.filink.log_server.utils.LogI18nCast;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
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
     * 测试对象 LogService
     */
    @Tested
    private LogServiceImpl logService;

    /**
     * 自动注入MongoTemplate
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 自动注入FunctionDangerLevelConfigService
     */
    @Injectable
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;

    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;

    /**
     * 新增操作日志
     */
    @Test
    public void addOperateLog() {
        AddOperateLogReq operateLogReq = new AddOperateLogReq();
        operateLogReq.setFunctionCode("1000");
        operateLogReq.setOptType("add");
        logService.insertOperateLog(operateLogReq);
    }

    /**
     * 新增安全日志
     */
    @Test
    public void addSecurityLog() {
        AddSecurityLogReq securityLogReq = new AddSecurityLogReq();
        securityLogReq.setFunctionCode("1000");
        securityLogReq.setOptType("add");
        logService.insertSecurityLog(securityLogReq);
    }

    /**
     * 新增系统日志
     */
    @Test
    public void addSystemLog() {
        AddSystemLogReq systemLogReq = new AddSystemLogReq();
        systemLogReq.setFunctionCode("1000");
        systemLogReq.setOptType("add");
        logService.insertSystemLog(systemLogReq);
    }

    /**
     * 获取危险级别
     */
    @Test
    public void getDangerLevel() {
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_PROMPT);
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_DANGER);
        LogI18nCast.getDangerLevel(LogConstants.DANGER_LEVEL_GENERAL);
    }

    /**
     * 获取操作返回值
     */
    @Test
    public void getOptResult() {
        LogI18nCast.getOptResult(LogConstants.OPT_RESULT_SUCCESS);
        LogI18nCast.getOptResult(LogConstants.OPT_RESULT_FAILURE);
    }

    /**
     * 获取操作类型
     */
    @Test
    public void getOptType() {
        LogI18nCast.getOptType(LogConstants.OPT_TYPE_PDA);
        LogI18nCast.getOptType(LogConstants.OPT_TYPE_WEB);
    }




    /**
     * 查询操作日志列表
     */
    @Test
    public void queryListOperateLogByPage() {
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
     * 查询安全日志列表
     */
    @Test
    public void queryListSecurityLogByPage() {
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
     * 查询系统日志列表
     */
    @Test
    public void queryListSystemLogByPage() {
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
     * 查询操作日志详情
     */
    @Test
    public void getOperateLogByLogId() {
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
     * 查询安全日志详情
     */
    @Test
    public void getSecurityLogByLogId() {
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
     * 查询系统日志详情
     */
    @Test
    public void getSystemLogByLogId() {
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


}