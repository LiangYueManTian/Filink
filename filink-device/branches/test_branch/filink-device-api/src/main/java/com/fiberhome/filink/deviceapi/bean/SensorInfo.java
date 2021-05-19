package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 10:04
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class SensorInfo {
    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 温度
     */
    private Integer temperature;

    /**
     * 湿度
     */
    private Integer humidity;

    /**
     * 创建时间
     */
    private Long currentTime;
}
