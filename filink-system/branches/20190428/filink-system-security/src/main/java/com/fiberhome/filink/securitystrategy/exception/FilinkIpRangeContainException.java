package com.fiberhome.filink.securitystrategy.exception;

/**
 * 自定义安全策略异常
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/15
 */
public class FilinkIpRangeContainException extends RuntimeException {
    public FilinkIpRangeContainException(String msg) {
        super(msg);
    }
}
