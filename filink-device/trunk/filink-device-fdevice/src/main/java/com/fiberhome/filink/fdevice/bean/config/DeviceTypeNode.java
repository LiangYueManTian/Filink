package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.util.List;

/**
 * 设施类型实体类
 *
 * @author CongcaiYu
 */
@Data
public class DeviceTypeNode {

    /**
     * 设施类型code码
     */
    private String deviceTypeId;

    /**
     * 设施类型对应业务信息
     */
    private List<BusinessNode> childDeviceTypeList;
}
