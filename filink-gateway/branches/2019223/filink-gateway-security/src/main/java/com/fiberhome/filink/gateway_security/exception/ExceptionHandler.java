package com.fiberhome.filink.gateway_security.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 安全异常捕获
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 14:41
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    /**
     * 自定义所有异常捕获
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(ValidateCodeExceprion.class)
    @ResponseBody
    public Result handlerException(ValidateCodeExceprion ex) {
        // 控制台打开报错
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, "验证码错误");
    }

}
