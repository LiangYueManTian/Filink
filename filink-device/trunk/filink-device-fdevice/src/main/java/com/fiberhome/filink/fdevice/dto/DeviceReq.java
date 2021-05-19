package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 查询基础设施信息 请求对象
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/22
 */
@Data
public class DeviceReq implements Serializable {

    /**
     * 设施ID
     */
    private String deviceId;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 主控ID
     */
    private String controlId;

    /**
     * 区域ID
     */
    private String areaId;
}
