package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 工单信息时间统计
 * @author hedongwei@wistronits.com
 * @date 2019/6/5 21:02
 */
@Data
public class ProcInfoYearStatisticalGroup {

    /**
     * 当前日期
     */
    private Long nowDate;

    /**
     * 工单数量
     */
    private Integer orderCount;
}
