package com.fiberhome.filink.filinkoceanconnectserver.entity.platform;

import lombok.Data;

/**
 * 设备服务实体
 * @author CongcaiYu
 */
@Data
public class DeviceServiceData {
    /**
     * 服务id
     */
    private String serviceId;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 服务数据信息
     */
    private Object data;
    /**
     * 事件发生时间
     */
    private String eventTime;
}
