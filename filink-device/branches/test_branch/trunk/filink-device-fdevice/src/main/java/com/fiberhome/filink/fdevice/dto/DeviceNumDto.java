package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/23 22:19
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceNumDto {

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 部署状态
     */
    private String deployStatus;

    /**
     * 设施数量
     */
    private int deviceNum;
}
