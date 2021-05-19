package com.fiberhome.filink.fdevice.exception;

/**
 * <p>
 * FilinkAreaDirtyDataException 数据异常
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class FiLinkAreaDirtyDataException extends RuntimeException {

    public FiLinkAreaDirtyDataException() {
    }
    /**
     * 有参构造
     */
    public FiLinkAreaDirtyDataException(String msg) {
        super(msg);
    }
}
