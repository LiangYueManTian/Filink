package com.fiberhome.filink.dump.exception;

/**
 * 超过最大限制异常
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/2/27 14:27
 */
public class FilinkExportDataTooLargeException extends RuntimeException {
    public FilinkExportDataTooLargeException(String message) {
        super(message);
    }
}
