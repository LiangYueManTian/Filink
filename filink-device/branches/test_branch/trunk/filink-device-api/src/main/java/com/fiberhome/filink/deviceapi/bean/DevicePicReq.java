package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

import java.util.Set;

/**
 * <p>
 * 设施图片请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-28
 */
@Data
public class DevicePicReq {

    /**
     * 图片张数
     */
    private String picNum;

    /**
     * 图片来源
     */
    private String resource;

    /**
     * 设施ids（用于权限控制）
     */
    private Set<String> deviceIds;

}
