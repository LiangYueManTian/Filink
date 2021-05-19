package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

/**
 * 业务节点信息
 *
 * @author CongcaiYu
 */
@Data
public class BusinessNode {

    /**
     * 业务信息code码
     */
    private String deviceTypeId;

    public BusinessNode(String deviceTypeCode, String code) {
        this.deviceTypeId = deviceTypeCode + "-" + code;
    }
}
