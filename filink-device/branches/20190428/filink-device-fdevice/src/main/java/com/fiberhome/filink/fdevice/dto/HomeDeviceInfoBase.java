package com.fiberhome.filink.fdevice.dto;


import lombok.Data;

/**
 * <p>
 * 首页设施简略信息DTO
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-21
 */
@Data
public class HomeDeviceInfoBase {
    /**
     * 设施id（UUID）
     */
    private String a;

    /**
     * 设施类型
     */
    private String b;
    /**
     * 设施状态
     */
    private String c;
    /**
     * 定位（基础）
     */
    private String d;

    /**
     * 关联区域id
     */
    private String e;
}
