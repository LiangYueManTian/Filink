package com.fiberhome.filink.rfid.exception;


/**
 * 设施id为空异常
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-07-08
 */
public class FilinkDeviceIdException extends RuntimeException {

    public FilinkDeviceIdException() {
    }

    public FilinkDeviceIdException(String msg) {
        super(msg);
    }
}
