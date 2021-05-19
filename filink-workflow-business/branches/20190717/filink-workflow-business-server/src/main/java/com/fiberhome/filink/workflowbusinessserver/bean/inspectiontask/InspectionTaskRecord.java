package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 巡检任务记录表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@TableName("inspection_task_record")
public class InspectionTaskRecord extends Model<InspectionTaskRecord> {

    private static final long serialVersionUID = 1L;

    @TableId("inspection_task_record_id")
    private String inspectionTaskRecordId;

    /**
     * 巡检任务编号
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    @TableField("inspection_task_name")
    private String inspectionTaskName;

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
     * 巡检区域编号
     */
    @TableField("inspection_area_id")
    private String inspectionAreaId;


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



    public String getInspectionTaskRecordId() {
        return inspectionTaskRecordId;
    }

    public void setInspectionTaskRecordId(String inspectionTaskRecordId) {
        this.inspectionTaskRecordId = inspectionTaskRecordId;
    }
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

    public String getInspectionAreaId() {
        return inspectionAreaId;
    }

    public void setInspectionAreaId(String inspectionAreaId) {
        this.inspectionAreaId = inspectionAreaId;
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
        return this.inspectionTaskRecordId;
    }

    @Override
    public String toString() {
        return "InspectionTaskRecord{" +
        "inspectionTaskRecordId=" + inspectionTaskRecordId +
        ", inspectionTaskId=" + inspectionTaskId +
        ", inspectionTaskName=" + inspectionTaskName +
        ", taskPeriod=" + taskPeriod +
        ", procPlanDate=" + procPlanDate +
        ", taskStartTime=" + taskStartTime +
        ", taskEndTime=" + taskEndTime +
        ", inspectionAreaId=" + inspectionAreaId +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
