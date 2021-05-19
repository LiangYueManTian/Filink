package com.fiberhome.filink.workflowbusinessapi.api.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.fallback.inspectiontask.InspectionTaskFeignFallback;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.DeleteInspectionTaskForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.UpdateInspectionStatusReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * InspectionTaskFeign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-workflow-business-server", path = "/inspectionTask",fallback = InspectionTaskFeignFallback.class)
public interface InspectionTaskFeign {

    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 10:48
     * @param id 巡检任务编号
     * @return 巡检任务详情
     */
    @GetMapping("/getInspectionTaskDetail/{id}")
    Result getInspectionTaskDetail(@PathVariable("id") String id);

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/15 19:16
     * @param req 修改巡检任务状态
     * @return 修改巡检任务状态结果
     */
    @PutMapping("/updateInspectionStatus")
    Result updateInspectionStatus(@RequestBody UpdateInspectionStatusReq req);


    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 10:10
     * @param req 删除巡检任务参数
     * @return 删除巡检任务
     */
    @PostMapping("/deleteInspectionTaskForDeviceList")
    Result deleteInspectionTaskForDeviceList(DeleteInspectionTaskForDeviceReq req);

    /**
     * 校验部门信息有无关联巡检任务
     * @author hedongwei@wistronits.com
     * @param deptIds 部门ids
     *
     * @date  2019/4/26 11:03
     * @return 校验部门信息有无关联巡检任务
     */
    @PostMapping("/queryInspectionTaskListByDeptIds")
    Object queryInspectionTaskListByDeptIds(@RequestBody List<String> deptIds);

}
