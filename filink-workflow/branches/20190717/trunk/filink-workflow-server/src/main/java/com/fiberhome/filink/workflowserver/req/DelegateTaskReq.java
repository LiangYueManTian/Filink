package com.fiberhome.filink.workflowserver.req;


/**
 * @author hedongwei@wistronits.com
 * description 会签任务参数类
 * date 2018/11/28 16:00
 */
public class DelegateTaskReq extends ProcessBaseReq {

    /**
     *  需要会签的taskId
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
     *  会签人
     */
    private String delegate;

    /**
     *  办结的意见
     */
    private String message;

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

    public String getDelegate() {
        return delegate;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
