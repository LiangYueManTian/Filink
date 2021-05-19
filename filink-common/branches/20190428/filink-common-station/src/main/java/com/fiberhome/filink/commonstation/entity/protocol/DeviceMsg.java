package com.fiberhome.filink.commonstation.entity.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 设施消息类
 * @author CongcaiYu
 */
@Data
public class DeviceMsg implements Serializable {
    /**
     * 主控id
     */
    private String equipmentId;
    /**
     * 设施ip
     */
    private String ip;
    /**
     * 设施端口
     */
    private Integer port;
    /**
     * 数据包
     */
    private String hexData;
    /**
     * 平台id
     */
    private String oceanConnectId;

}
