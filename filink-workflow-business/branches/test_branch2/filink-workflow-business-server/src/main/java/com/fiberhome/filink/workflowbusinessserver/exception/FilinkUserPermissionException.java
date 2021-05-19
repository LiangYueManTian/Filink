package com.fiberhome.filink.workflowbusinessserver.exception;

/**
 * <p>
 * 用户权限信息异常
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-04-18
 */
public class FilinkUserPermissionException extends RuntimeException {

    public FilinkUserPermissionException() {
    }

    public FilinkUserPermissionException(String msg) {
        super(msg);
    }
}
