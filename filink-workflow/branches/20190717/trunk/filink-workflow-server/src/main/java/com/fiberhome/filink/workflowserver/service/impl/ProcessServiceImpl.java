package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.NoticeDownloadUserReq;
import com.fiberhome.filink.workflowserver.bean.ActProcess;
import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.service.ActProcessService;
import com.fiberhome.filink.workflowserver.service.ActProcessTaskConfigService;
import com.fiberhome.filink.workflowserver.service.ProcessService;
import com.fiberhome.filink.workflowserver.constant.Constants;
import com.fiberhome.filink.workflowserver.constant.ProcessConstants;
import com.fiberhome.filink.workflowserver.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;


/**
 * @author hedongwei@wistronits.com
 * description 流程逻辑层
 * @date 2018/11/30 10:27
 */
@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActProcessTaskConfigService actProcessTaskConfigService;

    @Autowired
    private ProcBaseFeign procBaseFeign;

    @Value("${updateProcStatusSleepTime}")
    private String updateProcStatusSleepTime;

    /**
     * 发布流程模板
     * @author hedongwei@wistronits.com
     * @date 9:51 2019/2/12
     */
    @Override
    public void deploy() {
        //查询是否存在未发布的流程模板
        ActProcess actProcess = new ActProcess();
        actProcess.setIsDeploy(Constants.NOT_DEPLOY);
        List<ActProcess> actProcessList = actProcessService.getActProcess(actProcess);
        if (!ObjectUtils.isEmpty(actProcessList)) {
            //发布流程
            for (ActProcess actProcessOne : actProcessList) {
                //发布单个流程
                if (!deployProcInfoOne(actProcessOne)) {
                    continue;
                }
            }
        }
    }

    /**
     * 发布单个流程信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 18:01
     * @param actProcessOne 流程配置对象
     * @return 发布对象是否成功
     */
    public boolean deployProcInfoOne(ActProcess actProcessOne) {
        //文件名称不为空时
        if (StringUtils.isNotEmpty(actProcessOne.getFileName())) {
            try {
                //发布的对象
                Deployment deployment = repositoryService.createDeployment().name(actProcessOne.getProcName())
                        .addClasspathResource("processes/" + actProcessOne.getFileName())
                        .addClasspathResource("processes/" + actProcessOne.getFileName()
                                .split("[.]")[0] + ".png").deploy();

                if (null != deployment) {
                    //更改发布表的状态
                    ActProcess updateParam = new ActProcess();
                    updateParam.setProcId(actProcessOne.getProcId());
                    updateParam.setIsDeploy(Constants.DEPLOY);
                    actProcessService.updateActProcess(updateParam);
                }
                //更改发布的状态
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 启动流程参数
     * @return Result 启动流程结果
     */
    @Override
    public Result startProcess(StartProcessReq req) {
        //插入变量信息
        //获取变量信息
        Map<String, Object> variables = req.getVariables();

        //获取流程状态
        String procType = req.getProcType();


        //查询流程状态是否存在
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(req.getProcCode()).singleResult();

        if (ObjectUtils.isEmpty(processInstance)) {
            //根据不同的内容发起不同的流程
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(procType, req.getProcCode(), variables);

            //设置流程标题信息
            runtimeService.setProcessInstanceName(instance.getId(), req.getTitle());
        } else {
            //已经存在的工单只需要办结就行
            variables.put(ProcessConstants.PROC_POINT_ASSIGNED, req.getProcCode());
            CompleteTaskReq completeTaskReq = new CompleteTaskReq();
            BeanUtils.copyProperties(req, completeTaskReq);
            this.completeTask(completeTaskReq);
        }
        //返回执行结果
        return ResultUtils.success();
    }

    /**
     * 完成任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/14
     * @param req 完成任务参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result completeTask(CompleteTaskReq req) {
        //需要办结的任务编号
        String taskId = req.getTaskId();

        //获取任务实例
        Task task = this.getCompleteTaskInfo(req);

        if (null != task) {
            System.out.println(task.toString());

            //获取任务正在运行的instanceId
            String processInstanceId = task.getProcessInstanceId();

            //获取办理的信息
            String message = this.getMessage(req.getMessage());

            //给当前任务添加批注信息
            taskService.addComment(taskId, processInstanceId, message);

            //办结流程
            this.completeTaskProcess(req, task);

        }
        return ResultUtils.success();
    }

    /**
     * 获取完结任务流程任务实例
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 18:04
     * @param req 完结任务参数
     * @return 流程任务实例
     */
    public Task getCompleteTaskInfo(CompleteTaskReq req) {
        Task task = null;
        if (StringUtils.isEmpty(req.getTaskId())) {
            //没有taskId的，通过procCode 找到对应的taskId
            task = taskService.createTaskQuery().processInstanceBusinessKey(req.getProcCode()).singleResult();
        } else {
            //查询任务信息
            task = taskService.createTaskQuery().taskId(req.getTaskId()).singleResult();
        }
        return task;
    }

    /**
     * 办结任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 18:12
     * @param req 办结任务参数
     * @param task 流程任务实例
     */
    public void completeTaskProcess(CompleteTaskReq req, Task task) {

        //获取任务正在运行的instanceId
        String processInstanceId = task.getProcessInstanceId();

        //获取owner字段
        String owner = task.getOwner();

        //获取变量的信息
        Map<String, Object> variables = req.getVariables();

        if (null != owner && !"".equals(owner) && !task.getAssignee().equals(owner)) {
            //办理会签任务
            taskService.resolveTask(task.getId(), variables);
        } else {
            //正常办结任务
            taskService.complete(task.getId(), variables);

            //查询是否还有运行的任务
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (null != processInstance) {
                //获取状态
                String status = this.getRuntimeProcType(processInstance.getId());
            }
        }
    }

    /**
     *  领办任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 领办任务的参数
     * @return  Result 返回的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result claim(ClaimTaskReq req) {
        //任务编号
        String taskId = req.getTaskId();

        //领办人
        String userCode = req.getUserCode();

        //领办任务
        taskService.claim(taskId, userCode);
        return ResultUtils.success();
    }

    /**
     *  会签任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/13
     * @param req 会签任务的参数
     * @return  Result 返回的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delegateTask(DelegateTaskReq req) {
        String taskId = req.getTaskId();
        String delegate = req.getDelegate();
        //获取消息
        String message = this.getMessage(req.getMessage());

        //查询任务信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (null != task) {
            if (task.getAssignee().equals(delegate)) {
                return ResultUtils.warn(ResultCode.FAIL, "delegate user not doing user same");
            }
            //获取任务正在运行的instanceId
            String processInstanceId = task.getProcessInstanceId();

            //会签任务
            taskService.delegateTask(taskId, delegate);

            //添加备注信息
            taskService.addComment(taskId, processInstanceId, message);
        }
        return ResultUtils.success();
    }

    /**
     *  转办任务
     * @date 2019/2/13
     * @param req 转办任务的参数
     * @return  Result 返回的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result turnTask(TurnTaskReq req) {

        //获取备注信息
        String message = this.getMessage(req.getMessage());

        //任务编号
        String taskId = req.getTaskId();

        //转办人
        String turnUser = req.getTurnUser();

        //查询任务信息
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        //获取任务正在运行的instanceId
        String processInstanceId = task.getProcessInstanceId();

        //转办任务
        taskService.setAssignee(taskId, turnUser);

        //添加备注信息
        taskService.addComment(taskId, processInstanceId, message);

        return ResultUtils.success();
    }


    /**
     * 修改流程状态
     * @author hedongwei@wistronits.com
     * @date  2019/4/3 19:28
     * @param procBase 修改对象的参数
     */
    @Override
    @Async
    public void updateProcBaseStatusById(ProcBase procBase) {

        Result result = procBaseFeign.updateProcBaseStatusById(procBase);
        System.out.println(result.getCode());
    }


    /**
     * 提醒可以下载该工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 12:16
     * @param procId 工单编号
     */
    @Override
    @Async
    public void noticeDownloadUser(String procId, String assign) {
        NoticeDownloadUserReq req = new NoticeDownloadUserReq();
        req.setAssign(assign);
        req.setProcId(procId);
        //提醒能够下载人
        procBaseFeign.noticeDownloadUser(req);
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/2/25 16:19
     * @param instanceId 当前流程正在执行的节点
     */
    @Override
    public String getRuntimeProcType(String instanceId) {
        String statusName = "";
        //业务key
        Task task = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();
        //获取该节点的状态信息
        //获取节点的名称
        String taskDefinitionKey = task.getTaskDefinitionKey();
        //获取流程的类型
        String procType;
        if (!ObjectUtils.isEmpty(task.getProcessDefinitionId())) {
            procType = task.getProcessDefinitionId().split(":")[0];
        } else {
            return statusName;
        }
        //查询需要更改的状态值
        ActProcessTaskConfig actProcessTaskConfig = new ActProcessTaskConfig();
        actProcessTaskConfig.setProcType(procType);
        actProcessTaskConfig.setProcTaskName(taskDefinitionKey);
        List<ActProcessTaskConfig> actProcessTaskConfigList = actProcessTaskConfigService.queryListProcessTaskConfig(actProcessTaskConfig);
        if (null != actProcessTaskConfigList && 0 < actProcessTaskConfigList.size()) {
            ActProcessTaskConfig actProcessTaskConfigInfo = actProcessTaskConfigList.get(0);
            //获取状态值
            statusName = actProcessTaskConfigInfo.getProcStatusCode();
        }
        return statusName;
    }

    /**
     * 获得流程备注参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:54
     * @param addMessage 新增的消息
     * @return 流程备注信息
     */
    public String getMessage(String addMessage) {
        String message = "";
        if (StringUtils.isNotEmpty(addMessage)) {
            message = addMessage;
        }
        return message;
    }


}
