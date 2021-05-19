package com.fiberhome.filink.server_common.exception;

/**
 * 用户不存在异常定义 demo
 *
 * @author yuanyao@wistronits.com
 * create on 2018/11/13 3:58 PM
 */
public class UserNotExistException extends RuntimeException {

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public UserNotExistException() {
        super("user is not exist");
    }
}
