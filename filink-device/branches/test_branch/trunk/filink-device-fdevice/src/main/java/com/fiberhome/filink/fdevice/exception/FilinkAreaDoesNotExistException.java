package com.fiberhome.filink.fdevice.exception;

/**
 * <p>
 * FilinkAreaDateBaseException 数据库异常
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class FilinkAreaDoesNotExistException extends RuntimeException {
    /**
     * 无参构造
     */
    public FilinkAreaDoesNotExistException() {
    }

    /**
     * 有参构造
     */
    public FilinkAreaDoesNotExistException(String msg) {
        super(msg);
    }

}
