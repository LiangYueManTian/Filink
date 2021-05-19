package com.fiberhome.filink.logserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;
import com.fiberhome.filink.logserver.service.LogService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * LogController
 *
 * @author hedongwei@wistronits.com
 * create on  2019/1/24
 */
@RunWith(JMockit.class)
public class LogInfoControllerTest {
    /**
     * 测试对象 LogController
     */
    @Tested
    private LogController logInfoController;

    /**
     * 自动注入LogService
     */
    @Injectable
    private LogService logService;
    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private UserFeign userFeign;

    /**
     * 新增操作日志
     */
    @Test
    public void addOperateLog() {
        new Expectations() {
            {
                logService.insertOperateLog((AddOperateLogReq) any);
                Result result = ResultUtils.success(new Object());
            }
        };
        AddOperateLogReq req = new AddOperateLogReq();
        req.setLogId(UUIDUtil.getInstance().UUID32());
        Result result = logInfoController.addOperateLog(req);
    }

    /**
     * 新增安全日志
     */
    @Test
    public void addSecurityLog() {
        new Expectations() {
            {
                logService.insertSecurityLog((AddSecurityLogReq) any);
                Result result = ResultUtils.success(new Object());
            }
        };
        AddSecurityLogReq req = new AddSecurityLogReq();
        req.setLogId(UUIDUtil.getInstance().UUID32());
        Result result = logInfoController.addSecurityLog(req);
    }

    /**
     * 新增系统日志
     */
    @Test
    public void addSystemLog() {
        new Expectations() {
            {
                logService.insertSystemLog((AddSystemLogReq) any);
                Result result = ResultUtils.success(new Object());
            }
        };
        AddSystemLogReq req = new AddSystemLogReq();
        req.setLogId(UUIDUtil.getInstance().UUID32());
        Result result = logInfoController.addSystemLog(req);
    }

    /**
     * 查询操作日志
     */
    @Test
    public void findOperateLog() {
        Result result = logInfoController.findOperateLog(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 查询安全日志
     */
    @Test
    public void findSecurityLog() {
        Result result = logInfoController.findSecurityLog(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 查询系统日志
     */
    @Test
    public void findSystemLog() {
        Result result = logInfoController.findSystemLog(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 根据日志编号查询操作日志
     */
    @Test
    public void getOperateLogByLogId() {
        Result result = logInfoController.getOperateLogByLogId("1111");
        Assert.assertTrue(result.getCode() == 0);

        Result result1 = logInfoController.getOperateLogByLogId("");
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 根据日志编号查询安全日志
     */
    @Test
    public void getSecurityLogByLogId() {
        Result result = logInfoController.getSecurityLogByLogId("1111");
        Assert.assertTrue(result.getCode() == 0);

        Result result1 = logInfoController.getSecurityLogByLogId("");
        Assert.assertTrue(result1.getCode() != 0);
    }


    /**
     * 根据日志编号查询系统日志
     */
    @Test
    public void getSystemLogByLogId() {
        Result result = logInfoController.getSystemLogByLogId("1111");
        Assert.assertTrue(result.getCode() == 0);

        Result result1 = logInfoController.getSystemLogByLogId("");
        Assert.assertTrue(result1.getCode() != 0);
    }

}