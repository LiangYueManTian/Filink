package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/20 17:04
 * @Description: com.fiberhome.filink.fdevice.bean.device
 * @version: 1.0
 */
@Data
public class DeviceStatusDto {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 未配置
     */
    private String type1;

    /**
     * 正常
     */
    private String type2;

    /**
     * 告警
     */
    private String type3;

    /**
     * 离线
     */
    private String type4;

    /**
     * 失联
     */
    private String type5;
}
