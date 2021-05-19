package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 我的关注 DTO
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceAttentionDto implements Serializable {
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 设施状态
     */
    private String deviceStatus;
    /**
     * 设施资产编号
     */
    private String deviceCode;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 统计
     */
    private String totalNum;

    /**
     * 区域id
     */
    private String areaId;
}
