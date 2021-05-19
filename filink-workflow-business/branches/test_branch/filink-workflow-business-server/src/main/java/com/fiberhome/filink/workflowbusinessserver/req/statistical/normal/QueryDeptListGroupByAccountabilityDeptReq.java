package com.fiberhome.filink.workflowbusinessserver.req.statistical.normal;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 查询工单责任单位统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:09
 */
@Data
public class QueryDeptListGroupByAccountabilityDeptReq {

    /**
     * 统计时间
     */
    private List<Long> timeList;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 责任单位部门集合
     */
    private List<String> accountabilityDeptList;

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
     * 报表类型  1 未完工  2 历史
     */
    private String statisticalType;
}
