package com.fiberhome.filink.workflowapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowapi.fallback.ProcessFeignFallback;
import com.fiberhome.filink.workflowapi.req.CompleteTaskReq;
import com.fiberhome.filink.workflowapi.req.StartProcessReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author hedongwei@wistronits.com
 * ProcessFeign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-workflow-server", path = "/process",fallback = ProcessFeignFallback.class)
public interface ProcessFeign {

    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date 15:54 2018/11/28
     * @param req 启动流程
     * @return 启动流程
     */
    @PostMapping("/startProcess")
    Result startProcess(@RequestBody StartProcessReq req);


    /**
     * 任务办结
     * @author hedongwei@wistronits.com
     * @date 16:21 2018/11/28
     * @param req 办结的参数
     * @return CompleteTaskResp
     */
    @PostMapping("/complete")
    Result completeTask(@RequestBody CompleteTaskReq req);
}
