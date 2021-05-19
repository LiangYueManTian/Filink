package com.fiberhome.filink.workflowserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.resp.DeployTaskResp;
import com.fiberhome.filink.workflowserver.service.ProcessService;
import com.fiberhome.filink.workflowserver.utils.StringUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 流程相关查询测试类
 * @author hedongwei@wistronits.com
 * @date 2019/4/28 19:46
 */

@RunWith(JMockit.class)
@SpringBootTest
public class ProcessControllerTest {

    /**
     * 被测试的对象
     */

    @Tested
    private ProcessController processController;

    @Injectable
    private ProcessEngineFactoryBean processEngineFactoryBean;

    @Injectable
    private RepositoryService repositoryService;

    @Injectable
    private RuntimeService runtimeService;

    @Injectable
    private TaskService taskService;

    @Injectable
    private HistoryService historyService;

    @Injectable
    private ProcessEngineConfiguration processEngineConfiguration;

    @Injectable
    private ProcessService processService;


    /**
     * 发布的方法
     * @author hedongwei@wistronits.com
     * @date  2019/4/30 17:31
     */

    @Test
    public void deploy() {
        String info = "";
        StringUtils.formatLike(info);
        List<String> addList = new ArrayList<>();
        addList.add(info);
        StringUtils.filterWhite(addList);
        info = "1";
        StringUtils.formatLike(info);
        addList = new ArrayList<>();
        addList.add(info);
        StringUtils.filterWhite(addList);


        new Expectations() {
            {
                processService.deploy();
                Result result = ResultUtils.success(new Object());
            }
        };
        DeployTaskResp deployTaskResp = processController.deploy();
        deployTaskResp.setCode("code");
        deployTaskResp.setMessage("message");
        deployTaskResp.getCode();
        deployTaskResp.getMessage();
    }


    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date  2019/4/30 17:32
     */
    @Test
    public void startProcess() {
        StartProcessReq req = new StartProcessReq();
        req.setProcCode("kkkkkkkkk222");
        Map variables = new HashMap();
        variables.put("department", "222");
        req.setVariables(variables);
        processController.startProcess(req);
    }

    /**
     * 办结任务
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 10:50
     */
    @Test
    public void completeTask() {
        CompleteTaskReq req = new CompleteTaskReq();
        req.setProcCode("1212121221221");
        Map variables = new HashMap();
        variables.put("department", "222");
        req.setVariables(variables);
        processController.completeTask(req);
    }
    
    /**
     * 领办任务
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 10:53
     */
    @Test
    public void claim() {
        ClaimTaskReq req = new ClaimTaskReq();
        req.setProcCode("1212121221221");
        req.setUserCode("1");
        processController.claim(req);
    }

    /**
     * 会签任务
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 10:55
     */
    @Test
    public void delegate() {
        DelegateTaskReq req = new DelegateTaskReq();
        req.setUserCode("1");
        processController.delegateTask(req);
    }

    /**
     * 会签任务
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 10:55
     */
    @Test
    public void turnTask() {
        TurnTaskReq req = new TurnTaskReq();
        req.setProcCode("1212121221221");
        req.setUserCode("1");
        req.setTurnUser("admin");
        processController.turnTask(req);
    }

}

