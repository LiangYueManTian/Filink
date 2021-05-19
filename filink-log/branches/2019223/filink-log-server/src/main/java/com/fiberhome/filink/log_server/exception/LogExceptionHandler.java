package com.fiberhome.filink.log_server.exception;

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
 * @author hedongwei@wistronits.com
 * description 日志服务异常处理
 * date 10:45 2019/1/23
 */
@Slf4j
@ControllerAdvice
public class LogExceptionHandler extends GlobalExceptionHandler {


    /**
     * 捕获日志服务系统异常
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ExceptionHandler(FilinkLogException.class)
    @ResponseBody
    public Result handlerLogException(FilinkLogException ex) {
        return ResultUtils.warn(ResultCode.FAIL, "日志服务异常");
    }

}
