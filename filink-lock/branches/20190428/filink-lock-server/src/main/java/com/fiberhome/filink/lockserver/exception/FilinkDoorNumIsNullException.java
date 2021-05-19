package com.fiberhome.filink.lockserver.exception;

/**
 * <p>
 * 门编号不存在
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/7
 */
public class FilinkDoorNumIsNullException extends RuntimeException {
    public FilinkDoorNumIsNullException() {
        super();
    }

    public FilinkDoorNumIsNullException(String message) {
        super(message);
    }
}
