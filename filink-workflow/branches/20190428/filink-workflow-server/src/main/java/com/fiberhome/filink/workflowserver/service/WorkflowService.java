package com.fiberhome.filink.workflowserver.service;

import com.fiberhome.filink.workflowserver.bean.WorkflowBean;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 流程service
 * @author hedongwei@wistronits.com
 * @date  2019/4/1 17:33
 */
public interface WorkflowService {

	/**
	 * 保存新增发布信息
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:30
	 * @param file 文件
	 * @param filename 文件名
	 */
	void saveNewDeploy(File file, String filename);

	/**
	 * 查询发布集合
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:30
	 * @return 返回发布的集合
	 */
	List<Deployment> findDeploymentList();

	/**
	 * 查询流程集合
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:30
	 * @return 正在运行的流程集合
	 */
	List<ProcessDefinition> findProcessDefinitionList();

	/**
	 * 根据发布名称查询图片流
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:30
	 * @param deploymentId 发布编号
	 * @param imageName 图片名称
	 * @return 图片流
	 */
	InputStream findImageInputStream(String deploymentId, String imageName);

	/**
	 * 根据编号查询图片流
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:30
	 * @param id 编号
	 * @return 图片流
	 */
	InputStream findImageInputStream(String id);

	/**
	 * 删除正在运行的流程发布信息
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:35
	 * @param deploymentId
	 */
	void deleteProcessDefinitionByDeploymentId(String deploymentId);

	/**
	 * 启动流程
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:36
	 * @param workflowBean 启动流程的参数
	 */
	void saveStartProcess(WorkflowBean workflowBean);

	/**
	 * 根据名称查询task信息
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:36
	 * @param name 名称
	 * @return 查询task信息
	 */
	List<Task> findTaskListByName(String name);

	/**
	 * 根据名称查询数量
	 * @author hedongwei@wistronits.com
	 * @date  2019/3/27 11:36
	 * @param name 名称
	 * @return 返回数量
	 */
	int findTaskListByNameCount(String name);

	/**
	 * 根据指派人查询历史记录
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 返回历史记录信息
	 */
	List<HistoricTaskInstance> findSignatureRecordList(String userCode);

	/**
	 * 根据指派人查询历史记录个数
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 返回历史记录个数
	 */
	int findSignatureRecordCount(String userCode);

	/**
	 * 根据指派人查询流程实例集合
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 流程实例集合
	 */
	List<HistoricProcessInstance> findProcessInstanceList(String userCode);

	/**
	 * 根据指派人查询流程实例个数
	 * @author hedongwei@wistronits.com
	 * @date  2019/4/1 17:30
	 * @param userCode 用户编号
	 * @return 流程实例个数
	 */
	int findProcessInstanceCount(String userCode);

	/**
	 * 使用任务id获取当前任务节点对应的FormKey中对应的值
	 * @param taskId 任务编号
	 * @return 对应的formkey的值
	 */
	String findTaskFormKeyByTaskId(String taskId);

	/**
	 * 获取当前任务完成之后的连线走向，在前台展示
	 * @param taskId 任务编号
	 * @return 连线走向
	 */
	List<String> findOutComeListByTaskId(String taskId);

	/**
	 * 通过当前任务id获取历史任务的批注信息
	 * @param taskId 任务编号
	 * @return 历史任务的批注信息
	 */
	List<Comment> findCommentByTaskId(String taskId);

	/**
	 * 通过单据编号获取历史批注信息
	 * @param id 单据编号
	 * @return 历史批注信息
	 */
	List<Comment> findCommentByProcCode(String id);

	/**
	 * 使用任务id获取流程定义对象
	 * @param taskId 任务编号
	 * @return 获取流程定义对象
	 */
	ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	/**
	 * 通过流程编码获取流程实例
	 * @param procCode 请假单
	 * @return 流程实例
	 */
	ProcessDefinition findProcessDefinitionByProcCode(String procCode);

	/**
	 * 通过当前任务id获取当前活动节点所在的坐标(查看流程进度)
	 * @param taskId 任务id
	 * @return 活动节点所在坐标
	 */
	Map<String, Integer> findCoordinateByTask(String taskId);

	/**
	 * 根据业务单据的id获取当前活动节点(查看流程进度)
	 * @param procCode 流程编号
	 * @return 当前活动节点
	 */
	Map<String, Integer> findCoordinateByProcCode(String procCode);
	
}
