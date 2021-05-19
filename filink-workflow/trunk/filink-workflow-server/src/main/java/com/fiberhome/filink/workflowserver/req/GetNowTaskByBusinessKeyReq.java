package com.fiberhome.filink.workflowserver.req;


/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/2/20 16:44
 */
public class GetNowTaskByBusinessKeyReq {

    /**
     * 业务key
     */
    private String bussinessKey;

    public String getBussinessKey() {
        return bussinessKey;
    }

    public void setBussinessKey(String bussinessKey) {
        this.bussinessKey = bussinessKey;
    }
}
