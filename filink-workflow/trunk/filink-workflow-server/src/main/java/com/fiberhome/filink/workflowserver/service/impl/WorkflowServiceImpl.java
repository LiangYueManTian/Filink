package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.WorkflowBean;
import com.fiberhome.filink.workflowserver.constant.Constants;
import com.fiberhome.filink.workflowserver.service.WorkflowService;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 
 * @author hedongwei@wistronits.com
 * @date 2018/02/10下午5:04:26
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

	private static final String PROCESS_DEFINITION_KEY = "myProcess";


	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private IdentityService identityService;

	
	/**
	 * 部署流程定义 （zip格式）
	 */
	@Override
	public void saveNewDeploy(File file, String filename) {
		try {
			// 将File类型的文件转换成流
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment().name(filename).addZipInputStream(zipInputStream).deploy();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("deploy proc error", e);
		}
	}

	/**
	 * 查询部署对象信息 对应表act_re_deployment
	 */
	@Override
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService.createDeploymentQuery().orderByDeploymenTime().asc().list();
		return list;
	}

	/**
	 * 查询流程定义信息 对应表act_re_prodef
	 */
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		// 根据版本号升序排列
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion()
				.asc()
				.list();
		return list;
	}

	/**
	 * 使用部署对象id和资源图片名称，获取流程图的输入流
	 */
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	/**
	 * 根据部署对象id删除流程定义
	 */
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		// 最后一个参数为true时表示级联删除，会删除掉和当前部署的规则相关的信息，包括历史信息；否则为普通删除，如果当前部署的规则还存在正在制作的流程，会抛异常。
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**
	 * 启动流程实例
	 */
	@Override
	public void saveStartProcess(WorkflowBean workflowBean) {
		String id = workflowBean.getId();

		//更新业务表状态信息


        //获取业务信息

		HashMap<String, Object> variables = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

		// 从session中取出当前登录人，设置为提交申请任务的办理人
        identityService.setAuthenticatedUserId("test");
		variables.put("applyUser","");
		variables.put("days", "1");
		// 将启动的流程实例关联业务
		String businessKey = id;
		runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey, variables);
	}

	/**
	 * 查询用户的待办任务
	 */
	@Override
	public List<Task> findTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery().taskAssignee(name)
				.orderByTaskCreateTime().asc().listPage(0,10);
		return list;
	}

	/**
	 * 根据名称查询数量
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:36
	 * @param name 名称
	 * @return 返回数量
	 */
	@Override
	public int findTaskListByNameCount(String name) {
		Long count = taskService.createTaskQuery().taskAssignee(name).count();
		int retCount = count.intValue();
		return retCount;
	}

	/**
	 * 根据指派人查询历史记录
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 返回历史记录信息
	 */
	@Override
	public List<HistoricTaskInstance> findSignatureRecordList(String userCode) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.taskAssignee(userCode)
				.listPage(0,10);
		return list;
	}

	/**
	 * 根据指派人查询历史记录个数
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 返回历史记录个数
	 */
	@Override
	public int findSignatureRecordCount(String userCode) {
		Long count = historyService.createHistoricTaskInstanceQuery()
				.taskAssignee(userCode)
				.count();
		int retCount = count.intValue();
		return retCount;
	}

	/**
	 * 根据指派人查询流程实例集合
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 流程实例集合
	 */
	@Override
	public List<HistoricProcessInstance> findProcessInstanceList(String userCode) {
		List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().startedBy(userCode).listPage(0, 10);
		return historicProcessInstanceList;
	}

	/**
	 * 根据指派人查询流程实例个数
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 流程实例个数
	 */
	@Override
	public int findProcessInstanceCount(String userCode) {
		Long count = historyService.createHistoricProcessInstanceQuery()
				.startedBy(userCode)
				.count();
		int retCount = count.intValue();
		return retCount;
	}

	/**
	 * 使用任务id获取当前任务节点对应的FormKey中对应的值
	 */
	@Override
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		String url = formData.getFormKey();
		return url;
	}


	/**
	 * 获取当前任务完成之后的连线走向，在前台展示
	 */
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		ArrayList<String> outComeList = new ArrayList<String>();
		// 通过任务id获取任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 通过任务对象获取流程定义id
		String processDefinitionId = task.getProcessDefinitionId();
		// 通过任务对象获取流程实例id
		String processInstanceId = task.getProcessInstanceId();
		// 通过流程实例id获取流程实例对象
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// 通过流程定义id获取流程定义实体（对应.bpmn文件）
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
		// 通过流程实例获取当前活动id
		String activityId = processInstance.getActivityId();
		// 通过当前活动id获取当前活动对象
		return outComeList;
	}

	/**
	 * 通过当前任务id获取历史任务的批注信息
	 */
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
		return comments;
	}

	/**
	 * 通过单据编号获取历史批注信息
	 */
	@Override
	public List<Comment> findCommentByProcCode(String id) {

		HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(id)
				.singleResult();
		String processInstanceId = hpi.getId();
		List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
		return comments;
	}

	/**
	 * 使用任务id获取流程定义对象
	 */
	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId)
				.singleResult();
		return pd;
	}

	/**
	 * 通过当前任务id获取当前活动节点所在的坐标(查看流程进度)
	 */
	@Override
	public Map<String, Integer> findCoordinateByTask(String taskId) {
		HashMap<String, Integer> map = new HashMap<String , Integer>(Constants.MAP_INIT_NUM);
		return map;
	}

	/**
	 * 根据业务单据的id获取当前流程图
	 */
	@Override
	public InputStream findImageInputStream(String id) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(id)
				.singleResult();
		String processDefinitionId = processInstance.getProcessDefinitionId();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		InputStream stream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				processDefinition.getDiagramResourceName());
		return stream;
	}

	/**
	 * 根据业务单据的id获取当前活动节点(查看流程进度)
	 */
	@Override
	public Map<String, Integer> findCoordinateByProcCode(String procCode) {
		Map<String, Integer> map = new HashMap<>(Constants.MAP_INIT_NUM);

		return map;
	}

	/**
	 * 通过流程编码获取流程实例
	 */
	@Override
	public ProcessDefinition findProcessDefinitionByProcCode(String procCode) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(procCode).singleResult();
		String processDefinitionId = processInstance.getProcessDefinitionId();
		return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
	}
}
