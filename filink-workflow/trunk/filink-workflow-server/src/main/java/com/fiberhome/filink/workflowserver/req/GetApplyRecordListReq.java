package com.fiberhome.filink.workflowserver.req;

/**
 * @author hedongwei@wistronits.com
 * description 用户办理单据的信息参数类
 * date 2018/12/5 9:43
 */
public class GetApplyRecordListReq {

    /**
     *  用户编码
     */
    private String userCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
