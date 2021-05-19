package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask;

import lombok.Data;

/**
 * 工单关联设施信息
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 20:21
 */
@Data
public class InspectionTaskRelationDeviceListReq {

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;
}
