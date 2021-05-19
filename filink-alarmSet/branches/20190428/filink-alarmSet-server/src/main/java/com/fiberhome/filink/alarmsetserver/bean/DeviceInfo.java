package com.fiberhome.filink.alarmsetserver.bean;

import lombok.Data;

/**
 * <p>
 * 设施国际化实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class DeviceInfo {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施类型Code
     */
    private String deviceType;

    /**
     * 设施类型名称
     */
    private String deviceTypeName;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 部署状态
     */
    private String deployStatus;

    /**
     * 备注
     */
    private String remarks;
}
