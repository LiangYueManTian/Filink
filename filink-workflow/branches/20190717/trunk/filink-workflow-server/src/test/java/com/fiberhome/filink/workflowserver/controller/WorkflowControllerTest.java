package com.fiberhome.filink.workflowserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.service.WorkflowService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 流程控制层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/5/5 10:58
 */
@SpringBootTest
@RunWith(JMockit.class)
public class WorkflowControllerTest {

    @Tested
    private WorkflowController workflowController;

    @Injectable
    private WorkflowService workflowService;

    @Injectable
    private TaskService taskService;


    /**
     * 获得当前任务业务key
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 11:07
     */
    @Test
    public void getNowTaskByBusinessKey() {
        GetNowTaskByBusinessKeyReq req = new GetNowTaskByBusinessKeyReq();
        Result returnResult = workflowController.getNowTaskByBusinessKey(req);
        Assert.assertTrue(0 == returnResult.getCode());

        GetNowTaskByBusinessKeyReq reqInfo = new GetNowTaskByBusinessKeyReq();
        reqInfo.setBussinessKey("1");
        Result returnResultInfo = workflowController.getNowTaskByBusinessKey(reqInfo);
        Assert.assertTrue(0 == returnResultInfo.getCode());

        GetNowTaskByBusinessKeyReq reqParam = null;
        Result returnParamResultInfo = workflowController.getNowTaskByBusinessKey(reqParam);
        Assert.assertTrue(0 == returnParamResultInfo.getCode());
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询待办任务个数
     * date 9:38 2018/11/28
     */

    @Test
    public void getPendingTaskCount() {
        GetPendingTaskCountReq req = new GetPendingTaskCountReq();
        Map<String, Object> retCount = workflowController.getPendingTaskCount(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(retCount) == true);

        GetPendingTaskCountReq reqInfo = new GetPendingTaskCountReq();
        reqInfo.setUserCode("1");
        Map<String, Object> retCountInfo = workflowController.getPendingTaskCount(reqInfo);
        Assert.assertTrue(!ObjectUtils.isEmpty(retCountInfo) == true);
    }


    /**
     * @author hedongwei@wistronits.com
     * description 查询签核记录个数
     * date 9:36 2018/11/28
     */

    @Test
    public void getSignatureRecordCount() {
        GetSignatureRecordCountReq req = new GetSignatureRecordCountReq();
        Map<String, Object> reqCount = workflowController.getSignatureRecordCount(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(reqCount) == true);

        GetSignatureRecordCountReq reqInfo = new GetSignatureRecordCountReq();
        reqInfo.setUserCode("1");
        Map<String, Object> reqCountInfo = workflowController.getSignatureRecordCount(reqInfo);
        Assert.assertTrue(!ObjectUtils.isEmpty(reqCountInfo) == true);
    }

    /**
     * 查询签核记录个数
     * @author hedongwei@wistronits.com
     * date 9:36 2018/11/28
     */
    @Test
    public void getApplyRecordCount() {
        GetApplyRecordCountReq req = new GetApplyRecordCountReq();
        req.setUserCode("1");
        Map<String, Object> applyRecordCount = workflowController.getApplyRecordCount(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(applyRecordCount.get("count")) == true);
    }

    /**
     * 待办信息记录
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 11:24
     */
    @Test
    public void getPendingTaskList() {
        Task task = new TaskEntityImpl();
        ((TaskEntityImpl) task).setId("id");
        new Expectations() {
            {
                List<Task> list = workflowService.findTaskListByName((String)any);
                result = task;
            }
        };
        GetApplyRecordCountReq reqParam = new GetApplyRecordCountReq();
        reqParam.setUserCode("1");
        workflowController.getApplyRecordCount(reqParam);

        GetPendingTaskListReq req = new GetPendingTaskListReq();
        req.setUserCode("1");
        Map<String, Object> pendingTaskList = workflowController.getPendingTaskCountList(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(pendingTaskList.get("total")) == true);
    }

    /**
     * 签核记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 11:30
     */
    @Test
    public void getSignatureRecordList() {
        GetSignatureRecordListReq req = new GetSignatureRecordListReq();
        req.setUserCode("1");
        Map<String, Object> signatureRecordList = workflowController.getSignatureRecordList(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(signatureRecordList.get("total")) == true);
    }


    /**
     * 申请记录信息
     * @author hedongwei@wistronits.com
     * date 9:28 2018/11/28
     */
    @Test
    public void getApplyRecordList() {
        GetApplyRecordListReq req = new GetApplyRecordListReq();
        req.setUserCode("1");
        Map<String , Object> info = workflowController.getApplyRecordList(req);
        Assert.assertTrue(!ObjectUtils.isEmpty(info.get("total")) == true);
    }


}

