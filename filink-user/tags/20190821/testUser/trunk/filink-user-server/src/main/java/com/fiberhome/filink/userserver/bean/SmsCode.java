package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class SmsCode {
    /**
     * 短信code
     */
    private String smsCode;

    public SmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
