package com.fiberhome.filink.filinkoceanconnectserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.exception.FiLinkBusinessException;
import com.fiberhome.filink.commonstation.exception.ProtocolException;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 平台异常处理类
 * @author CongcaiYu
 */
@Slf4j
@RestControllerAdvice
public class OceanExceptionHandler {

    /**
     * 处理OceanException
     * @param ex oceanException异常
     */
    @ExceptionHandler(OceanException.class)
    public void handlerDeviceException(OceanException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * 请求异常
     * @param ex 异常信息
     */
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
    public Result handleFiLinkDeviceLogException(ProtocolException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

    /**
     * 响应解析异常
     * @param ex ProtocolException
     */
    @ExceptionHandler(ResponseException.class)
    public Result handleResponseException(ResponseException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }


    /**
     * 业务逻辑处理异常
     * @param ex 异常信息
     */
    @ExceptionHandler(FiLinkBusinessException.class)
    public void handleBusinessException(FiLinkBusinessException ex){
        log.error(ex.getMessage());
        ex.printStackTrace();
    }

}
