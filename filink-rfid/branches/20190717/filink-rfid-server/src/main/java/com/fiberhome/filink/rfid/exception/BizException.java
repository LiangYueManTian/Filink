package com.fiberhome.filink.rfid.exception;

import lombok.Data;

/**
 * Created by Qing on 2019/6/27.
 * 业务异常
 */
@Data
public class BizException extends RuntimeException {

    /**
     * code
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    public  BizException(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
