package com.fiberhome.filink.gatewaysecurity.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义证码验证失败异常
 */
public class ValidateCodeException extends AuthenticationException {


    public ValidateCodeException(String msg) {
        super(msg);
    }
}
