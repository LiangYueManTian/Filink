package com.fiberhome.filink.gateway_security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证异常
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 6:05 PM
 */
public class AuthenticationUserException extends AuthenticationException {

    public AuthenticationUserException(String msg) {
        super(msg);
    }

}
