package com.fiberhome.filink.gateway_security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义证码验证失败异常
 * @author yaoyaun
 */
public class ValidateCodeExceprion extends AuthenticationException {


    public ValidateCodeExceprion(String msg) {
        super(msg);
    }
}
