package com.fiberhome.filink.workflowserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.resp.DeployTaskResp;
import com.fiberhome.filink.workflowserver.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
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
     * @return 返回数据
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
}
