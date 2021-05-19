package com.fiberhome.filink.workflowbusinessapi.api.procinspection;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessapi.fallback.procinspection.ProcInspectionFeignFallback;
import com.fiberhome.filink.workflowbusinessapi.req.procinspection.ProcInspectionReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author hedongwei@wistronits.com
 * ProcInspectionFeign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-workflow-business-server", path = "/procInspection", fallback = ProcInspectionFeignFallback.class)
public interface ProcInspectionFeign {

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单请求
     * @return 新增巡检工单结果
     */
    @PostMapping("/addInspectionProc")
    Result addInspectionProc(@RequestBody ProcInspectionReq addInspectionProcReq);

    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 19:53
     * @param inspectionTask 新增巡检工单参数
     * @return 返回任务生成巡检任务
     */
    @PostMapping("/jobAddInspectionProc")
    Result jobAddInspectionProc(@RequestBody InspectionTask inspectionTask);
}
