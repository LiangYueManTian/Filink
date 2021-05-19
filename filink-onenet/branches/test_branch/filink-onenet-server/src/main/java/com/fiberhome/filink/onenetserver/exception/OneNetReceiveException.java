package com.fiberhome.filink.onenetserver.exception;

import com.fiberhome.filink.onenetserver.constant.NBStatus;

/**
 * <p>
 *   oneNet平台HTTP请求异常类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class OneNetReceiveException extends RuntimeException {
    public OneNetReceiveException(NBStatus status) {
        super("errorNo:" + status.getErrorNo() + ",error:" + status.getError());
    }
}
