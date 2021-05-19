package com.fiberhome.filink.workflowapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowapi.api.ProcessFeign;
import com.fiberhome.filink.workflowapi.req.CompleteTaskReq;
import com.fiberhome.filink.workflowapi.req.StartProcessReq;
import com.fiberhome.filink.workflowapi.utils.ProcessResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class ProcessFeignFallback implements ProcessFeign {

    private static final String INFO = "Workflow call feign fallback";

    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date 15:54 2018/11/28
     * @param req 启动流程
     * @return 启动流程
     */
    @Override
    public Result startProcess(StartProcessReq req) {
        log.info(INFO);
        String msg = "Start proc error<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
        return ResultUtils.success(ProcessResultCode.START_PROCESS_ERROR, msg);
    }

    /**
     * 任务办结
     * @author hedongwei@wistronits.com
     * @date 16:21 2018/11/28
     * @param req 办结的参数
     * @return 办结任务结果
     */
    @Override
    public Result completeTask(CompleteTaskReq req) {
        log.info(INFO);
        String msg = "Complete proc error<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
        return ResultUtils.success(ProcessResultCode.COMPLETE_PROCESS_ERROR, msg);
    }
}
