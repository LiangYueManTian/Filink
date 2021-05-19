package com.fiberhome.filink.workflowserver.service.impl;

import com.fiberhome.filink.workflowserver.bean.WorkflowBean;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;

import java.io.File;

/**
 * 工作流逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/6/30 17:50
 */
@RunWith(JMockit.class)
@SpringBootTest
public class WorkflowServiceTest {

    @Tested
    private WorkflowServiceImpl workflowService;

    @Injectable
    private RuntimeService runtimeService;

    @Injectable
    private RepositoryService repositoryService;

    @Injectable
    private HistoryService historyService;

    @Injectable
    private TaskService taskService;

    @Injectable
    private FormService formService;

    @Injectable
    private IdentityService identityService;


    /**
     * 保存发布的方法
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 17:16
     */
    @Test
    public void saveNewDeploy() throws Exception {
        ClassPathResource resource = new ClassPathResource("/file/process.zip");
        File file = resource.getFile();
        String filename = "process.png";
        workflowService.saveNewDeploy(file, filename);
    }

    /**
     * 查询部署对象信息 对应表act_re_deployment
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findDeploymentList() {
        workflowService.findDeploymentList();
    }

    /**
     * 查询流程定义信息 对应表act_re_prodef
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findProcessDefinitionList() {
        workflowService.findProcessDefinitionList();
    }

    /**
     * 使用部署对象id和资源图片名称，获取流程图的输入流
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findImageInputStream() {
        String deploymentId = "1";
        String imageName = "process.png";
        workflowService.findImageInputStream(deploymentId, imageName);
    }

    /**
     * 根据部署对象id删除流程定义
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void deleteProcessDefinitionByDeploymentId() {
        String deploymentId = "1";
        workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
    }

    /**
     * 启动流程实例
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void saveStartProcess() {
        WorkflowBean workflowBean = new WorkflowBean();
        workflowBean.setId("1");
        workflowBean.setComment("comment");
        workflowBean.setDeploymentId("admin");
        File file = null;
        workflowBean.setFile(file);
        workflowBean.setFilename("fileName");
        workflowBean.setImageName("imageName");
        workflowBean.setOutcome("outcome");
        workflowBean.setTaskId("taskId");
        workflowBean.getId();
        workflowBean.getComment();
        workflowBean.getDeploymentId();
        workflowBean.getFile();
        workflowBean.getFilename();
        workflowBean.getImageName();
        workflowBean.getOutcome();
        workflowBean.getTaskId();
        workflowService.saveStartProcess(workflowBean);
    }

    /**
     * 查询用户的待办任务
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findTaskListByName() {
        String name = "张三";
        workflowService.findTaskListByName(name);
    }

    /**
     * 根据名称查询数量
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findTaskListByNameCount() {
        String name = "李四";
        int num = workflowService.findTaskListByNameCount(name);
        Assert.assertTrue(!ObjectUtils.isEmpty(num));
    }

    /**
     * 根据指派人查询历史记录
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findSignatureRecordList() {
        String userCode = "1";
        workflowService.findSignatureRecordList(userCode);
    }


    /**
     * 根据指派人查询历史记录个数
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findSignatureRecordCount() {
        String userCode = "1";
        int num = workflowService.findSignatureRecordCount(userCode);
        Assert.assertTrue(!ObjectUtils.isEmpty(num));
    }


    /**
     * 根据指派人查询流程实例集合
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findProcessInstanceList() {
        String userCode = "1";
        workflowService.findProcessInstanceList(userCode);
    }


    /**
     * 根据指派人查询流程实例个数
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findProcessInstanceCount() {
        String userCode = "1";
        int num = workflowService.findProcessInstanceCount(userCode);
        Assert.assertTrue(!ObjectUtils.isEmpty(num));
    }

    /**
     * 使用任务id获取当前任务节点对应的FormKey中对应的值
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findTaskFormKeyByTaskId() {
        String taskId = "1";
        workflowService.findTaskFormKeyByTaskId(taskId);
    }

    /**
     * 获取当前任务完成之后的连线走向，在前台展示
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findOutComeListByTaskId() {
        new Expectations() {
            {
                taskService.createTaskQuery().taskId(anyString).singleResult();
                TaskEntityImpl taskEntity = new TaskEntityImpl();
                taskEntity.setProcessInstanceId("1");
                taskEntity.setProcessDefinitionId("1");
                result = taskEntity;

                ProcessInstance info = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(anyString).singleResult();
                result = info;
            }
        };

        String taskId = "1";
        workflowService.findOutComeListByTaskId(taskId);
    }

    /**
     * 通过当前任务id获取历史任务的批注信息
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findCommentByTaskId() {
        String taskId = "1";
        workflowService.findCommentByTaskId(taskId);
    }

    /**
     * 通过单据编号获取历史批注信息
     *  @author hedongwei@wistronits.com
     *  @date  2019/7/2 17:16
     */
    @Test
    public void findCommentByProcCode() {
        String procCode = "procCode";
        workflowService.findCommentByProcCode(procCode);
    }

    /**
     * 使用任务id获取流程定义对象
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 18:52
     */
    @Test
    public void findProcessDefinitionByTaskId() {
        String taskId = "1";
        workflowService.findProcessDefinitionByTaskId(taskId);
    }


    /**
     * 通过当前任务id获取当前活动节点所在的坐标(查看流程进度)
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 18:52
     */
    @Test
    public void findCoordinateByTask() {
        String taskId = "1";
        workflowService.findCoordinateByTask(taskId);
    }


    /**
     * 通过当前任务id获取当前活动节点所在的坐标(查看流程进度)
     * @author hedongwei@wistronits.com
     * @date  2019/7/2 18:52
     */
    @Test
    public void findProcessDefinitionByProcCode() {
        String taskId = "1";
        workflowService.findProcessDefinitionByProcCode(taskId);
    }


}
