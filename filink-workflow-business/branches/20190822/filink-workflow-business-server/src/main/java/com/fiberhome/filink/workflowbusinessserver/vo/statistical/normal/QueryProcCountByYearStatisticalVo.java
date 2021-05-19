package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import lombok.Data;

/**
 * 根据时间区间查询工单增量统计
 * @author hedongwei@wistronits.com
 * @date 2019/6/6 14:36
 */
@Data
public class QueryProcCountByYearStatisticalVo {

    /**
     * 统计时间
     */
    private Long nowDate;

    /**
     * 每天新增工单数量
     */
    private Integer orderCount;

    /**
     * 时间
     */
    private String date;
}
