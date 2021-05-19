package com.fiberhome.filink.workflowserver.req;


/**
 * @author hedongwei@wistronits.com
 * description 办理用户单据的数量
 * date 2018/12/5 9:37
 */
public class GetApplyRecordCountReq {

    /**
     *  用户的编码
     */
    private String userCode;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
