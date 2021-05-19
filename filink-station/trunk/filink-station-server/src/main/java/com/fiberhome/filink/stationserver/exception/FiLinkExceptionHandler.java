package com.fiberhome.filink.stationserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.exception.FiLinkBusinessException;
import com.fiberhome.filink.commonstation.exception.ProtocolException;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * filink异常处理类
 * @author CongcaiYu
 */
@Slf4j
@RestControllerAdvice
public class FiLinkExceptionHandler extends GlobalExceptionHandler {

    /**
     * 请求异常
     * @param ex 异常信息
     */
    @ExceptionHandler(RequestException.class)
    public void handlerDeviceException(RequestException ex) {
        log.error(ex.getMessage());
    }

    /**
     * 协议异常
     * @param ex ProtocolException
     */
    @ExceptionHandler(ProtocolException.class)
    public Result handleFiLinkDeviceLogException(ProtocolException ex) {
        log.error(ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

    /**
     * 响应解析异常
     * @param ex ProtocolException
     */
    @ExceptionHandler(ResponseException.class)
    public Result handleResponseException(ResponseException ex) {
        log.error(ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

    /**
     * udp发送异常
     * @param ex 异常信息
     */
    @ExceptionHandler(UdpSendException.class)
    public void handleUdpSendException(UdpSendException ex){
        log.error(ex.getMessage());
    }

    /**
     * 业务逻辑处理异常
     * @param ex 异常信息
     */
    @ExceptionHandler(FiLinkBusinessException.class)
    public void handleBusinessException(FiLinkBusinessException ex){
        log.error(ex.getMessage());
    }
}
