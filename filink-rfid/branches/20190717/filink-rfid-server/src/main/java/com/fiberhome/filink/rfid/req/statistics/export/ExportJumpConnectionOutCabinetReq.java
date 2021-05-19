package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 描述
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
public class ExportJumpConnectionOutCabinetReq {
    /**
     * A端设施
     */
    private String deviceName;
    /**
     * A端
     */
    private String portNo;
    /**
     * Z端设施
     */
    private String oppositeDeviceName;
    /**
     * Z端
     */
    private String oppositePortNo;
    /**
     * 跳接类型
     */
    private String jumpType;
    /**
     * 跳接信息
     */
    private String remark;
    /**
     * 序号
     */
    private String serialNumber;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }

    public String getOppositeDeviceName() {
        return oppositeDeviceName;
    }

    public void setOppositeDeviceName(String oppositeDeviceName) {
        this.oppositeDeviceName = oppositeDeviceName;
    }

    public String getOppositePortNo() {
        return oppositePortNo;
    }

    public void setOppositePortNo(String oppositePortNo) {
        this.oppositePortNo = oppositePortNo;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
