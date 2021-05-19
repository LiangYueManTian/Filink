package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import lombok.Data;

import java.util.List;

/**
 * 巡检任务详情参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/10 21:52
 */
@Data
public class InspectionTaskDetailBean extends InspectionTask{

    /**
     * 设施集合
     */
    private List<InspectionTaskDevice> deviceList;

    /**
     * 部门集合
     */
    private List<InspectionTaskDepartment> deptList;
}
