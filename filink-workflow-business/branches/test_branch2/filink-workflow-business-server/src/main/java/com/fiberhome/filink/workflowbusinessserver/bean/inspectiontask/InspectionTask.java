package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 巡检任务表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@TableName("inspection_task")
public class InspectionTask extends Model<InspectionTask> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    @TableField("inspection_task_name")
    private String inspectionTaskName;

    /**
     * 巡检任务状态 1 未巡检 2 巡检中 3 已完成
     */
    @TableField("inspection_task_status")
    private String inspectionTaskStatus;

    /**
     * 巡检任务类型 1 例行巡检
     */
    @TableField("inspection_task_type")
    private String inspectionTaskType;

    /**
     * 巡检周期
     */
    @TableField("task_period")
    private Integer taskPeriod;

    /**
     * 巡检工单期望用时
     */
    @TableField("proc_plan_date")
    private Integer procPlanDate;

    /**
     * 巡检任务开始时间
     */
    @TableField("task_start_time")
    private Date taskStartTime;

    /**
     * 巡检任务结束时间
     */
    @TableField("task_end_time")
    private Date taskEndTime;

    /**
     * 是否启用巡检任务 0 禁用 1 启用
     */
    @TableField("is_open")
    private String isOpen;

    /**
     * 是否选择全集
     */
    @TableField("is_select_all")
    private String isSelectAll;

    /**
     * 巡检区域编号
     */
    @TableField("inspection_area_id")
    private String inspectionAreaId;

    /**
     * 巡检设施总数
     */
    @TableField("inspection_device_count")
    private Integer inspectionDeviceCount;

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

    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskId) {
        this.inspectionTaskId = inspectionTaskId;
    }
    public String getInspectionTaskName() {
        return inspectionTaskName;
    }

    public void setInspectionTaskName(String inspectionTaskName) {
        this.inspectionTaskName = inspectionTaskName;
    }
    public Integer getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(Integer taskPeriod) {
        this.taskPeriod = taskPeriod;
    }
    public Integer getProcPlanDate() {
        return procPlanDate;
    }

    public void setProcPlanDate(Integer procPlanDate) {
        this.procPlanDate = procPlanDate;
    }
    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }
    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getIsSelectAll() {
        return isSelectAll;
    }

    public void setIsSelectAll(String isSelectAll) {
        this.isSelectAll = isSelectAll;
    }

    public String getInspectionAreaId() {
        return inspectionAreaId;
    }

    public void setInspectionAreaId(String inspectionAreaId) {
        this.inspectionAreaId = inspectionAreaId;
    }

    public Integer getInspectionDeviceCount() {
        return inspectionDeviceCount;
    }

    public void setInspectionDeviceCount(Integer inspectionDeviceCount) {
        this.inspectionDeviceCount = inspectionDeviceCount;
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

    public String getInspectionTaskStatus() {
        return inspectionTaskStatus;
    }

    public void setInspectionTaskStatus(String inspectionTaskStatus) {
        this.inspectionTaskStatus = inspectionTaskStatus;
    }

    public String getInspectionTaskType() {
        return inspectionTaskType;
    }

    public void setInspectionTaskType(String inspectionTaskType) {
        this.inspectionTaskType = inspectionTaskType;
    }

    @Override
    protected Serializable pkVal() {
        return this.inspectionTaskId;
    }

    @Override
    public String toString() {
        return "InspectionTask{" +
        "inspectionTaskId=" + inspectionTaskId +
        ", inspectionTaskName=" + inspectionTaskName +
        ", inspectionTaskStatus=" + inspectionTaskStatus +
        ", inspectionTaskType=" + inspectionTaskType +
        ", taskPeriod=" + taskPeriod +
        ", procPlanDate=" + procPlanDate +
        ", taskStartTime=" + taskStartTime +
        ", taskEndTime=" + taskEndTime +
        ", isOpen=" + isOpen +
        ", isSelectAll=" + isSelectAll +
        ", inspectionAreaId=" + inspectionAreaId +
        ", inspectionDeviceCount="+ inspectionDeviceCount +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
