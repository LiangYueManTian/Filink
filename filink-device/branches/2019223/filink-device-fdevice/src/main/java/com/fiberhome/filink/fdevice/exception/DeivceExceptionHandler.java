package com.fiberhome.filink.fdevice.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.DeviceI18n;
import com.fiberhome.filink.fdevice.bean.area.AreaI18n;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapI18n;
import com.fiberhome.filink.fdevice.utils.AreaRusultCode;
import com.fiberhome.filink.fdevice.utils.DeviceMapResultCode;
import com.fiberhome.filink.fdevice.utils.DeviceResultCode;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 设施异常处理
 *
 * @author zepenggao@wistronits.com
 * @date 2019/1/16 10:57
 */
@Slf4j
@RestControllerAdvice
public class DeivceExceptionHandler extends GlobalExceptionHandler {

    //==================================
    //===============设施===============
    //==================================

    /**
     * 参数错误
     *
     * @param ex FiLinkDeviceLogException
     */
    @ExceptionHandler(FiLinkDeviceLogException.class)
    @ResponseBody
    public Result handleFiLinkDeviceLogException(FiLinkDeviceLogException ex) {
        log.info(ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
    }

    /**
     * 设施配置异常
     *
     * @param ex FiLinkDeviceConfigException
     */
    @ExceptionHandler(FiLinkDeviceConfigException.class)
    @ResponseBody
    public Result handleFiLinkDeviceConfigException(FiLinkDeviceConfigException ex) {
        log.info(ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.PARAMETER_ERROR));
    }

    /**
     * 捕获方案参数无效异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkDeviceNameSameException.class)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ResponseBody
    public Result handlerDeviceException(FilinkDeviceNameSameException ex) {
        log.info(ex.getMessage());
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(DeviceI18n.DEVICE_NAME_SAME));
    }

    /**
     * 设施不存在异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkDeviceException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceException(FilinkDeviceException ex) {
        log.info(ex.getMessage());
        return ResultUtils.warn(DeviceResultCode.DEVICE_NOT_EXIST, I18nUtils.getString(DeviceI18n.DEVICE_IS_NOT_EXIST));
    }

    //==================================
    //===============区域===============
    //==================================

    /**
     * 捕获方案数据异常
     *
     * @param ex 数据异常
     * @return 返回结果
     */
    @ExceptionHandler(FilinkAreaDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDirtyDataException(FilinkAreaDirtyDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        String msg = ex.getMessage();
        if (StringUtils.isEmpty(msg)) {
            return ResultUtils.warn(AreaRusultCode.DIRTY_DATA, I18nUtils.getString(AreaI18n.DIRTY_DATA));
        }
        return ResultUtils.warn(AreaRusultCode.DIRTY_DATA, msg);
    }

    /**
     * 捕获方案数据库异常
     *
     * @param ex 数据库异常
     * @return 返回结果
     */
    @ExceptionHandler(FilinkAreaDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDateBaseException(FilinkAreaDateBaseException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        String msg = ex.getMessage();
        if (StringUtils.isEmpty(msg)) {
            return ResultUtils.warn(AreaRusultCode.DATE_BASE_ERROR, I18nUtils.getString(AreaI18n.DATEBASE_ERROR));
        }
        return ResultUtils.warn(AreaRusultCode.DATE_BASE_ERROR, ex.getMessage());
    }

    /**
     * 捕获未定义AreaException
     *
     * @param ex 未定义区域异常
     * @return 返回结果
     */

    @ExceptionHandler(FilinkAreaException.class)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @ResponseBody
    public Result handlerAreaException(FilinkAreaException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
    }

    /**
     * 捕获参数格式异常
     *
     * @param ex 格式异常
     * @return 返回结果
     */
    @ExceptionHandler(FilinkAreaDateFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDateFormatException(FilinkAreaDateFormatException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(AreaRusultCode.PARAMETER_FORMAT_IS_INCORRECT, I18nUtils.getString(AreaI18n.PARAMETER_FORMAT_IS_INCORRECT));
    }

    //==================================
    //==========首页设施配置============
    //==================================

    /**
     * 捕获异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceMapParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceMapParamException(FilinkDeviceMapParamException ex) {
        log.info(I18nUtils.getString(DeviceMapI18n.CONFIG_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_PARAM_ERROR,
                I18nUtils.getString(DeviceMapI18n.CONFIG_PARAM_ERROR));
    }

    /**
     * 捕获异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceMapMessageException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceMapMessageException(FilinkDeviceMapMessageException ex) {
        log.info(I18nUtils.getString(DeviceMapI18n.CONFIG_MESSAGE_EMPTY));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_MESSAGE_EMPTY,
                I18nUtils.getString(DeviceMapI18n.CONFIG_MESSAGE_EMPTY));
    }

    /**
     * 捕获异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceMapUpdateException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceMapUpdateException(FilinkDeviceMapUpdateException ex) {
        log.info(I18nUtils.getString(DeviceMapI18n.CONFIG_UPDATE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_UPDATE_ERROR,
                I18nUtils.getString(DeviceMapI18n.CONFIG_UPDATE_ERROR));
    }

}
