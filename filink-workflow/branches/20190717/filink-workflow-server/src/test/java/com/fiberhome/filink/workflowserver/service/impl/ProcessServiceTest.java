package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowserver.bean.ActProcess;
import com.fiberhome.filink.workflowserver.constant.Constants;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.service.ActProcessService;
import com.fiberhome.filink.workflowserver.service.ActProcessTaskConfigService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/6/30 17:48
 */
@RunWith(JMockit.class)
@SpringBootTest
public class ProcessServiceTest {

    /**
     * 流程逻辑层测试类
     */
    @Tested
    private ProcessServiceImpl processService;

    /**
     * 流程任务实现类
     */
    @Injectable
    private ActProcessService actProcessService;

    @Injectable
    private RepositoryService repositoryService;

    @Injectable
    private RuntimeService runtimeService;

    @Injectable
    private TaskService taskService;

    @Injectable
    private ActProcessTaskConfigService actProcessTaskConfigService;

    @Injectable
    private ProcBaseFeign procBaseFeign;

    @Injectable
    private Constants constants;



    /**
     * 发布流程模板
     * @author hedongwei@wistronits.com
     * @date  2019/6/30 20:02
     */
    @Test
    public void deploy() {
        new Expectations() {
            {

                actProcessService.getActProcess((ActProcess) any);
                List<ActProcess> actProcessInfoList = new ArrayList<>();
                ActProcess actProcess = new ActProcess();
                actProcess.setIsDeploy("0");
                actProcessInfoList.add(actProcess);
                result = actProcessInfoList;
            }
        };
        processService.deploy();


        new Expectations() {
            {
                try {
                    //发布的对象
                    Deployment deployment = repositoryService.createDeployment().name((String) any)
                            .addClasspathResource("processes/" + "process.bpmn")
                            .addClasspathResource("processes/" + "process"
                                    .split("[.]")[0] + ".png").deploy();
                    Deployment deploymentInfo = new DeploymentEntityImpl();
                    ((DeploymentEntityImpl) deploymentInfo).setKey("name");
                    result = deploymentInfo;
                    //更改发布的状态
                } catch (Exception e) {

                }
            }
        };
        ActProcess actProcess = new ActProcess();
        actProcess.setFileName("process.bpmn");
        processService.deployProcInfoOne(actProcess);

    }


    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 14:30
     */
    @Test
    public void startProcess() {
        new Expectations() {
            {
                runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(anyString).singleResult();
                result = null;
            }
        };
        StartProcessReq req = new StartProcessReq();
        req.setProcCode("111");
        req.setProcType("process");
        processService.startProcess(req);


        new Expectations() {
            {
                ProcessInstance info = runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(anyString).singleResult();
                result = info;
            }
        };
        StartProcessReq reqInfo = new StartProcessReq();
        reqInfo.setProcCode("111");
        reqInfo.setProcType("process");
        Map<String, Object> variables = new HashMap<>();
        variables.put("info","2222");
        reqInfo.setVariables(variables);
        processService.startProcess(reqInfo);
    }


    /**
     * 提交任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 14:30
     */
    @Test
    public void completeTask() {
        CompleteTaskReq req = new CompleteTaskReq();
        req.setTaskId("1");
        new Expectations() {
            {
                processService.getCompleteTaskInfo(req);
                Task task = new TaskEntityImpl();
                ((TaskEntityImpl) task).setProcessDefinitionId("11");
                result = task;
            }
        };
        processService.completeTask(req);
    }

    /**
     * 领办任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 14:30
     */
    @Test
    public void claim() {
        ClaimTaskReq req = new ClaimTaskReq();
        Assert.assertTrue(processService.claim(req).getCode() == 0);
    }

    /**
     * 会签任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 14:30
     */
    @Test
    public void delegateTask() {
        DelegateTaskReq req = new DelegateTaskReq();
        req.setTaskId("1");
        req.setDelegate("1");
        new Expectations() {
            {
                taskService.createTaskQuery().taskId(anyString).singleResult();
                Task task = new TaskEntityImpl();
                ((TaskEntityImpl) task).setAssignee("1");
                result = task;
            }
        };
        processService.delegateTask(req);



        req.setDelegate("2");
        new Expectations() {
            {
                taskService.createTaskQuery().taskId(anyString).singleResult();
                Task task = new TaskEntityImpl();
                ((TaskEntityImpl) task).setAssignee("1");
                result = task;
            }
        };
        processService.delegateTask(req);
    }

    /**
     * 转办任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 16:14
     */
    @Test
    public void turnTask() {
        TurnTaskReq req = new TurnTaskReq();
        req.setMessage("222");
        req.setTaskId("1");
        req.setTurnUser("2");
        new Expectations() {
            {
                taskService.createTaskQuery().taskId(anyString).singleResult();
                TaskEntityImpl taskInfo = new TaskEntityImpl();
                taskInfo.setProcessInstanceId("2");
                taskInfo.setAssignee("1");
                result = taskInfo;
            }
        };
        processService.turnTask(req);
    }

    /**
     * 修改工单状态
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 16:14
     */
    @Test
    public void updateProcBaseStatusById() {
        new Expectations() {
            {
                procBaseFeign.updateProcBaseStatusById((ProcBase) any);
                Result resultInfo = ResultUtils.success();
                result = resultInfo;
            }
        };
        ProcBase procBase = new ProcBase();
        procBase.setProcId("wewqewwrewrerer");
        processService.updateProcBaseStatusById(procBase);
    }

    /**
     *
     * 提醒下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 16:22
     */
    @Test
    public void noticeDownloadUser() {
        processService.noticeDownloadUser("qeqweqwewqewqe", "2");
    }

    /**
     * 获取流程备注参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 16:28
     */
    @Test
    public void getMessage() {
        String message = "info";
        processService.getMessage(message);
    }
}
