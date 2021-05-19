package com.fiberhome.filink.workflowbusinessapi.fallback.procinspection;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procinspection.ProcInspectionFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessapi.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessapi.utils.procinspection.ProcInspectionResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class ProcInspectionFeignFallback implements ProcInspectionFeign {

    private static final String INFO = "Inspection Proc call feign fallback";

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单请求
     * @return 新增巡检工单结果
     */
    @Override
    public Result addInspectionProc(ProcInspectionReq addInspectionProcReq) {
        String addInspectionErrorMsg = "Add inspection proc error";
        log.info(INFO  + "," + addInspectionErrorMsg + ",title is " + addInspectionProcReq.getTitle(), "title");
        return ResultUtils.warn(ProcInspectionResultCode.ADD_INSPECTION_PROC_ERROR, addInspectionErrorMsg);
    }

    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 19:53
     * @param inspectionTask 新增巡检工单参数
     * @return 返回任务生成巡检任务
     */
    @Override
    public Result jobAddInspectionProc(InspectionTask inspectionTask) {
        String addProcErrorMsg = "Inspection task add inspection proc error";
        log.info(INFO + "," + addProcErrorMsg + ",inspectionTaskId is " + inspectionTask.getInspectionTaskId(), "inspectionTaskId");
        return ResultUtils.warn(ProcInspectionResultCode.ADD_INSPECTION_PROC_ERROR, addProcErrorMsg);
    }

}
