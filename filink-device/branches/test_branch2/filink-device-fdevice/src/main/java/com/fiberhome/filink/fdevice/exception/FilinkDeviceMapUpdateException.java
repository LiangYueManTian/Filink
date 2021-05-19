package com.fiberhome.filink.fdevice.exception;

/**
 * <p>
 *     首页设施地图请求参数异常
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/29
 */
public class FilinkDeviceMapUpdateException extends RuntimeException {

    public FilinkDeviceMapUpdateException() { }

    public FilinkDeviceMapUpdateException(String msg) {
        super(msg);
    }
}
