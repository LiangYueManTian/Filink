package com.fiberhome.filink.fdevice.dto;


import lombok.Data;

/**
 * <p>
 * 首页设施信息DTO
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-21
 */
@Data
public class HomeDeviceInfoDto {

    /**
     * 设施id（UUID）
     */
    private String deviceId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 设施编号
     */
    private String deviceCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 定位（基础）
     */
    private String positionBase;

    /**
     * 关联区域id
     */
    private String areaId;

    /**
     * 关联区域名称
     */
    private String areaName;
}
