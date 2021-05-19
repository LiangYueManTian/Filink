package com.fiberhome.filink.workflowserver.req;

import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description 发起流程参数类
 * date 2018/11/28 14:11
 */
public class StartProcessReq extends ProcessBaseReq {

    /**
     *  流程编号
     */
    private String procCode;

    /**
     *  流程类型
     */
    private String procType;

    /**
     *  用户code
     */
    private String userCode;

    /**
     *  流程标题
     */
    private String title;

    /**
     *  参数信息
     */
    private Map<String, Object> variables;

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getProcType() {
        return procType;
    }

    public void setProcType(String procType) {
        this.procType = procType;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
