package com.fiberhome.filink.securitystrategy.exception;

/**
 * 自定义安全策略异常
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/15
 */
public class FilinkIpRangeOverlapException extends RuntimeException {
    public FilinkIpRangeOverlapException(String msg) {
        super(msg);
    }
}
