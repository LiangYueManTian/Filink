package com.fiberhome.filink.server_common.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.bean.ResultMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.fiberhome.filink.server_common.bean.ResultMessageEnum.METHOD_ARGUMENT_NOT_VALID;

/**
 * 全局异常捕获
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/19 6:10 PM
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义所有异常捕获
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Result handlerException(Exception ex) {
        // 控制台打开报错
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ResultMessageEnum.UNDEFINED_EXCEPTION.getMsg());
    }

    /**
     *用户未登录异常捕获
     *
     * @param ex 用户未登录异常
     * @return  用户未登录
     */
    @ExceptionHandler(UserNotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Result handlerNotLoginException(UserNotLoginException ex) {
        // 控制台打开报错
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.NOT_LOGIN, ResultMessageEnum.USER_NOT_LOGIN.getMsg());
    }


    /**
     * 捕获方案参数无效异常
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result handlerDemoException(MethodArgumentNotValidException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL,METHOD_ARGUMENT_NOT_VALID.getMsg());
    }
}
