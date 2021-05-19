package com.fiberhome.filink.workflowserver.listener;

import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowserver.bean.ActProcessTaskConfig;
import com.fiberhome.filink.workflowserver.constant.ProcessConstants;
import com.fiberhome.filink.workflowserver.service.ActProcessTaskConfigService;
import com.fiberhome.filink.workflowserver.service.ProcessService;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * 流程全局监听类
 * @author hedongwei@wistronits.com
 * @date 2019/2/19 14:42
 */
@Service
public class ActivitiStatusListener implements TaskListener, ExecutionListener {

    private ActProcessTaskConfigService actProcessTaskConfigService;

    private ProcBaseFeign procBaseFeign;

    private HistoryService historyService;

    private RuntimeService runtimeService;

    private TaskService taskService;

    private ProcessService processService;





    /**
     * 监听器实现类
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:47
     * @param task 监听对象
     */
    @Override
    public void notify(DelegateTask task) {

        //自动注入对象
        this.acivitiListenerAutowired();

        //当前节点名称
        String taskDefName = task.getTaskDefinitionKey();

        String regex = ":";

        //流程类型
        String procType = task.getProcessDefinitionId().split(regex)[0];

        //获取代理信息
        DelegateExecution delegateExecution = task.getExecution();

        //业务编号
        String procId =  delegateExecution.getProcessInstanceBusinessKey();

        String assign = task.getAssignee();

        //下载节点需要推送信息
        if (ProcessConstants.PROC_POINT_DEPARTMENT.equals(taskDefName)) {
            assign = null;
            //需要进行工单通知推送,调用工单推送的方法
            processService.noticeDownloadUser(procId, assign);
        }

        //转办节点需要推送消息
        if (ProcessConstants.PROC_POINT_TURN_USER.equals(taskDefName)) {
            //需要进行工单通知推送，并推送给指定的人
            processService.noticeDownloadUser(procId, assign);
        }

        //改变业务流程状态
        this.changeProcStatus(procType, taskDefName, procId);

    }

    /**
     * 获取activiti运行对象
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:48
     * @param engine 流程对象
     * @return 运行对象
     */
    private RuntimeService getRuntimeService(ProcessEngine engine) {
        if (null == runtimeService) {
            runtimeService = engine.getRuntimeService();
        }
        return runtimeService;
    }

    /**
     * 获取activiti任务对象
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:48
     * @param engine 流程对象
     * @return 任务对象
     */
    public TaskService getTaskService(ProcessEngine engine) {
        if (null == taskService) {
            taskService = engine.getTaskService();
        }
        return taskService;
    }

    /**
     * 获取activiti历史对象
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:48
     * @param engine 流程对象
     * @return 历史对象
     */
    public HistoryService getHistoryService(ProcessEngine engine) {
        if (null == historyService) {
            historyService = engine.getHistoryService();
        }
        return historyService;
    }

    /**
     * 注入bean对象
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:46
     */
    public void autowireBean() {
        //判断spring容器是否为空
        if (null != SpringUtils.getApplicationContext()) {
            if (null == actProcessTaskConfigService) {
                actProcessTaskConfigService = SpringUtils.getBean(ActProcessTaskConfigService.class);
            }
            if (null == procBaseFeign) {
                procBaseFeign = SpringUtils.getBean(ProcBaseFeign.class);
            }

            if (null == processService) {
                processService = SpringUtils.getBean(ProcessService.class);
            }
        }
    }

    /**
     * 监听器注入
     */
    public void acivitiListenerAutowired() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //运行service
        this.getRuntimeService(engine);
        //任务service
        this.getTaskService(engine);
        //历史service
        this.getHistoryService(engine);
        //注入bean对象
        this.autowireBean();
    }

    /**
     * 变更工单的状态
     * @author hedongwei@wistronits.com
     * @date  2019/4/3 17:31
     * @param procType 流程类型
     * @param taskDefName 节点名称
     * @param procId 流程编号
     */
    public void changeProcStatus(String procType, String taskDefName, String procId) {
        if (null != procType && !"".equals(procType)) {
            //查询单据的流程信息
            ActProcessTaskConfig taskConfig = new ActProcessTaskConfig();
            taskConfig.setProcType(procType);
            taskConfig.setProcTaskName(taskDefName);
            List<ActProcessTaskConfig> configList = actProcessTaskConfigService.queryListProcessTaskConfig(taskConfig);
            if (!ObjectUtils.isEmpty(configList)) {
                if (!ObjectUtils.isEmpty(configList.get(0))) {
                    String procStatus = configList.get(0).getProcStatusCode();
                    //更改单据状态
                    ProcBase procBase = new ProcBase();
                    procBase.setProcId(procId);
                    procBase.setStatus(procStatus);
                    //修改流程状态
                    processService.updateProcBaseStatusById(procBase);
                }
            }
        }
    }

    /**
     * 监听器实现类
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 17:47
     * @param execution 监听对象
     */
    @Override
    public void notify(DelegateExecution execution) {

        //自动注入对象
        this.acivitiListenerAutowired();

        String regex = ":";
        //流程类型
        String procType = execution.getProcessDefinitionId().split(regex)[0];

        //业务编号
        String procId =  execution.getProcessInstanceBusinessKey();

        //节点id
        String taskDefName = execution.getCurrentFlowElement().getId();

        //改变业务流程状态
        this.changeProcStatus(procType, taskDefName, procId);
    }
}
