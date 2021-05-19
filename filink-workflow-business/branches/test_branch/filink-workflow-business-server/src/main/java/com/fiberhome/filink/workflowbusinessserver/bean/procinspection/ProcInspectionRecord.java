package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 工单巡检记录
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@TableName("proc_inspection_record")
public class ProcInspectionRecord extends Model<ProcInspectionRecord> {

    private static final long serialVersionUID = 1L;

    @TableId("proc_inspection_record_id")
    private String procInspectionRecordId;

    /**
     * 巡检任务名称
     */
    @TableField("inspection_task_name")
    private String inspectionTaskName;

    /**
     * 巡检任务编号
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 设施编号
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设施名称
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 设施区域编号
     */
    @TableField("device_area_id")
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    @TableField("device_area_name")
    private String deviceAreaName;

    /**
     * 关联工单编码
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 巡检时间
     */
    @TableField("inspection_time")
    private Date inspectionTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 异常描述
     */
    @TableField("exception_description")
    private String exceptionDescription;

    /**
     * 巡检结果 0 正常 1 异常
     */
    private String result;

    /**
     * 资源匹配情况
     */
    @TableField("resource_matching")
    private String resourceMatching;

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

    public String getProcInspectionRecordId() {
        return procInspectionRecordId;
    }

    public void setProcInspectionRecordId(String procInspectionRecordId) {
        this.procInspectionRecordId = procInspectionRecordId;
    }
    public String getInspectionTaskName() {
        return inspectionTaskName;
    }

    public void setInspectionTaskName(String inspectionTaskName) {
        this.inspectionTaskName = inspectionTaskName;
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
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceAreaId() {
        return deviceAreaId;
    }

    public void setDeviceAreaId(String deviceAreaId) {
        this.deviceAreaId = deviceAreaId;
    }

    public String getDeviceAreaName() {
        return deviceAreaName;
    }

    public void setDeviceAreaName(String deviceAreaName) {
        this.deviceAreaName = deviceAreaName;
    }

    public String getProcId() {
        return procId;
    }

    public void setProcId(String procId) {
        this.procId = procId;
    }

    public Date getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(Date inspectionTime) {
        this.inspectionTime = inspectionTime;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getExceptionDescription() {
        return exceptionDescription;
    }

    public void setExceptionDescription(String exceptionDescription) {
        this.exceptionDescription = exceptionDescription;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String getResourceMatching() {
        return resourceMatching;
    }

    public void setResourceMatching(String resourceMatching) {
        this.resourceMatching = resourceMatching;
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
        return this.procInspectionRecordId;
    }

    @Override
    public String toString() {
        return "ProcInspectionRecord{" +
        "procInspectionRecordId=" + procInspectionRecordId +
        ", inspectionTaskName=" + inspectionTaskName +
        ", inspectionTaskId=" + inspectionTaskId +
        ", deviceId=" + deviceId +
        ", deviceName=" + deviceName +
        ", deviceType=" + deviceType +
        ", deviceAreaId=" + deviceAreaId +
        ", deviceAreaName=" + deviceAreaName +
        ", procId=" + procId +
        ", inspectionTime=" + inspectionTime +
        ", description=" + description +
        ", exceptionDescription=" + exceptionDescription +
        ", result=" + result +
        ", resourceMatching=" + resourceMatching +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
