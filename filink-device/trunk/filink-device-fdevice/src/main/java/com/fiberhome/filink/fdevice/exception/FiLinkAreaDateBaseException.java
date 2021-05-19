package com.fiberhome.filink.fdevice.exception;

/**
 * <p>
 * FilinkAreaDateBaseException 数据库异常
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class FiLinkAreaDateBaseException extends RuntimeException {
    /**
     * 无参构造
     */
    public FiLinkAreaDateBaseException() {
    }

    /**
     * 有参构造
     */
    public FiLinkAreaDateBaseException(String msg) {
        super(msg);
    }

}
