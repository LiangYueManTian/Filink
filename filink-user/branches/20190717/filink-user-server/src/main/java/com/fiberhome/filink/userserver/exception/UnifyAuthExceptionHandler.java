package com.fiberhome.filink.userserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.consts.UserI18n;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 用户服务异常处理
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/29 11:02
 */
@Slf4j
@ControllerAdvice
public class UnifyAuthExceptionHandler extends GlobalExceptionHandler {


    /**
     * 捕获用户服务系统异常
     *
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ExceptionHandler(FilinkUnifyAuthException.class)
    @ResponseBody
    public Result handlerUserException(FilinkUnifyAuthException ex) {
        log.error("Bad things:", ex);
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DATABASE_OPER_ERROR));
    }

}
