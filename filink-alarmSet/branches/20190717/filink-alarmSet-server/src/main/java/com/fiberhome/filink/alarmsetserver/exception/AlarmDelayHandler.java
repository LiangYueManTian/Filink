package com.fiberhome.filink.alarmsetserver.exception;

import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
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
public class AlarmDelayHandler extends GlobalExceptionHandler {

    /**
     * 捕获当前告警服务系统异常
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ExceptionHandler(FilinkAlarmDelayException.class)
    @ResponseBody
    public Result handlerAlarmCurrentException(FilinkAlarmDelayException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_SETTING_ABNORMAL));
    }

}
