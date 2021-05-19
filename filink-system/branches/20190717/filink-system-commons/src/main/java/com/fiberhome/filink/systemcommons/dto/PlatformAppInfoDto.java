package com.fiberhome.filink.systemcommons.dto;

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
public class PlatformAppInfoDto {
    /**
     * 平台类型
     */
    private Integer platformType;
    /**
     * 平台APP（产品）ID
     */
    private String appId;
    /**
     * 应用(产品)名称
     */
    private String appName;
}
