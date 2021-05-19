package com.fiberhome.filink.lockserver.exception;

/**
 * <p>
 * 设施id为空
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/6
 */
public class FilinkDeviceIsNullException extends RuntimeException {

    public FilinkDeviceIsNullException(String message) {
        super(message);
    }
}
