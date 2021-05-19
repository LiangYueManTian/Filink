package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * PDA设施数据传输类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/8
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceInfoForPda {
    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String districtName;

    /**
     * 基础定位(经纬度)
     */
    private String positionBase;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 是否关注
     */
    private String isCollecting;

    /**
     * 区域id(UUID)
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域级别
     */
    private String areaLevel;

    /**
     * 主控信息集合
     */
    private List<ControlParam> controlParamList;

    /**
     * 是否是包含电子锁设施
     */
    private String containsLocks;

    /**
     * 实景图片地址集合
     */
    private List<DevicePicDto> picList;

}
