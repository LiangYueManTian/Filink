package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 巡检任务设施表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@TableName("inspection_task_device")
public class InspectionTaskDevice extends Model<InspectionTaskDevice> {

    private static final long serialVersionUID = 1L;

    @TableId("inspection_task_device_id")
    private String inspectionTaskDeviceId;

    /**
     * 编号
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 设施编号
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设施区域id
     */
    @TableField("device_area_id")
    private String deviceAreaId;

    /**
     * 选择设备的类型 区域  1  设备 0
     */
    @TableField("select_device_type")
    private String selectDeviceType;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    public String getInspectionTaskDeviceId() {
        return inspectionTaskDeviceId;
    }

    public void setInspectionTaskDeviceId(String inspectionTaskDeviceId) {
        this.inspectionTaskDeviceId = inspectionTaskDeviceId;
    }
    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskId) {
        this.inspectionTaskId = inspectionTaskId;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceAreaId() {
        return deviceAreaId;
    }

    public void setDeviceAreaId(String deviceAreaId) {
        this.deviceAreaId = deviceAreaId;
    }
    public String getSelectDeviceType() {
        return selectDeviceType;
    }

    public void setSelectDeviceType(String selectDeviceType) {
        this.selectDeviceType = selectDeviceType;
    }
    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.inspectionTaskDeviceId;
    }

    @Override
    public String toString() {
        return "InspectionTaskDevice{" +
        "inspectionTaskDeviceId=" + inspectionTaskDeviceId +
        ", inspectionTaskId=" + inspectionTaskId +
        ", deviceId=" + deviceId +
        ", deviceAreaId=" + deviceAreaId +
        ", selectDeviceType=" + selectDeviceType +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
