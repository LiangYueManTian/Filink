package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 巡检任务关联单位表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
@TableName("inspection_task_department")
public class InspectionTaskDepartment extends Model<InspectionTaskDepartment> {

    private static final long serialVersionUID = 1L;

    @TableId("inspection_task_dept_id")
    private String inspectionTaskDeptId;

    /**
     * 巡检任务名称
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 责任单位编号
     */
    @TableField("accountability_dept")
    private String accountabilityDept;

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

    public String getInspectionTaskDeptId() {
        return inspectionTaskDeptId;
    }

    public void setInspectionTaskDeptId(String inspectionTaskDeptId) {
        this.inspectionTaskDeptId = inspectionTaskDeptId;
    }
    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskName) {
        this.inspectionTaskId = inspectionTaskName;
    }
    public String getAccountabilityDept() {
        return accountabilityDept;
    }

    public void setAccountabilityDept(String accountabilityDept) {
        this.accountabilityDept = accountabilityDept;
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
        return this.inspectionTaskDeptId;
    }

    @Override
    public String toString() {
        return "InspectionTaskDepartment{" +
        "inspectionTaskDeptId=" + inspectionTaskDeptId +
        ", inspectionTaskName=" + inspectionTaskId +
        ", accountabilityDept=" + accountabilityDept +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
