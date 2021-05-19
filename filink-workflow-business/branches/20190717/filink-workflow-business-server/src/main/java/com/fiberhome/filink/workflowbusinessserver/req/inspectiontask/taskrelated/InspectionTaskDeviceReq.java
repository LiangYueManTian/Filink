package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated;

import lombok.Data;

import java.util.List;

/**
 * 巡检任务设施请求参数类
 * @author hedongwei@wistronits.com
 * @date 2019/4/26 13:35
 */
@Data
public class InspectionTaskDeviceReq {

    /**
     * 设施集合
     */
    private List<String> deviceIdList;

    /**
     * 巡检任务编号
     */
    private List<String> inspectionTaskIdList;

    /**
     * 巡检任务记录编号
     */
    private List<String> inspectionTaskDeviceIdList;

    /**
     * 是否删除
     */
    private String isDeleted;
}
