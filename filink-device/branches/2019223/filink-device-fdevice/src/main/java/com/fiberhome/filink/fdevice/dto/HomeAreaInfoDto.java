package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * <p>
 * 首页区域信息Dto
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/21
 *
 */
@Data
public class HomeAreaInfoDto {

    /**
     * 区域id(UUID)
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 所属区域id
     */
    private String parentAreaId;

    /**
     * 区域级别
     */
    private Integer areaLevel;
}
