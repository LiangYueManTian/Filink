package com.fiberhome.filink.lockserver.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/3 19:58
 * @Description: com.fiberhome.filink.lockserver.bean
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SensorTopNum {

    /**
     * 设施ID
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 传感器值类型
     */
    private String sensorType;

    /**
     * 传感器值
     */
    private String sensorValue;

    /**
     * 行号
     */
    private Integer rowNum;

}
