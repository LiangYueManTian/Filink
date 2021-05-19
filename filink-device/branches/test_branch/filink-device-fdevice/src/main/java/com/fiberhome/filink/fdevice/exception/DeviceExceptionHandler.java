package com.fiberhome.filink.fdevice.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceCollectingI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceCollectingResultCode;
import com.fiberhome.filink.fdevice.constant.device.DeviceMapI18n;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCodeConstant;
import com.fiberhome.filink.fdevice.constant.device.DeviceMapResultCode;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 设施异常处理
 *
 * @author zepenggao@wistronits.com
 * @date 2019/1/16 10:57
 */
@Slf4j
@RestControllerAdvice
public class DeviceExceptionHandler extends GlobalExceptionHandler {

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
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
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
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
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
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(DeviceI18n.DEVICE_NAME_SAME));
    }

    /**
     * 设施参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FiLinkDeviceException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceException(FiLinkDeviceException ex) {
        log.info(ex.getMessage());
        return ResultUtils.warn(DeviceResultCode.DEVICE_NOT_EXIST, ex.getMessage());
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
    @ExceptionHandler(FiLinkAreaDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDirtyDataException(FiLinkAreaDirtyDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        String msg = ex.getMessage();
        if (StringUtils.isEmpty(msg)) {
            return ResultUtils.warn(AreaResultCodeConstant.DIRTY_DATA, I18nUtils.getSystemString(AreaI18nConstant.DIRTY_DATA));
        }
        return ResultUtils.warn(AreaResultCodeConstant.DIRTY_DATA, msg);
    }

    /**
     * 捕获方案数据库异常
     *
     * @param ex 数据库异常
     * @return 返回结果
     */
    @ExceptionHandler(FiLinkAreaDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDateBaseException(FiLinkAreaDateBaseException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        String msg = ex.getMessage();
        if (StringUtils.isEmpty(msg)) {
            return ResultUtils.warn(AreaResultCodeConstant.DATE_BASE_ERROR, I18nUtils.getSystemString(AreaI18nConstant.DATA_BASE_ERROR));
        }
        return ResultUtils.warn(AreaResultCodeConstant.DATE_BASE_ERROR, ex.getMessage());
    }
    /**
     * 捕获方案区域没有权限异常
     *
     * @param ex 数据库异常
     * @return 返回结果
     */
    @ExceptionHandler(FilinkAreaNoDataPermissionsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaNoDataPermissionsException(FilinkAreaNoDataPermissionsException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(AreaResultCodeConstant.NO_DATA_PERMISSIONS, I18nUtils.getSystemString(AreaI18nConstant.NO_DATA_PERMISSIONS));
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
    @ExceptionHandler(FiLinkAreaDateFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDateFormatException(FiLinkAreaDateFormatException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(AreaResultCodeConstant.PARAMETER_FORMAT_IS_INCORRECT, I18nUtils.getSystemString(AreaI18nConstant.PARAMETER_FORMAT_IS_INCORRECT));
    }

	/**
	 * 捕获区域不存在异常
	 * @param ex 区域不存在异常
	 * @return 返回结果
	 */
	@ExceptionHandler(FilinkAreaDoesNotExistException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Result handlerAreaDoesNotExistException(FilinkAreaDoesNotExistException ex) {
		log.info(ex.getMessage());
		ex.printStackTrace();
		return ResultUtils.warn(AreaResultCodeConstant.THIS_AREA_DOES_NOT_EXIST, I18nUtils.getSystemString(AreaI18nConstant.THIS_AREA_DOES_NOT_EXIST));
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
        log.info(I18nUtils.getSystemString(DeviceMapI18n.CONFIG_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_PARAM_ERROR,
                I18nUtils.getSystemString(DeviceMapI18n.CONFIG_PARAM_ERROR));
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
        log.info(I18nUtils.getSystemString(DeviceMapI18n.CONFIG_MESSAGE_EMPTY));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_MESSAGE_EMPTY,
                I18nUtils.getSystemString(DeviceMapI18n.CONFIG_MESSAGE_EMPTY));
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
        log.info(I18nUtils.getSystemString(DeviceMapI18n.CONFIG_UPDATE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceMapResultCode.CONFIG_UPDATE_ERROR,
                I18nUtils.getSystemString(DeviceMapI18n.CONFIG_UPDATE_ERROR));
    }

    //-------
    //--我的关注
    //--------

    /**
     * 捕获请求参数格式异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkAttentionRequestParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerFilinkRequestParamException(FilinkAttentionRequestParamException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(DeviceCollectingResultCode.ATTENTION_REQUEST_PARAMS_ERROR, I18nUtils.getSystemString(DeviceCollectingI18n.PARAMS_ERROR));
    }

    /**
     * 捕获系统错误异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkAttentionDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerFilinkSystemErrorException(FilinkAttentionDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(DeviceCollectingResultCode.ATTENTION_DATA_ERROR, I18nUtils.getSystemString(DeviceCollectingI18n.DATA_ERROR));
    }

	/**
	 * 重复关注异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(FilinkAttentionRepeatException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Result handlerFilinkSystemErrorException(FilinkAttentionRepeatException ex) {
		log.info(ex.getMessage());
		ex.printStackTrace();
		return ResultUtils.warn(DeviceCollectingResultCode.ATTENTION_REPEAT_ERROR,I18nUtils.getSystemString(DeviceCollectingI18n.REPEAT_ERROR));
	}
}
