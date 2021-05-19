package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-04-16
 */
@TableName("inspection_task_related_job")
public class InspectionTaskRelatedJob extends Model<InspectionTaskRelatedJob> {

    private static final long serialVersionUID = 1L;

    @TableId("inspection_task_related_id")
    private String inspectionTaskRelatedId;

    /**
     * 巡检任务定时任务名称
     */
    @TableField("inspection_task_job_name")
    private String inspectionTaskJobName;

    /**
     * 巡检任务编号
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private String isDeleted;

    public String getInspectionTaskRelatedId() {
        return inspectionTaskRelatedId;
    }

    public void setInspectionTaskRelatedId(String inspectionTaskRelatedId) {
        this.inspectionTaskRelatedId = inspectionTaskRelatedId;
    }

    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskId) {
        this.inspectionTaskId = inspectionTaskId;
    }
    public String getInspectionTaskJobName() {
        return inspectionTaskJobName;
    }

    public void setInspectionTaskJobName(String inspectionTaskJobName) {
        this.inspectionTaskJobName = inspectionTaskJobName;
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

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    protected Serializable pkVal() {
        return this.inspectionTaskId;
    }

    @Override
    public String toString() {
        return "InspectionTaskRelatedJob{" +
        "inspectionTaskId=" + inspectionTaskId +
        ", inspectionTaskJobName=" + inspectionTaskJobName +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        "}";
    }
}
