package com.fiberhome.filink.workflowbusinessapi.bean.procbase;

import lombok.Data;

import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * @date 2019/3/25 21:39
 */
@Data
public class ProcBaseInfoBean extends ProcBase {

    /**
     * 工单关联设施
     */
    private List<ProcRelatedDevice> procRelatedDevices;

    /**
     * 工单关联部门
     */
    private List<ProcRelatedDepartment> procRelatedDepartments;
}
