package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 工单销障状态导出类
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 15:16
 */
@Data
public class ProcTopListStatisticalExportBean {

    /**
     * 排名
     */
    private String ranking;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 责任单位
     */
    private String accountabilityUnitName;

    /**
     * 部署状态
     */
    private String status;

}
