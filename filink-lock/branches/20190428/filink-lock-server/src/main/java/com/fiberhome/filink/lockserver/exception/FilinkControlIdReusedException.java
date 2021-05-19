package com.fiberhome.filink.lockserver.exception;

/**
 * <p>
 * 主控id已存在异常
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/18
 */
public class FilinkControlIdReusedException extends RuntimeException {
    public FilinkControlIdReusedException(String msg) {
        super(msg);
    }
}
