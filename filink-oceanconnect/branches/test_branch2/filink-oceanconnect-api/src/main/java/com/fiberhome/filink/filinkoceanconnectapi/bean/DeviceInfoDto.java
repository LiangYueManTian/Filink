package com.fiberhome.filink.filinkoceanconnectapi.bean;

import lombok.Data;

/**
 * DeviceInfoDTO
 * @author CongcaiYu
 */
@Data
public class DeviceInfoDto {
    /**
     * 厂商id
     */
    private String manufacturerId;
    /**
     * 厂商名称
     */
    private String manufacturerName;
    /**
     * 设施类型(和profile一致)
     */
    private String deviceType;
    /**
     * 设施型号
     */
    private String model;
    /**
     * 设施协议类型
     */
    private String protocolType;
}
