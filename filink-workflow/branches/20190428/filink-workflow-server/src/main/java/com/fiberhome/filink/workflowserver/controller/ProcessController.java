package com.fiberhome.filink.workflowserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.resp.DeployTaskResp;
import com.fiberhome.filink.workflowserver.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author hedongwei@wistronits.com
 * description 流程控制层
 * @date 2018/11/28 12:24
 */
@RestController
@Slf4j
@RequestMapping("/process")
public class ProcessController {
    @Resource
    private ProcessEngineFactoryBean processEngineFactoryBean;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;


    @Autowired
    private ProcessService processService;

    protected static Logger logger = LoggerFactory.getLogger(ProcessController.class);


    /**
     * @author hedongwei@wistronits.com
     * description 流程发布
     * @date 18:07 2018/11/29
     */
    @RequestMapping("/deploy")
    public @ResponseBody DeployTaskResp deploy() {
        DeployTaskResp resp = new DeployTaskResp();
        processService.deploy();
        return resp;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 启动流程
     * @date 15:54 2018/11/28
     * @param req 启动流程
     */
    @PostMapping("/startProcess")
    public Result startProcess(@RequestBody StartProcessReq req) {
        return processService.startProcess(req);
    }


    /**
     * 任务办结
     * @author hedongwei@wistronits.com
     * @date 16:21 2018/11/28
     * @param req 办结的参数
     * @return CompleteTaskResp
     */
    @PostMapping("/complete")
    public Result completeTask(@RequestBody CompleteTaskReq req) {
        return processService.completeTask(req);
	}

	/**
	 * @author hedongwei@wistronits.com
	 * description 领办任务
	 * date 17:24 2018/11/28
	 * param [req]
	 */
	@PostMapping("/claim")
    public Result claim(@RequestBody ClaimTaskReq req) {
        return processService.claim(req);
    }


    /**
     * @author hedongwei@wistronits.com
     * description 会签任务
     * date 16:53 2018/11/28
     * param [req]
     **/
	@PostMapping("/delegate")
    public Result delegateTask(@RequestBody DelegateTaskReq req) {
        return processService.delegateTask(req);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 转办任务
     * date 10:48 2018/11/29
     * param [req]
     **/
    @PostMapping("/turnTask")
    public Result turnTask(@RequestBody TurnTaskReq req) {
        return processService.turnTask(req);
    }



    /**
     * @author hedongwei@wistronits.com
     * description 查看流程图信息
     * date 12:17 2018/11/28
     * param [deploymentId, response]
     */
    /*@RequestMapping("/viewProcImg")
    public void viewProcImg(String deploymentId, HttpServletResponse response) {
        //创建任务查询
        //根据任务id查询
        Task task = taskService.createTaskQuery()
                .taskId(deploymentId)
                .singleResult();

        if (null != task) {
            //获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //获取历史流程实例
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            //获取流程图
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

            List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            //高亮环节id集合
            List<String> highLightedActivitis = new ArrayList<String>();
            //高亮线路id集合
            List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

      *//* for(HistoricActivityInstance tempActivity : highLightedActivitList){
           String activityId = tempActivity.getActivityId();
           highLightedActivitis.add(activityId);
       }*//*

            //执行实例
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();
            highLightedActivitis.add(execution.getActivityId());

            //中文显示的是口口口，设置字体就好了
            try {
                InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体", null, null,1.0);
                //单独返回流程图，不高亮显示
                //InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
                // 输出资源内容到相应对象
                byte[] b = new byte[1024];
                int len;
                while ((len = imageStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
            } catch (Exception e) {
                logger.error("查看流程图信息",e);
            }
        }
    }*/

    /**
     * @author hedongwei@wistronits.com
     * description 获取需要高亮的线
     * date 14:45 2019/2/11
     * param [processDefinitionEntity, historicActivityInstances]
     */
    /*private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
        //用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<String>();
        //对历史流程节点进行遍历
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            //得到节点定义的详细信息
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());
            //用以保存后需开始时间相同的节点
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            //将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                //后续第一个节点
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);
                //后续第二个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            //取出节点的所有出去的线
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                //对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                //如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }*/
}
