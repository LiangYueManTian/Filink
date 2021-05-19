package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 柜间跳接导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
public class ExportJumpConnectionInCabinetReq {
    /**
     * A端
     */
    private String portNo;
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

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
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
