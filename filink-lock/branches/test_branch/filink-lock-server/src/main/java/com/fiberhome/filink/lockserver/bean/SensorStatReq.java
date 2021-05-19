package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/31 15:38
 * @Description: com.fiberhome.filink.lockserver.bean
 * @version: 1.0
 */
@Data
public class SensorStatReq {
    /**
     * 设施ID
     */
    private String deviceId;

    /**
     * 传感值类型
     */
    private String sensorType;

    /**
     * 起始时间
     */
    private Long startTime;

    /**
     * 终止时间
     */
    private Long endTime;
}
