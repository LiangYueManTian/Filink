package com.fiberhome.filink.oss.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.oss.bean.FdfsI18n;
import com.fiberhome.filink.oss.utils.FdfsResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <P>
 *     文件服务器全局捕获
 * </P>
 * @author chaofang@wistronits.com
 * @since \2019/1/17
 */
@Slf4j
@RestControllerAdvice
public class FdfsExceptionHandler {

    /**
     * 捕获文件服务器请求参数异常
     *
     * @param ex 异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FilinkFdfsParamException.class)
    @ResponseBody
    public Result handlerFdfsParamException(FilinkFdfsParamException ex) {
        log.error(I18nUtils.getString(FdfsI18n.OSS_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(FdfsResultCode.OSS_PARAM_ERROR,
                I18nUtils.getString(FdfsI18n.OSS_PARAM_ERROR));
    }

}
