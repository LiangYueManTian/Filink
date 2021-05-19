package com.fiberhome.filink.dprotocol.exception;

/**
 * 自定义设施协议文件已存在异常
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/15
 */
public class FilinkDeviceProtocolFileExistException extends RuntimeException {
    public FilinkDeviceProtocolFileExistException(String msg) {
        super(msg);
    }
}
