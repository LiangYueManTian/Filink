package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/6 16:04
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceLogTopNum {
    /**
     * 设施ID
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 统计值
     */
    private String countValue;

    /**
     * 行号
     */
    private Integer rowNum;
}
