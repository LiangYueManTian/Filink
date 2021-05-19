package com.fiberhome.filink.lockserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.util.ControlI18n;
import com.fiberhome.filink.lockserver.util.LockI18n;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 电子锁异常处理
 * @author CongcaiYu
 */
@Slf4j
@RestControllerAdvice
public class FiLinkLockExceptionHandler {


    /**
     * 电子锁异常处理类
     *
     * @param ex FiLinkLockException
     * @return Result
     */
    @ExceptionHandler(FiLinkLockException.class)
    @ResponseBody
    public Result handleLockException(FiLinkLockException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(LockI18n.PARAMETER_ERROR));
    }

    /**
     * 主控异常处理类
     *
     * @param ex FiLinkLockException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlException.class)
    @ResponseBody
    public Result handleControlException(FiLinkControlException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(ControlI18n.PARAMETER_ERROR));
    }
}
