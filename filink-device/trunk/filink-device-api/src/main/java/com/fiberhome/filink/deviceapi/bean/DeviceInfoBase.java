package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/5 17:33
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class DeviceInfoBase {
    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 区域ID
     */
    private String areaId;
}
