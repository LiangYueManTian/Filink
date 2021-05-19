package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 销障工单表
 * </p>
 *
 * @author wh1701002@wistronits.com
 * @since 2019-02-15
 */
@TableName("proc_clear_failure")
@Data
public class ProcClearFailure extends Model<ProcClearFailure> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableField("proc_clear_failure_id")
    private String procClearFailureId;

    /**
     * 工单编码
     */
    @TableField("proc_id")
    private String procId;

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
        return this.procId;
    }

    @Override
    public String toString() {
        return "ProcClearFailure{" +
        "procClearFailureId=" + procClearFailureId +
        ", procId=" + procId +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
