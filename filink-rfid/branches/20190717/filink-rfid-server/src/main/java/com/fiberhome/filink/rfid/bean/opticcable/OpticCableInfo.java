package com.fiberhome.filink.rfid.bean.opticcable;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 光缆信息表
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@TableName("optic_cable_info")
public class OpticCableInfo extends Model<OpticCableInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 光缆id(uuid)
     */
    @TableId("optic_cable_id")
    private String opticCableId;

    /**
     * 光缆名字
     */
    @TableField("optic_cable_name")
    private String opticCableName;

    /**
     * 光缆级别
     */
    @TableField("optic_cable_level")
    private String opticCableLevel;

    /**
     * 本地网代码
     */
    @TableField("local_code")
    private String localCode;

    /**
     * 光缆拓扑结构
     */
    private String topology;

    /**
     * 布线类型
     */
    @TableField("wiring_type")
    private String wiringType;

    /**
     * 光缆芯数
     */
    @TableField("core_num")
    private String coreNum;

    /**
     * 业务信息
     */
    @TableField("biz_id")
    private String bizId;

    /**
     * 备注
     */
    @TableField("remark")
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

    public String getOpticCableId() {
        return opticCableId;
    }

    public void setOpticCableId(String opticCableId) {
        this.opticCableId = opticCableId;
    }
    public String getOpticCableName() {
        return opticCableName;
    }

    public void setOpticCableName(String opticCableName) {
        this.opticCableName = opticCableName;
    }
    public String getOpticCableLevel() {
        return opticCableLevel;
    }

    public void setOpticCableLevel(String opticCableLevel) {
        this.opticCableLevel = opticCableLevel;
    }
    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }
    public String getTopology() {
        return topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }
    public String getWiringType() {
        return wiringType;
    }

    public void setWiringType(String wiringType) {
        this.wiringType = wiringType;
    }
    public String getCoreNum() {
        return coreNum;
    }

    public void setCoreNum(String coreNum) {
        this.coreNum = coreNum;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    protected Serializable pkVal() {
        return this.opticCableId;
    }

    @Override
    public String toString() {
        return "OpticCableInfo{" +
        "opticCableId=" + opticCableId +
        ", opticCableName=" + opticCableName +
        ", opticCableLevel=" + opticCableLevel +
        ", localCode=" + localCode +
        ", topology=" + topology +
        ", wiringType=" + wiringType +
        ", coreNum=" + coreNum +
        ", bizId=" + bizId +
        ", remark=" + remark +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
