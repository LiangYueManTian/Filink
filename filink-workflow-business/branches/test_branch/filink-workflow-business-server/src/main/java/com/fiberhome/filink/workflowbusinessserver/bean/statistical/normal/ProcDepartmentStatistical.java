package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 工单责任部门统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:46
 */
@Data
public class ProcDepartmentStatistical {

    /**
     * 责任部门id
     */
    private String departmentId;

    /**
     * 责任部门数量
     */
    private Integer departmentCount;
}
