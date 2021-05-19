package com.fiberhome.filink.onenetserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 *   oneNet平台服务全局异常捕获
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-26
 */
@Slf4j
@RestControllerAdvice
public class OneNetExceptionHandler {

    /**
     * 处理onenetException
     * @param ex oceanException异常
     */
    @ExceptionHandler(FilinkOneNetException.class)
    public void handlerDeviceException(FilinkOneNetException ex) {
        log.error("oneNet exception:", ex);
    }
    /**
     * 捕获设施协议删除异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(OneNetReceiveException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handlerDeviceProtocolDeleteException(OneNetReceiveException ex) {
        log.error("receive error:", ex);
        return ex.getMessage();
    }
}
