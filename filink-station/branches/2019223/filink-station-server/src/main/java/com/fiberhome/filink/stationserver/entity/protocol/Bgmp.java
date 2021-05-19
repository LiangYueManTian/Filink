package com.fiberhome.filink.stationserver.entity.protocol;

import java.io.Serializable;

/**
 * BGMP对象
 *
 * @author CongcaiYu
 */
public class Bgmp implements Serializable {

    /**
     * 协议标识
     */
    private String protocolFlag;

    /**
     * 命令序列号
     */
    private Integer cmdSequenceId;

    /**
     * 设备id
     */
    private Integer equipmentId;

    /**
     * 命令类型 1 命令帧 2 应答帧
     */
    private Integer cmdType;

    /**
     * 净荷长度(data body的长度)
     */
    private Integer len;

    public Integer getCmdSequenceId() {
        return cmdSequenceId;
    }

    public void setCmdSequenceId(Integer cmdSequenceId) {
        this.cmdSequenceId = cmdSequenceId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getCmdType() {
        return cmdType;
    }

    public void setCmdType(Integer cmdType) {
        this.cmdType = cmdType;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getProtocolFlag() {
        return protocolFlag;
    }

    public void setProtocolFlag(String protocolFlag) {
        this.protocolFlag = protocolFlag;
    }

    @Override
    public String toString() {
        return "Bgmp{" +
                "protocolFlag='" + protocolFlag + '\'' +
                ", cmdSequenceId=" + cmdSequenceId +
                ", equipmentId=" + equipmentId +
                ", cmdType=" + cmdType +
                ", len=" + len +
                '}';
    }
}
