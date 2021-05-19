package com.fiberhome.filink.fdevice.dto;


import lombok.Data;

/**
 * <p>
 * 首页设施简化信息DTO
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-21
 */
@Data
public class HomeDeviceInfoSimple {

    /**
     * 设施id（UUID）
     */
    private String a;

    /**
     * 设施类型
     */
    private String b;

    /**
     * 设施名称
     */
    private String f;

    /**
     * 设施状态
     */
    private String c;

    /**
     * 设施编号
     */
    private String g;

    /**
     * 详细地址
     */
    private String h;

    /**
     * 定位（基础）
     */
    private String d;

    /**
     * 关联区域id
     */
    private String e;

    /**
     * 关联区域名称
     */
    private String i;
}
