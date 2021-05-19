package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import lombok.Data;

import java.util.List;

/**
 * 巡检任务关联信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 15:51
 */
@Data
public class InspectionTaskRelated {

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务关联设施
     */
    private List<InspectionTaskDevice> deviceList;

    /**
     * 巡检任务关联部门
     */
    private List<InspectionTaskDepartment> departmentList;
}
