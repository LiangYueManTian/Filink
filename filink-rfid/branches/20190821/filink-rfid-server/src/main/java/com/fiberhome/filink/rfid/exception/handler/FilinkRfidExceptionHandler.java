package com.fiberhome.filink.rfid.exception.handler;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.exception.*;
import com.fiberhome.filink.rfid.utils.ResultI18Utils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

/**
 * 异常捕获
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-14
 */
@Slf4j
@RestControllerAdvice
public class FilinkRfidExceptionHandler extends GlobalExceptionHandler {

    /**
     * 获取用户信息异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkObtainUserInfoException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerCommitProcInspectionException(FilinkObtainUserInfoException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(RfIdResultCode.USER_SERVER_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.USER_SERVER_ERROR));
    }

    /**
     * 用户权限信息异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkUserPermissionException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerUserPermissionException(FilinkUserPermissionException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.success(new ArrayList<>());
    }

    /**
     * 获取端口信息异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkPortInfoException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerPortInfoException(FilinkPortInfoException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(RfIdResultCode.PORT_INFO_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.PORT_INFO_NOT_EXIST));
    }

    /**
     * rfid 异常信息
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkRfIdException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerRfIdInfoException(FilinkRfIdException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultI18Utils.convertWarnResult(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
    }


    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerBizException(BizException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ex.getCode(), I18nUtils.getSystemString(ex.getMsg()));
    }

    /**
     * 设施id为空异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkDeviceIdException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceIdException(FilinkDeviceIdException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(RfIdResultCode.QUERY_REALPOSITION_DEVICE_ID_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR));
    }

    /**
     * 跳接信息为空异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkJumpInfoException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerJumpInfoException(FilinkJumpInfoException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(RfIdResultCode.JUMP_CORE_NOT_EXIST, I18nUtils.getSystemString(RfIdI18nConstant.JUMP_CORE_NOT_EXIST));
    }

}
