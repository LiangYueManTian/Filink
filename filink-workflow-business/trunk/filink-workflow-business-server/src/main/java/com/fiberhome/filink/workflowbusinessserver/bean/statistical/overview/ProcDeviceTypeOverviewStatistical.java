package com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview;

import lombok.Data;

/**
 * 工单设施类型概览统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:36
 */
@Data
public class ProcDeviceTypeOverviewStatistical extends ProcStatisticalOverviewBase {

    /**
     * 设施类型
     */
    private String deviceType;
}
