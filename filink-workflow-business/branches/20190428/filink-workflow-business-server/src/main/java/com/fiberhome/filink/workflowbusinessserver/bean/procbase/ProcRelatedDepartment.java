package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单关联单位表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-11
 */
@TableName("proc_related_department")
@Data
public class ProcRelatedDepartment extends Model<ProcRelatedDepartment> {

    private static final long serialVersionUID = 1L;

    @TableField("proc_related_dept_id")
    private String procRelatedDeptId;

    /**
     * 工单编码
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 责任单位编号
     */
    @TableField("accountability_dept")
    private String accountabilityDept;

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

    @Override
    protected Serializable pkVal() {
        return this.procRelatedDeptId;
    }

    @Override
    public String toString() {
        return "ProcRelatedDepartment{" +
        "procRelatedDeptId=" + procRelatedDeptId +
        ", procId=" + procId +
        ", accountabilityDept=" + accountabilityDept +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
