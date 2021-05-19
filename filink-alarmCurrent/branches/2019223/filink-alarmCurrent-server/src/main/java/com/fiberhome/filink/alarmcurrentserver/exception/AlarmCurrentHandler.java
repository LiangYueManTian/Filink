package com.fiberhome.filink.alarmcurrentserver.exception;

import com.fiberhome.filink.alarmcurrentserver.bean.Alarm18n;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
public class AlarmCurrentHandler extends GlobalExceptionHandler{

    /**
     * 捕获当前告警服务系统异常
     * @param ex 异常信息
     * @return 判断结果
     */
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ExceptionHandler(FilinkAlarmCurrentException.class)
    @ResponseBody
    public Result handlerAlarmCurrentException(FilinkAlarmCurrentException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(Alarm18n.ALARM_SERVICE_SYSTEM));
    }

}
