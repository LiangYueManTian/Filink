package com.fiberhome.filink.workflowserver.bean;

import java.io.File;
import java.io.Serializable;

/**
 * @author hedongwei@wistronits.com
 * description 流程定义类
 * date 15:14 2019/2/11
 */
public class WorkflowBean implements Serializable {

    private static final long serialVersionUID = -1149169000261632370L;

    /**
     *  流程定义部署文件
     */
    private File file;

    /**
     *  流程定义名称
     */
    private String filename;

    /**
     *  申请单ID
     */
    private String id;

    /**
     *  部署对象ID
     */
    private String deploymentId;

    /**
     *  资源文件名称
     */
    private String imageName;

    /**
     *  流程编号
     */
    private String taskId;

    /**
     *  连线名称
     */
    private String outcome;

    /**
     *  备注
     */
    private String comment;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
