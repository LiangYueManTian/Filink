package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 工单区域工单比统计
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 16:11
 */
@Data
public class ProcAreaPercentStatisticalExportBean {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域工单数量
     */
    private String areaProcCount;

    /**
     * 区域工单比
     */
    private String areaProcPercent;
}
