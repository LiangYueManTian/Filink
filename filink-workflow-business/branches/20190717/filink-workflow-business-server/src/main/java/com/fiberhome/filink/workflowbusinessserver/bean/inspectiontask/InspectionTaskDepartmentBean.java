package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import lombok.Data;

/**
 * 巡检任务关联部门类
 * @author hedongwei@wistronits.com
 * @date 2019/3/5 22:40
 */
@Data
public class InspectionTaskDepartmentBean extends InspectionTaskDepartment{

    /**
     * 责任单位名称
     */
    private String accountabilityDeptName;
}
