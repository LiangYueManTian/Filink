package com.fiberhome.filink.workflowbusinessapi.fallback.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.DeleteInspectionTaskForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.UpdateInspectionStatusReq;
import com.fiberhome.filink.workflowbusinessapi.utils.inspectiontask.InspectionTaskResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class InspectionTaskFeignFallback implements InspectionTaskFeign {

    private static final String INFO = "Inspection task call feign fallback";

    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 10:48
     * @param id 巡检任务编号
     * @return 巡检任务详情
     */
    @Override
    public Result getInspectionTaskDetail(String id) {
        String taskDetailErrorMsg = "Search inspection task detail error";
        log.info(INFO + "," + taskDetailErrorMsg + ",inspectionTaskId is" + id, "inspectionTaskId");
        return ResultUtils.warn(InspectionTaskResultCode.GET_INSPECTION_TASK_DETAIL_ERROR, taskDetailErrorMsg);
    }

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/15 19:16
     * @param req 修改巡检任务状态
     */
    @Override
    public Result updateInspectionStatus(UpdateInspectionStatusReq req) {
        String updateInspectionStatusErrorMsg = "Update inspection task status error";
        log.info(INFO + "," + updateInspectionStatusErrorMsg + ",inspectionTaskId is" + req.getInspectionTaskId(), "inspectionTaskId");
        return ResultUtils.warn(InspectionTaskResultCode.UPDATE_INSPECTION_STATUS_ERROR_MSG, updateInspectionStatusErrorMsg);
    }

    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 10:10
     * @param req 删除巡检任务参数
     * @return 删除巡检任务
     */
    @Override
    public Result deleteInspectionTaskForDeviceList(DeleteInspectionTaskForDeviceReq req) {
        String deleteError = ",delete inspection task for device list error" ;
        log.info(INFO + deleteError);
        throw new NullPointerException();
    }

    /**
     * 校验部门信息有无关联巡检任务
     * @author hedongwei@wistronits.com
     * @param deptIds 部门ids
     *
     * @date  2019/4/26 11:03
     * @return 校验部门信息有无关联巡检任务
     */
    @Override
    public Object queryInspectionTaskListByDeptIds(List<String> deptIds) {
        String searchError = ",query inspection task list by dept id list is error";
        log.info(INFO + searchError);
        return null;
    }

}
