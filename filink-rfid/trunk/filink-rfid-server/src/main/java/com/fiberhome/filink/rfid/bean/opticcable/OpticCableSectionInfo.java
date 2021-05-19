package com.fiberhome.filink.rfid.bean.opticcable;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 光缆段信息表
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@TableName("optic_cable_section_info")
public class OpticCableSectionInfo extends Model<OpticCableSectionInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 光缆段主键id(uuid)
     */
    @TableId("optic_cable_section_id")
    private String opticCableSectionId;

    /**
     * 光缆段名字
     */
    @TableField("optic_cable_section_name")
    private String opticCableSectionName;

    /**
     * 所属光缆id
     */
    @TableField("belong_optic_cable_id")
    private String belongOpticCableId;

    /**
     * 长度
     */
    @TableField("length")
    private String length;

    /**
     * 所属区域
     */
    @TableField("area_id")
    private String areaId;
    /**
     * 起始节点
     */
    @TableField("start_node")
    private String startNode;

    /**
     * 起始节点设施类型
     */
    @TableField("start_node_device_type")
    private String startNodeDeviceType;

    /**
     * 终止节点
     */
    @TableField("termination_node")
    private String terminationNode;

    /**
     * 终止节点设施类型
     */
    @TableField("termination_node_device_type")
    private String terminationNodeDeviceType;

    /**
     * 光缆芯数
     */
    @TableField("core_num")
    private String coreNum;
    /**
     * 光缆已占用芯数
     */
    @TableField("used_core_num")
    private String usedCoreNum;

    /**
     * 状态
     */
    private String status;

    /**
     * 序列号
     */
    @TableField("serial_num")
    private String serialNum;

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

    public String getOpticCableSectionId() {
        return opticCableSectionId;
    }

    public void setOpticCableSectionId(String opticCableSectionId) {
        this.opticCableSectionId = opticCableSectionId;
    }
    public String getOpticCableSectionName() {
        return opticCableSectionName;
    }

    public void setOpticCableSectionName(String opticCableSectionName) {
        this.opticCableSectionName = opticCableSectionName;
    }
    public String getBelongOpticCableId() {
        return belongOpticCableId;
    }

    public void setBelongOpticCableId(String belongOpticCableId) {
        this.belongOpticCableId = belongOpticCableId;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }
    public String getStartNodeDeviceType() {
        return startNodeDeviceType;
    }

    public void setStartNodeDeviceType(String startNodeDeviceType) {
        this.startNodeDeviceType = startNodeDeviceType;
    }
    public String getTerminationNode() {
        return terminationNode;
    }

    public void setTerminationNode(String terminationNode) {
        this.terminationNode = terminationNode;
    }
    public String getTerminationNodeDeviceType() {
        return terminationNodeDeviceType;
    }

    public void setTerminationNodeDeviceType(String terminationNodeDeviceType) {
        this.terminationNodeDeviceType = terminationNodeDeviceType;
    }
    public String getCoreNum() {
        return coreNum;
    }

    public void setCoreNum(String coreNum) {
        this.coreNum = coreNum;
    }

    public String getUsedCoreNum() {
        return usedCoreNum;
    }

    public void setUsedCoreNum(String usedCoreNum) {
        this.usedCoreNum = usedCoreNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
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
        return this.opticCableSectionId;
    }

    @Override
    public String toString() {
        return "OpticCableSectionInfo{" +
        "opticCableSectionId=" + opticCableSectionId +
        ", opticCableSectionName=" + opticCableSectionName +
        ", belongOpticCableId=" + belongOpticCableId +
        ", length=" + length +
        ", areaId=" + areaId +
        ", startNode=" + startNode +
        ", startNodeDeviceType=" + startNodeDeviceType +
        ", terminationNode=" + terminationNode +
        ", terminationNodeDeviceType=" + terminationNodeDeviceType +
        ", coreNum=" + coreNum +
        ", usedCoreNum=" + usedCoreNum +
        ", status=" + status +
        ", serialNum=" + serialNum +
        ", remark=" + remark +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
