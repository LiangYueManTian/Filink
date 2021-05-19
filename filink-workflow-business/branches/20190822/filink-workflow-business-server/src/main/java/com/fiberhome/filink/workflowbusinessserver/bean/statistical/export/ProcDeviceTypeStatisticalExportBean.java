package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 工单设施类型导出
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 15:57
 */
@Data
public class ProcDeviceTypeStatisticalExportBean {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 光交箱
     */
    private String opticalBoxCount;

    /**
     * 光交箱数量
     */
    private String wellCount;


    /**
     * 配线架数量
     */
    private String distributionFrameCount;

    /**
     * 接头盒数量
     */
    private String junctionBoxCount;

    /**
     * 室外柜数量
     */
    private String outdoorCabinetCount;

}
