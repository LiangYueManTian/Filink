package com.fiberhome.filink.rfid.exception;

/**
 * <p>
 * 用户权限信息异常
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-06-14
 */
public class FilinkUserPermissionException extends RuntimeException {

    public FilinkUserPermissionException() {
    }

    public FilinkUserPermissionException(String msg) {
        super(msg);
    }
}
