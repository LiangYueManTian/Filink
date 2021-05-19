package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 工单销障状态导出类
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 15:16
 */
@Data
public class ProcStatusStatisticalExportBean {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 待指派
     */
    private String assignedCount;

    /**
     * 待处理
     */
    private String pendingCount;

    /**
     * 处理中
     */
    private String processingCount;

    /**
     * 已转派
     */
    private String turnProcessingCount;

    /**
     * 已退单
     */
    private String singleBackCount;

    /**
     * 已完成
     */
    private String completedCount;

}
