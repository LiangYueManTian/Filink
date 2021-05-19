package com.fiberhome.filink.protocol.exception;

/**
 * <p>
 * 系统异常
 * 包含数据库异常：脏数据
 * 调用其他微服务异常
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/21
 */
public class ProtocolSystemException extends RuntimeException {
    /**
     * 无参构造函数
     */
    public ProtocolSystemException() {
    }
}
