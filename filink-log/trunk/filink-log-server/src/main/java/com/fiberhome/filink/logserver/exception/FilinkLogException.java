package com.fiberhome.filink.logserver.exception;

/**
 * @author hedongwei@wistronits.com
 * description 日志服务异常类
 * date 10:45 2019/1/23
 */
public class FilinkLogException extends RuntimeException {

    public FilinkLogException() {
        super("log server error");
    }
}
