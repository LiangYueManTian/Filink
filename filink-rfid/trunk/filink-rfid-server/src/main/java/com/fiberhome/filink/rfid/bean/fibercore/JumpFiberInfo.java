package com.fiberhome.filink.rfid.bean.fibercore;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 跳纤信息表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@TableName("jump_fiber_info")
public class JumpFiberInfo extends Model<JumpFiberInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 跳纤id（uuid）
     */
    @TableId("jump_fiber_id")
    private String jumpFiberId;

    /**
     * 本端端口所属设施id
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 本端端口所属箱架AB面
     */
    @TableField("box_side")
    private Integer boxSide;

    /**
     * 本端端口所属框号
     */
    @TableField("frame_no")
    private String frameNo;

    /**
     * 本端端口所属盘AB面
     */
    @TableField("disc_side")
    private Integer discSide;

    /**
     * 本端端口所属盘号
     */
    @TableField("disc_no")
    private String discNo;

    /**
     * 本端端口号
     */
    @TableField("port_no")
    private String portNo;

    /**
     * 对端端口所属设施id
     */
    @TableField("opposite_device_id")
    private String oppositeDeviceId;

    /**
     * 对端端口所属框AB面
     */
    @TableField("opposite_box_side")
    private Integer oppositeBoxSide;

    /**
     * 对端端口所属框号
     */
    @TableField("opposite_frame_no")
    private String oppositeFrameNo;

    /**
     * 对端端口所属盘AB面
     */
    @TableField("opposite_disc_side")
    private Integer oppositeDiscSide;

    /**
     * 对端端口所属盘号
     */
    @TableField("opposite_disc_no")
    private String oppositeDiscNo;

    /**
     * 对端端口号
     */
    @TableField("opposite_port_no")
    private String oppositePortNo;


    /**
     * 适配器类型(FC/SC)
     */
    @TableField("adapter_type")
    private Integer adapterType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 对端备注
     */
    private String oppositeRemark;

    /**
     * 本端rfid信息
     */
    @TableField("rfid_code")
    private String rfidCode;

    /**
     * 对端智能标签
     */
    @TableField("opposite_rfid_code")
    private String oppositeRfidCode;

    /**
     * 是否柜内跳（0、柜内跳；1、柜间跳）
     */
    @TableField("inner_device")
    private String innerDevice;

    /**
     * 是否分路器（0、不是；1、是）
     */
    @TableField("branching_unit")
    private String branchingUnit;

    /**
     * 删除标记
     */
    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    public String getJumpFiberId() {
        return jumpFiberId;
    }

    public void setJumpFiberId(String jumpFiberId) {
        this.jumpFiberId = jumpFiberId;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOppositeRemark() {
        return oppositeRemark;
    }

    public void setOppositeRemark(String oppositeRemark) {
        this.oppositeRemark = oppositeRemark;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getOppositeRfidCode() {
        return oppositeRfidCode;
    }

    public void setOppositeRfidCode(String oppositeRfidCode) {
        this.oppositeRfidCode = oppositeRfidCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public String getDiscNo() {
        return discNo;
    }

    public void setDiscNo(String discNo) {
        this.discNo = discNo;
    }

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }

    public String getOppositeDeviceId() {
        return oppositeDeviceId;
    }

    public void setOppositeDeviceId(String oppositeDeviceId) {
        this.oppositeDeviceId = oppositeDeviceId;
    }

    public String getOppositeFrameNo() {
        return oppositeFrameNo;
    }

    public void setOppositeFrameNo(String oppositeFrameNo) {
        this.oppositeFrameNo = oppositeFrameNo;
    }

    public String getOppositeDiscNo() {
        return oppositeDiscNo;
    }

    public void setOppositeDiscNo(String oppositeDiscNo) {
        this.oppositeDiscNo = oppositeDiscNo;
    }

    public String getOppositePortNo() {
        return oppositePortNo;
    }

    public void setOppositePortNo(String oppositePortNo) {
        this.oppositePortNo = oppositePortNo;
    }

    public Integer getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(Integer adapterType) {
        this.adapterType = adapterType;
    }

    public Integer getBoxSide() {
        return boxSide;
    }

    public void setBoxSide(Integer boxSide) {
        this.boxSide = boxSide;
    }

    public Integer getDiscSide() {
        return discSide;
    }

    public void setDiscSide(Integer discSide) {
        this.discSide = discSide;
    }

    public Integer getOppositeBoxSide() {
        return oppositeBoxSide;
    }

    public void setOppositeBoxSide(Integer oppositeBoxSide) {
        this.oppositeBoxSide = oppositeBoxSide;
    }

    public Integer getOppositeDiscSide() {
        return oppositeDiscSide;
    }

    public void setOppositeDiscSide(Integer oppositeDiscSide) {
        this.oppositeDiscSide = oppositeDiscSide;
    }

    public String getInnerDevice() {
        return innerDevice;
    }

    public void setInnerDevice(String innerDevice) {
        this.innerDevice = innerDevice;
    }

    public String getBranchingUnit() {
        return branchingUnit;
    }

    public void setBranchingUnit(String branchingUnit) {
        this.branchingUnit = branchingUnit;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    protected Serializable pkVal() {
        return this.jumpFiberId;
    }

    @Override
    public String toString() {
        return "JumpFiberInfo{" +
        "jumpFiberId=" + jumpFiberId +
        ", deviceId=" + deviceId +
        ", boxSide=" + boxSide +
        ", frameNo=" + frameNo +
        ", discNo=" + discNo +
        ", discSide=" + discSide +
        ", portNo=" + portNo +
        ", oppositeBoxSide=" + oppositeBoxSide +
        ", oppositeDeviceId=" + oppositeDeviceId +
        ", oppositeFrameNo=" + oppositeFrameNo +
        ", oppositeDiscSide=" + oppositeDiscSide +
        ", oppositeDiscNo=" + oppositeDiscNo +
        ", oppositePortNo=" + oppositePortNo +
        ", adapterType=" + adapterType +
        ", remark=" + remark +
        ", oppositeRemark=" + oppositeRemark +
        ", rfidCode=" + rfidCode +
        ", oppositeRfidCode=" + oppositeRfidCode +
        ", innerDevice=" + innerDevice +
        ", branchingUnit" + branchingUnit +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
