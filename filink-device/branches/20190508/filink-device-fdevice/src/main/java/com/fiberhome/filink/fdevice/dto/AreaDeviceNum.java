package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * <p>
 *     首页区域下设施数量
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-06-24
 */
@Data
public class AreaDeviceNum {
    /**
     * 区域ID
     */
    private String areaId;
    /**
     * 设施数量
     */
    private int deviceNum;
}
