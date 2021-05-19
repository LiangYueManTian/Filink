package com.fiberhome.filink.map.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 地区码表
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@Data
public class BaiduArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地区Id
     */
    private Integer areaId;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 地区名
     */
    private String areaName;

    /**
     * 地区级别（1:省份province,2:市city,3:区县district,4:街道street）
     */
    private Integer level;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市中心点（即：经纬度坐标）
     */
    private String center;

    /**
     * 地区边界
     */
    private String boundary;

    /**
     * 地区父节点
     */
    private Integer parentId;



}
