package com.fiberhome.filink.server_common.exception;
/**
 * 用户不存在异常定义 demo
 *
 * @author chaofang@wistronits.com
 * create on 2019/02/16 3:58 PM
 */
public class UserNotLoginException extends RuntimeException {

    public UserNotLoginException() { }

    public UserNotLoginException(String msg) {
        super(msg);
    }
}
