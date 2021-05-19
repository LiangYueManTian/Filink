package com.fiberhome.filink.filinkoceanconnectserver.entity.platform;

import lombok.Data;

/**
 * 平台消息接收类
 * @author CongcaiYu
 */
@Data
public class ReceiveBean {
    /**
     * 通知类型
     */
    private String notifyType;
    /**
     * 消息序列号
     */
    private String requestId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 网关id
     */
    private String gatewayId;
    /**
     * 设备服务数据
     */
    private DeviceServiceData service;
}
