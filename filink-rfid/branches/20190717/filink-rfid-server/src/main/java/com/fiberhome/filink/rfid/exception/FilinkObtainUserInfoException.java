package com.fiberhome.filink.rfid.exception;

/**
 * <p>
 * 获取用户信息异常
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-06-14
 */
public class FilinkObtainUserInfoException extends RuntimeException {

    public FilinkObtainUserInfoException() {
    }

    public FilinkObtainUserInfoException(String msg) {
        super(msg);
    }
}
