package com.fiberhome.filink.rfid.bean.fibercore;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 成端信息表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@TableName("port_cable_core_info")
public class PortCableCoreInfo extends Model<PortCableCoreInfo> {

    private static final long serialVersionUID = 1L;

    @TableId("port_core_id")
    private String portCoreId;

    @TableId("port_no")
    private String portNo;

    /**
     * 所属设施id
     */
    @TableField("resource_device_id")
    private String resourceDeviceId;

    /**
     * 所属箱架ab面
     */
    @TableField("resource_box_side")
    private Integer resourceBoxSide;

    /**
     * 所属框编号
     */
    @TableField("resource_frame_no")
    private String resourceFrameNo;

    /**
     * 所属盘ab面
     */
    @TableField("resource_disc_side")
    private Integer resourceDiscSide;

    /**
     * 所属盘编号
     */
    @TableField("resource_disc_no")
    private String resourceDiscNo;

    /**
     * 对端所属光缆段
     */
    @TableField("opposite_resource")
    private String oppositeResource;

    /**
     * 对端纤芯号
     */
    @TableField("opposite_cable_core_no")
    private String oppositeCableCoreNo;

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

    public String getPortCoreId() {
        return portCoreId;
    }

    public void setPortCoreId(String portCoreId) {
        this.portCoreId = portCoreId;
    }

    public String getOppositeResource() {
        return oppositeResource;
    }

    public void setOppositeResource(String oppositeResource) {
        this.oppositeResource = oppositeResource;
    }
    public String getOppositeCableCoreNo() {
        return oppositeCableCoreNo;
    }

    public void setOppositeCableCoreNo(String oppositeCableCoreNo) {
        this.oppositeCableCoreNo = oppositeCableCoreNo;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }

    public String getResourceDeviceId() {
        return resourceDeviceId;
    }

    public void setResourceDeviceId(String resourceDeviceId) {
        this.resourceDeviceId = resourceDeviceId;
    }

    public String getResourceFrameNo() {
        return resourceFrameNo;
    }

    public void setResourceFrameNo(String resourceFrameNo) {
        this.resourceFrameNo = resourceFrameNo;
    }

    public String getResourceDiscNo() {
        return resourceDiscNo;
    }

    public void setResourceDiscNo(String resourceDiscNo) {
        this.resourceDiscNo = resourceDiscNo;
    }

    public Integer getResourceBoxSide() {
        return resourceBoxSide;
    }

    public void setResourceBoxSide(Integer resourceBoxSide) {
        this.resourceBoxSide = resourceBoxSide;
    }

    public Integer getResourceDiscSide() {
        return resourceDiscSide;
    }

    public void setResourceDiscSide(Integer resourceDiscSide) {
        this.resourceDiscSide = resourceDiscSide;
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

    @Override
    protected Serializable pkVal() {
        return this.portCoreId;
    }

    @Override
    public String toString() {
        return "PortCableCoreInfo{" +
        "portCoreId=" + portCoreId +
        ", portNo=" + portNo +
        ", resourceDeviceId=" + resourceDeviceId +
        ", resourceBoxSide=" + resourceBoxSide +
        ", resourceFrameNo=" + resourceFrameNo +
        ", resourceDiscSide=" + resourceDiscSide +
        ", resourceDiscNo=" + resourceDiscNo +
        ", oppositeResource=" + oppositeResource +
        ", oppositeCableCoreNo=" + oppositeCableCoreNo +
        ", remark=" + remark +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
