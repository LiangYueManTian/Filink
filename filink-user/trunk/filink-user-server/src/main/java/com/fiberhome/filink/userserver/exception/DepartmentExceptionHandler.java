package com.fiberhome.filink.userserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.constant.UserI18n;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * DepartmentException处理类
 *
 * @author xuangong
 * @since 2019-01-23
 */
@Slf4j
@RestControllerAdvice
public class DepartmentExceptionHandler extends GlobalExceptionHandler {

    /**
     * 捕获方案数据异常
     *
     * @param ex 异常信息
     * @return 返回异常结果
     */
    @ExceptionHandler(FilinkDepartmentException.class)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ResponseBody
    public Result handlerAreaDirtyDataException(FilinkDepartmentException ex) {
        log.error("Bad things:", ex);
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DATABASE_OPER_ERROR));
    }
}
