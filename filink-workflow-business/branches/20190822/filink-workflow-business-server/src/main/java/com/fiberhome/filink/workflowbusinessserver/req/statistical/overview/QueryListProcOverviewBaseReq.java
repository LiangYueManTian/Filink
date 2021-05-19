package com.fiberhome.filink.workflowbusinessserver.req.statistical.overview;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 查询报表参数父类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:55
 */
@Data
public class QueryListProcOverviewBaseReq {

    /**
     * 区域编号集合
     */
    private List<String> areaIdList;

    /**
     * 设施类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 时间区间
     */
    private List<Long> timeList;

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 统计类型 无 不用根据告警类型统计  1 未完工统计 2 历史工单统计
     */
    private String statisticalType;

    /**
     * 权限过滤部门id
     */
    private Set<String> permissionDeptIds;

}
