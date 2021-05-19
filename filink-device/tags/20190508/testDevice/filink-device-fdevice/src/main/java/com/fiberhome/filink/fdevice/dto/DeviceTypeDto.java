package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/20 17:04
 * @Description: com.fiberhome.filink.fdevice.bean.device
 * @version: 1.0
 */
@Data
public class DeviceTypeDto {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 光交箱
     */
    private String type001;

    /**
     * 人井
     */
    private String type030;

    /**
     * 配线架
     */
    private String type060;

    /**
     * 接头盒
     */
    private String type090;

    /**
     * 室外柜
     */
    private String type210;
}
