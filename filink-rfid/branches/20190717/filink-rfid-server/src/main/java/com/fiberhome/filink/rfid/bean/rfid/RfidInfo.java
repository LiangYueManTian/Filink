package com.fiberhome.filink.rfid.bean.rfid;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * rfid信息表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-11
 */
@TableName("rfid_info")
public class RfidInfo extends Model<RfidInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id(UUID)
     */
    @TableId("rfid_id")
    private String rfidId;

    @TableField("rfid_code")
    private String rfidCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * rfid类型（箱架类型、盘类型、端口类型、光缆类型、跳纤类型）
     */
    @TableField("rfid_type")
    private String rfidType;

    /**
     * 标记类型（RFID、二维码）
     */
    @TableField("mark_type")
    private String markType;

    /**
     * 标签状态（0、正常；1、异常）
     */
    @TableField("rfid_status")
    private String rfidStatus;

    /**
     * 设施id
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 删除标记
     */
    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    public String getRfidId() {
        return rfidId;
    }

    public void setRfidId(String rfidId) {
        this.rfidId = rfidId;
    }
    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRfidType() {
        return rfidType;
    }

    public void setRfidType(String rfidType) {
        this.rfidType = rfidType;
    }
    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }
    public String getRfidStatus() {
        return rfidStatus;
    }

    public void setRfidStatus(String rfidStatus) {
        this.rfidStatus = rfidStatus;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    protected Serializable pkVal() {
        return this.rfidId;
    }

    @Override
    public String toString() {
        return "RfidInfo{" +
        "rfidId=" + rfidId +
        ", rfidCode=" + rfidCode +
        ", remark=" + remark +
        ", rfidType=" + rfidType +
        ", markType=" + markType +
        ", rfidStatus=" + rfidStatus +
        ", deviceId=" + deviceId +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
