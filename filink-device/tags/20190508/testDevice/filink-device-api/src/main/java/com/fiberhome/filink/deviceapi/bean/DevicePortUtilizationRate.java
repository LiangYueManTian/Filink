package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

/**
 * <p>
 * 设施端口使用率信息表
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
@Data
public class DevicePortUtilizationRate {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 使用率
     */
    private Double utilizationRate;
}
