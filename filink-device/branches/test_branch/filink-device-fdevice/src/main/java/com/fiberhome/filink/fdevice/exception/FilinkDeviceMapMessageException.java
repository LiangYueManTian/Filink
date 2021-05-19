package com.fiberhome.filink.fdevice.exception;

/**
 * <p>
 *     首页设施地图数据库数据异常
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/29
 */
public class FilinkDeviceMapMessageException extends RuntimeException {

    public FilinkDeviceMapMessageException() { }

    public FilinkDeviceMapMessageException(String msg) {
        super(msg);
    }
}
