package com.fiberhome.filink.log_server.exception;

/**
 * @author hedongwei@wistronits.com
 * description 日志服务异常类
 * date 10:45 2019/1/23
 */
public class FilinkLogException extends RuntimeException {

    public FilinkLogException() {
        super("日志服务异常");
    }
}
