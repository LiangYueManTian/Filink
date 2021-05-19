package com.fiberhome.filink.alarmcurrentserver.exception;

/**
 * <p>
 * 自定义设施协议异常
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public class FilinkAlarmCurrentException extends RuntimeException {

    /**
     * 异常处理信息
     *
     * @param msg 提示信息
     */
    public FilinkAlarmCurrentException(String msg) {
        super(msg);
    }
}
