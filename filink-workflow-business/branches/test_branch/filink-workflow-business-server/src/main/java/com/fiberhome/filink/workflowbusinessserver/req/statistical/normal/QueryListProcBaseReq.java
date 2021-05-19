package com.fiberhome.filink.workflowbusinessserver.req.statistical.normal;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 查询报表参数父类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:55
 */
@Data
public class QueryListProcBaseReq {

    /**
     * 区域编号集合
     */
    private List<String> areaIdList;

    /**
     * 设施类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 统计时间
     */
    private List<Long> timeList;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 权限过滤部门id
     */
    private Set<String> permissionDeptIds;
}
