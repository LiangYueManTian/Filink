package com.fiberhome.filink.alarmcurrentserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>
 * 设施协议捕获异常
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@ControllerAdvice
public class AlarmCurrentHandler extends GlobalExceptionHandler {

    /**
     * 捕获当前告警服务系统异常
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FilinkAlarmCurrentException.class)
    @ResponseBody
    public Result handlerAlarmCurrentException(FilinkAlarmCurrentException ex) {
        log.info("alarm current system exception:{}", ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

}
