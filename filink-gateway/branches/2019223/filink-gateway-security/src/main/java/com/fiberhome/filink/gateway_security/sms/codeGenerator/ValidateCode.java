package com.fiberhome.filink.gateway_security.sms.codeGenerator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码(图片/短信)
 * @author yuanyao@wistronits.com
 * create on 2018/11/18 9:26 PM
 */
@Data
public class ValidateCode implements Serializable {

    /**随机数*/
    private String code;

    /**过期时间*/
    private LocalDateTime expireTime;

    /**
     *
     * @param code 随机数
     * @param expireInt 在多少秒过期
     */
    public ValidateCode(String code, int expireInt) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireInt);
    }

    /**
     *
     * @param code 随机数
     * @param expireInt 在多少秒过期
     */
    public ValidateCode(String code, LocalDateTime expireInt) {
        this.code = code;
        this.expireTime = expireInt;
    }

    public boolean isExpried() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
