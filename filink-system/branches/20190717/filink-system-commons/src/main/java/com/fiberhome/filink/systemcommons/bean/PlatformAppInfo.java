package com.fiberhome.filink.systemcommons.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
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
@TableName("platform_app_info")
public class PlatformAppInfo {
    /**
     * 主键ID
     */
    @TableId("platform_app_id")
    private String platformAppId;

    /**
     * 平台类型
     */
    @TableField("platform_type")
    private Integer platformType;

    /**
     * 平台APP（产品）ID
     */
    @TableField("app_id")
    private String appId;

    /**
     * 核心秘钥
     */
    @TableField("secret_key")
    private String secretKey;

    /**
     * 应用(产品)名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 厂商id
     */
    @TableField("manufacturer_id")
    private String manufacturerId;
    /**
     * 厂商名称
     */
    @TableField("manufacturer_name")
    private String manufacturerName;
    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 型号
     */
    private String model;

    /**
     * 协议类型
     */
    @TableField("protocol_type")
    private String protocolType;
}
