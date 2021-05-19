package com.fiberhome.filink.onenetserver.constant;

/**
 * <p>
 *   oneNet平台HTTP请求Status枚举类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public enum NBStatus {
    /**1*/
    HTTP_REQUEST_ERROR(1, "http request error"),
    /**2*/
    RECEIVE_ERROR(2,"receive error");
    /**
     * 错误描述
     */
    private String error;
    /**
     * 调用错误码
     */
    private int errorNo;

    NBStatus(int errorNo, String error) {
        this.error = error;
        this.errorNo = errorNo;
    }
    public String getError() {
        return error;
    }

    public int getErrorNo() {
        return errorNo;
    }
}
