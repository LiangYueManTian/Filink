package com.fiberhome.filink.workflowserver.controller;


import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowserver.req.*;
import com.fiberhome.filink.workflowserver.service.WorkflowService;
import com.fiberhome.filink.workflowserver.constant.Constants;
import com.fiberhome.filink.workflowserver.utils.StringUtils;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description 流程控制层
 * date 2018/11/28 12:24
 */
@RestController
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TaskService taskService;

    /**
     * 根据业务key查询当前的任务信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/20
     * @param req 业务key
     */
    public Result getNowTaskByBusinessKey(@RequestBody GetNowTaskByBusinessKeyReq req) {
        if (null != req) {
            if (StringUtils.isNotEmpty(req.getBussinessKey())) {
                //获取用户信息
                Task task = taskService.createTaskQuery().processInstanceBusinessKey(req.getBussinessKey()).singleResult();
                return ResultUtils.success(task);
            } else {
                return ResultUtils.success(new JSONObject());
            }
        } else {
            return ResultUtils.success(new JSONObject());
        }
    }


    /**
     * @author hedongwei@wistronits.com
     * description 查询待办任务个数
     * date 9:38 2018/11/28
     * param [req]
     */
    @PostMapping("/getPendingTaskCount")
    @ResponseBody
    public Map<String, Object> getPendingTaskCount(@RequestBody GetPendingTaskCountReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        //查询待办任务个数
        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        int count = workflowService.findTaskListByNameCount(userCode);
        retMap.put("count", count);
        return retMap;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询签核记录个数
     * date 9:36 2018/11/28
     * param [req]
     */
    @PostMapping("/getSignatureRecordCount")
    @ResponseBody
    public Map<String, Object> getSignatureRecordCount(@RequestBody GetSignatureRecordCountReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        //查询签核记录个数
        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        int count = workflowService.findSignatureRecordCount(userCode);
        retMap.put("count", count);
        return retMap;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询申请记录个数
     * date 9:37 2018/11/28
     * param [req]
     */
    @PostMapping("/getApplyRecordCount")
    @ResponseBody
    public Map<String, Object> getApplyRecordCount(@RequestBody GetApplyRecordCountReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        //查询申请记录个数
        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        int count = workflowService.findProcessInstanceCount(userCode);
        retMap.put("count", count);
        return retMap;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 待办记录信息
     * date 9:27 2018/11/28
     * param [req]
     */
    @PostMapping("/getPendingTaskList")
    @ResponseBody
    public Map<String, Object> getPendingTaskCountList(@RequestBody GetPendingTaskListReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        List<Task> list = workflowService.findTaskListByName(userCode);
        int count = workflowService.findTaskListByNameCount(userCode);
        //用fastjson序列化List<Task>会报错，因此将List<Task>中的内容复制到List<Map>中，再进行序列化

        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        for (Task task : list) {
            HashMap<String, Object> map = new HashMap<>(Constants.MAP_INIT_NUM);
            map.put("id", task.getId());
            map.put("name", task.getName());
            map.put("assignee", task.getAssignee());
            map.put("createTime", task.getCreateTime());
            arrayList.add(map);
        }

        retMap.put("rows", arrayList);
        retMap.put("total", count);
        //查询待办任务信息
        return retMap;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 签核记录信息
     * date 9:27 2018/11/28
     * param [session, req]
     */
    @PostMapping("/getSignatureRecordList")
    @ResponseBody
    public Map<String, Object> getSignatureRecordList(@RequestBody GetSignatureRecordListReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        //查询签核记录信息
        List<HistoricTaskInstance> list = workflowService.findSignatureRecordList(userCode);
        int count = workflowService.findSignatureRecordCount(userCode);
        retMap.put("rows", list);
        retMap.put("total", count);

        return retMap;
    }

    
    /**
     * @author hedongwei@wistronits.com
     * description 申请记录信息
     * date 9:28 2018/11/28
     * param [session, req]
     */
    @PostMapping("/getApplyRecordList")
    @ResponseBody
    public Map<String, Object> getApplyRecordList(@RequestBody GetApplyRecordListReq req) {
        Map<String, Object> retMap = new HashMap<String, Object>(Constants.MAP_INIT_NUM);

        String userCode = "";
        if (null != req.getUserCode()) {
            userCode = req.getUserCode();
        }

        //查询申请记录信息
        List<HistoricProcessInstance> historicProcessInstanceList = workflowService.findProcessInstanceList(userCode);
        int count = workflowService.findProcessInstanceCount(userCode);
        retMap.put("rows", historicProcessInstanceList);
        retMap.put("total", count);
        return retMap;
    }

}
