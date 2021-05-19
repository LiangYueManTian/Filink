package com.fiberhome.filink.workflowserver.req;


/**
 * @author hedongwei@wistronits.com
 * description 待办任务信息参数类
 * date 2018/12/5 9:39
 */
public class GetPendingTaskListReq {

    /**
     *  需要查询的用户信息
     */
    private String userCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
