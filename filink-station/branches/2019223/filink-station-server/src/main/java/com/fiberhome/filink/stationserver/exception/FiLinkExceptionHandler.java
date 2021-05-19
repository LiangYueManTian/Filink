package com.fiberhome.filink.stationserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * filink异常处理类
 * @author CongcaiYu
 */
@Slf4j
@RestControllerAdvice
public class FiLinkExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(RequestException.class)
    public void handlerDeviceException(RequestException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * 协议异常
     * @param ex ProtocolException
     */
    @ExceptionHandler(ProtocolException.class)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ResponseBody
    public Result handleFiLinkDeviceLogException(ProtocolException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

    /**
     * 响应解析异常
     * @param ex ProtocolException
     */
    @ExceptionHandler(ResponseException.class)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ResponseBody
    public Result handleResponseException(ResponseException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }
}
