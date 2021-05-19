package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 工单处理方案统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:36
 */
@Data
public class ProcProcessingSchemeStatistical extends ProcStatisticalBase {

    /**
     * 处理方案
     */
    private String processingScheme;
}
