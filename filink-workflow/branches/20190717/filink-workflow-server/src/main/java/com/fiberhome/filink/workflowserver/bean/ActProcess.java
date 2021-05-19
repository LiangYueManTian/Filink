package com.fiberhome.filink.workflowserver.bean;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 流程模板记录表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-13
 */
@TableName("act_process")
public class ActProcess extends Model<ActProcess> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程编号
     */
    @TableId("proc_id")
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
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 流程查看链接
     */
    @TableField("proc_action")
    private String procAction;

    /**
     * 流程工作空间
     */
    @TableField("proc_namespace")
    private String procNamespace;

    /**
     * 是否已经发布   0 未发布 1 已发布  修改原来发布的流程需要更改状态
     */
    @TableField("is_deploy")
    private String isDeploy;

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
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getProcAction() {
        return procAction;
    }

    public void setProcAction(String procAction) {
        this.procAction = procAction;
    }
    public String getProcNamespace() {
        return procNamespace;
    }

    public void setProcNamespace(String procNamespace) {
        this.procNamespace = procNamespace;
    }
    public String getIsDeploy() {
        return isDeploy;
    }

    public void setIsDeploy(String isDeploy) {
        this.isDeploy = isDeploy;
    }

    @Override
    protected Serializable pkVal() {
        return this.procId;
    }

    @Override
    public String toString() {
        return "ActProcess{" +
        "procId=" + procId +
        ", procType=" + procType +
        ", procName=" + procName +
        ", fileName=" + fileName +
        ", procAction=" + procAction +
        ", procNamespace=" + procNamespace +
        ", isDeploy=" + isDeploy +
        "}";
    }
}
