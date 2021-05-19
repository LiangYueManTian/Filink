package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 * 设施参数 for pda 查询电子锁二维码
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/11
 */
@Data
public class DeviceInfoForLock {
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 电子锁主控信息列表
     */
    private List<ControlParamForControl> controlList;
}
