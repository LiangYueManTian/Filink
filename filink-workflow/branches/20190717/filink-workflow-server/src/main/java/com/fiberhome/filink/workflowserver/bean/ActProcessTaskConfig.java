package com.fiberhome.filink.workflowserver.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 流程配置表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-20
 */
@TableName("act_process_task_config")
public class ActProcessTaskConfig extends Model<ActProcessTaskConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程编号
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 流程类型
     */
    @TableField("proc_type")
    private String procType;

    /**
     * 流程名称
     */
    @TableField("proc_name")
    private String procName;

    /**
     * 流程任务名称
     */
    @TableField("proc_task_name")
    private String procTaskName;

    /**
     * 流程任务描述
     */
    @TableField("proc_task_desc")
    private String procTaskDesc;

    /**
     * 流程状态名称
     */
    @TableField("proc_status_name")
    private String procStatusName;

    /**
     * 流程状态code
     */
    @TableField("proc_status_code")
    private String procStatusCode;

    public String getProcId() {
        return procId;
    }

    public void setProcId(String procId) {
        this.procId = procId;
    }
    public String getProcType() {
        return procType;
    }

    public void setProcType(String procType) {
        this.procType = procType;
    }
    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }
    public String getProcTaskName() {
        return procTaskName;
    }

    public void setProcTaskName(String procTaskName) {
        this.procTaskName = procTaskName;
    }
    public String getProcTaskDesc() {
        return procTaskDesc;
    }

    public void setProcTaskDesc(String procTaskDesc) {
        this.procTaskDesc = procTaskDesc;
    }
    public String getProcStatusName() {
        return procStatusName;
    }

    public void setProcStatusName(String procStatusName) {
        this.procStatusName = procStatusName;
    }
    public String getProcStatusCode() {
        return procStatusCode;
    }

    public void setProcStatusCode(String procStatusCode) {
        this.procStatusCode = procStatusCode;
    }

    @Override
    protected Serializable pkVal() {
        return this.procId;
    }

    @Override
    public String toString() {
        return "ActProcessTaskConfig{" +
        "procId=" + procId +
        ", procType=" + procType +
        ", procName=" + procName +
        ", procTaskName=" + procTaskName +
        ", procTaskDesc=" + procTaskDesc +
        ", procStatusName=" + procStatusName +
        ", procStatusCode=" + procStatusCode +
        "}";
    }
}
