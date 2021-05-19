package com.fiberhome.filink.platformappapi.bean;

import lombok.Data;

/**
 * <p>
 *   设备平台APP/产品信息实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
@Data
public class PlatformAppInfo {
    /**
     * 主键ID
     */
    private String platformAppId;

    /**
     * 平台类型
     */
    private Integer platformType;

    /**
     * 平台APP（产品）ID
     */
    private String appId;

    /**
     * 核心秘钥
     */
    private String secretKey;

    /**
     * 应用(产品)名称
     */
    private String appName;

    /**
     * 厂商id
     */
    private String manufacturerId;
    /**
     * 厂商名称
     */
    private String manufacturerName;
    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 型号
     */
    private String model;

    /**
     * 协议类型
     */
    private String protocolType;
}
