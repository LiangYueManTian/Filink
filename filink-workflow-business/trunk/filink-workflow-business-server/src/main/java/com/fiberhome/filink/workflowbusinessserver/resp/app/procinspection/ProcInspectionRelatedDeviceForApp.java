package com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection;

import lombok.Data;

/**
 * 流程关联设施app
 * @author hedongwei@wistronits.com
 * @date 2019/4/15 16:38
 */
@Data
public class ProcInspectionRelatedDeviceForApp {

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施区域编号
     */
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    private String deviceAreaName;

    /**
     * 地址
     */
    private String address;

    /**
     * 经纬度
     */
    private String positionBase;
}
