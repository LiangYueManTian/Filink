package com.fiberhome.filink.workflowbusinessserver.req.statistical.normal;

import lombok.Data;

import java.util.Set;

/**
 * 根据时间查询工单增量统计
 * @author hedongwei@wistronits.com
 * @date 2019/6/6 13:21
 */
@Data
public class QueryHomeProcCountByYearReq extends QueryListProcBaseReq {

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

    /**
     * 权限过滤部门id
     */
    private Set<String> permissionDeptIds;

    /**
     * 工单类型
     */
    private String procType;
}
