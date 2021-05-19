package com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview;

import lombok.Data;

/**
 * 工单处理方案概览统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:36
 */
@Data
public class ProcProcessingSchemeOverviewStatistical extends ProcStatisticalOverviewBase {

    /**
     * 处理方案
     */
    private String processingScheme;
}
