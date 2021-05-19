package com.fiberhome.filink.workflowserver.req;

import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description 办结任务参数类
 * date 2018/11/28 16:00
 */
public class CompleteTaskReq extends ProcessBaseReq {

    /**
     *  需要办结的taskId
     */
    private String taskId;

    /**
     *  流程code信息
     */
    private String procCode;

    /**
     *  用户code
     */
    private String userCode;

    /**
     *  办结的意见
     */
    private String message;

    /**
     *  参数信息
     */
    private Map<String, Object> variables;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
