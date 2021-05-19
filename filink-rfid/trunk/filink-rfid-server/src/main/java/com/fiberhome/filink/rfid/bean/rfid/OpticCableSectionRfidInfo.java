package com.fiberhome.filink.rfid.bean.rfid;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 光缆段rfid信息表
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
@TableName("optic_cable_section_rfid_info")
public class OpticCableSectionRfidInfo extends Model<OpticCableSectionRfidInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id(UUID)
     */
    @TableId("optic_status_id")
    private String opticStatusId;

    /**
     * 光缆段id
     */
    @TableField("optic_cable_section_id")
    private String opticCableSectionId;

    /**
     * 智能标签信息
     */
    @TableField("rfid_code")
    private String rfidCode;

    /**
     * 经纬度
     */
    private String position;

    /**
     * 埋深
     */
    @TableField("ruried_depth")
    private String ruriedDepth;

    /**
     * 温度
     */
    private String temperature;

    /**
     * 是否震动
     */
    private String vibrate;

    /**
     * 距离起始位置（m）
     */
    @TableField("distance_starting_position")
    private String distanceStartingPosition;

    /**
     * 处于光缆段的顺序号（手机上传）
     */
    @TableField("rfid_seq")
    private String rfidSeq;

    /**
     * 备注
     */
    private String remark;

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

    public String getOpticStatusId() {
        return opticStatusId;
    }

    public void setOpticStatusId(String opticStatusId) {
        this.opticStatusId = opticStatusId;
    }

    public String getOpticCableSectionId() {
        return opticCableSectionId;
    }

    public void setOpticCableSectionId(String opticCableSectionId) {
        this.opticCableSectionId = opticCableSectionId;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRuriedDepth() {
        return ruriedDepth;
    }

    public void setRuriedDepth(String ruriedDepth) {
        this.ruriedDepth = ruriedDepth;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVibrate() {
        return vibrate;
    }

    public void setVibrate(String vibrate) {
        this.vibrate = vibrate;
    }

    public String getDistanceStartingPosition() {
        return distanceStartingPosition;
    }

    public void setDistanceStartingPosition(String distanceStartingPosition) {
        this.distanceStartingPosition = distanceStartingPosition;
    }

    public String getRfidSeq() {
        return rfidSeq;
    }

    public void setRfidSeq(String rfidSeq) {
        this.rfidSeq = rfidSeq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return this.opticStatusId;
    }

    @Override
    public String toString() {
        return "OpticCableSectionRfidInfo{" +
                "opticStatusId=" + opticStatusId +
                ", opticCableSectionId=" + opticCableSectionId +
                ", rfidCode=" + rfidCode +
                ", position=" + position +
                ", ruriedDepth=" + ruriedDepth +
                ", temperature=" + temperature +
                ", vibrate=" + vibrate +
                ", distanceStartingPosition=" + distanceStartingPosition +
                ", rfidSeq=" + rfidSeq +
                ", remark=" + remark +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", createUser=" + createUser +
                ", updateTime=" + updateTime +
                ", updateUser=" + updateUser +
                "}";
    }
}
