package com.fiberhome.filink.workflowbusinessapi.req.inspectiontask;

import lombok.Data;

/**
 * 修改巡检任务状态参数
 * @author hedongwei@wistronits.com
 * @date 2019/3/15 19:14
 */
@Data
public class UpdateInspectionStatusReq {

    /**
     * 巡检任务状态
     */
    private String inspectionTaskStatus;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;
}
