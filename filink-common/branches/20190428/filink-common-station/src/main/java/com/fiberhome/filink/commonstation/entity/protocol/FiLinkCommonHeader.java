package com.fiberhome.filink.commonstation.entity.protocol;

import lombok.Data;

/**
 * filink公共头参数信息
 * @author CongcaiYu
 */
@Data
public class FiLinkCommonHeader {

    /**
     * 帧地址头
     */
    private String frameHead;
    /**
     * 设备id
     */
    private String equipmentId;
    /**
     * 命令长度
     */
    private String cmdLen;
    /**
     * 协议标志位
     */
    private String protocolFlag;
    /**
     * 流水号
     */
    private String serialNumber;
    /**
     * 命令id
     */
    private String cmdId;
    /**
     * 命令类型
     */
    private String cmdType;
    /**
     * 净荷长度
     */
    private String len;
}
