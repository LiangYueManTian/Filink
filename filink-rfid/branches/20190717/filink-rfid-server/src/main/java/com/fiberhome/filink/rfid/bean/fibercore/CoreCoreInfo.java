package com.fiberhome.filink.rfid.bean.fibercore;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 熔接信息表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-27
 */
@TableName("core_core_info")
public class CoreCoreInfo extends Model<CoreCoreInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 纤芯熔接主键id(UUID)
     */
    @TableId("core_core_id")
    private String coreCoreId;

    /**
     * 中间节点设施
     */
    @TableField("intermediate_node_device_id")
    private String intermediateNodeDeviceId;

    /**
     * 所属光缆段
     */
    private String resource;

    /**
     * 纤芯号
     */
    @TableField("cable_core_no")
    private String cableCoreNo;

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

    public String getCoreCoreId() {
        return coreCoreId;
    }

    public void setCoreCoreId(String coreCoreId) {
        this.coreCoreId = coreCoreId;
    }
    public String getIntermediateNodeDeviceId() {
        return intermediateNodeDeviceId;
    }

    public void setIntermediateNodeDeviceId(String intermediateNodeDeviceId) {
        this.intermediateNodeDeviceId = intermediateNodeDeviceId;
    }
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getCableCoreNo() {
        return cableCoreNo;
    }

    public void setCableCoreNo(String cableCoreNo) {
        this.cableCoreNo = cableCoreNo;
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
        return this.coreCoreId;
    }

    @Override
    public String toString() {
        return "CoreCoreInfo{" +
        "coreCoreId=" + coreCoreId +
        ", intermediateNodeDeviceId=" + intermediateNodeDeviceId +
        ", resource=" + resource +
        ", cableCoreNo=" + cableCoreNo +
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
