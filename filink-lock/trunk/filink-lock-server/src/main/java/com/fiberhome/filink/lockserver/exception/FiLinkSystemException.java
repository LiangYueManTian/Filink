package com.fiberhome.filink.lockserver.exception;

/**
 * <p>
 * 系统异常 比如：feign调用失败
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/18
 */
public class FiLinkSystemException extends RuntimeException {
    public FiLinkSystemException(String message) {
        super(message);
    }
}
