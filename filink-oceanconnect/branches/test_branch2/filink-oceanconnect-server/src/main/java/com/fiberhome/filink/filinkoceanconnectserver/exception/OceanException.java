package com.fiberhome.filink.filinkoceanconnectserver.exception;

/**
 * 平台异常
 * @author CongcaiYu
 */
public class OceanException extends RuntimeException {

    private String msg;

    public OceanException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
