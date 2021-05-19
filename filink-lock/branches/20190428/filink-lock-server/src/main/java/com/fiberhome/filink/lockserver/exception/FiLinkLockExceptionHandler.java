package com.fiberhome.filink.lockserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.constant.control.ControlI18n;
import com.fiberhome.filink.lockserver.constant.control.ControlResultCode;
import com.fiberhome.filink.lockserver.constant.lock.LockI18n;
import com.fiberhome.filink.lockserver.constant.lock.LockResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 电子锁异常处理
 *
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
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleLockException(FiLinkLockException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(LockI18n.PARAMETER_ERROR));
    }

    /**
     * 设施id为空异常处理类
     *
     * @param ex FilinkDeviceIsNullException
     * @return Result
     */
    @ExceptionHandler(FilinkDeviceIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleDeviceIsNullException(FilinkDeviceIsNullException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.DEVICE_ID_IS_NULL, I18nUtils.getString(ControlI18n.DEVICE_ID_IS_NULL));
    }

    /**
     * 主控信息为空异常处理类
     *
     * @param ex FilinkControlIsNullException
     * @return Result
     */
    @ExceptionHandler(FilinkControlIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleControlIsNullException(FilinkControlIsNullException ex) {
        log.error(I18nUtils.getString(ControlI18n.CONTROL_IS_NULL));
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.CONTROL_IS_NULL, I18nUtils.getString(ControlI18n.CONTROL_IS_NULL));
    }

    /**
     * 设施配置参数为空异常处理类
     *
     * @param ex FilinkConfigValueIsNullException
     * @return Result
     */
    @ExceptionHandler(FilinkConfigValueIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleConfigValueIsNullException(FilinkConfigValueIsNullException ex) {
        log.error(I18nUtils.getString(ControlI18n.CONFIG_VALUE_IS_NULL));
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.CONFIG_VALUE_IS_NULL, I18nUtils.getString(ControlI18n.CONFIG_VALUE_IS_NULL));
    }

    /**
     * 主控异常处理类
     *
     * @param ex FiLinkLockException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleControlException(FiLinkControlException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(ControlI18n.PARAMETER_ERROR));
    }

    /**
     * 二维码重复使用异常处理类
     *
     * @param ex FilinkQrcodeReusedException
     * @return Result
     */
    @ExceptionHandler(FilinkQrcodeReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleQrcodeReusedException(FilinkQrcodeReusedException ex) {
        log.error(I18nUtils.getString(LockI18n.QR_CODE_REUSED));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.QR_CODE_REUSED, I18nUtils.getString(LockI18n.QR_CODE_REUSED));
    }

    /**
     * 锁芯id重复使用异常处理类
     *
     * @param ex FilinkLockCodeReusedException
     * @return Result
     */
    @ExceptionHandler(FilinkLockCodeReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleLockCodeReusedException(FilinkLockCodeReusedException ex) {
        log.error(I18nUtils.getString(LockI18n.LOCK_CODE_REUSED));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.LOCK_CODE_REUSED, I18nUtils.getString(LockI18n.LOCK_CODE_REUSED));
    }

    /**
     * 二维码错误异常处理类
     *
     * @param ex FilinkQrcodeErrorException
     * @return Result
     */
    @ExceptionHandler(FilinkQrcodeErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleQrcodeErrorException(FilinkQrcodeErrorException ex) {
        log.error(I18nUtils.getString(LockI18n.QR_CODE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.QR_CODE_ERROR, I18nUtils.getString(LockI18n.QR_CODE_ERROR));
    }

    /**
     * 系统异常处理类
     *
     * @param ex FilinkSystemException
     * @return Result
     */
    @ExceptionHandler(FilinkSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkSystemException(FilinkSystemException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.SYSTEM_ERROR, I18nUtils.getString(LockI18n.SYSTEM_ERROR));
    }

    /**
     * 主控id重复使用处理类
     *
     * @param ex FilinkControlIdReusedException
     * @return Result
     */
    @ExceptionHandler(FilinkControlIdReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkControlIdReusedException(FilinkControlIdReusedException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.CONTROL_ID_REUSED, I18nUtils.getString(ControlI18n.CONTROL_ID_REUSED));
    }

    /**
     * 开锁没有权限
     *
     * @param ex FilinkOpenLockException
     * @return Result
     */
    @ExceptionHandler(FilinkOpenLockException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkControlIdReusedException(FilinkOpenLockException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.OPEN_LOCK_REFUSED, I18nUtils.getString(LockI18n.OPEN_LOCK_REFUSED));
    }

    /**
     * 没有电子锁数据权限
     *
     * @param ex FilinkAccessDenyException
     * @return Result
     */
    @ExceptionHandler(FilinkAccessDenyException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDenyException(FilinkAccessDenyException ex) {
        log.error(I18nUtils.getString(LockI18n.ACCESS_DENY));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.ACCESS_DENY, I18nUtils.getString(LockI18n.ACCESS_DENY));
    }

    /**
     * 一个设施的门编号重复
     *
     * @param ex FilinkDeviceDoorNumReusedException
     * @return Result
     */
    @ExceptionHandler(FilinkDeviceDoorNumReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDeviceDoorNumReusedException(FilinkDeviceDoorNumReusedException ex) {
        log.error(I18nUtils.getString(LockI18n.DEVICE_DOOR_NUM_REUSED));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NUM_REUSED, I18nUtils.getString(LockI18n.DEVICE_DOOR_NUM_REUSED));
    }

    /**
     * 一个设施的主控名重复
     *
     * @param ex FilinkControlNameResuedException
     * @return Result
     */
    @ExceptionHandler(FilinkControlNameResuedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkControlNameResuedException(FilinkControlNameResuedException ex) {
        log.error(I18nUtils.getString(ControlI18n.CONTROL_NAME_REUSED));
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.CONTROL_NAME_REUSED, I18nUtils.getString(ControlI18n.CONTROL_NAME_REUSED));
    }

    /**
     * 设施的主控数量已到最大值
     *
     * @param ex FilinkControlMaxNumException
     * @return Result
     */
    @ExceptionHandler(FilinkControlMaxNumException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkControlMaxNumException(FilinkControlMaxNumException ex) {
        log.error(I18nUtils.getString(ControlI18n.CONTROL_MAX_NUM));
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.CONTROL_MAX_NUM, I18nUtils.getString(ControlI18n.CONTROL_MAX_NUM));
    }

    /**
     * 门编号不存在
     *
     * @param ex FilinkDoorNumIsNullException
     * @return Result
     */
    @ExceptionHandler(FilinkDoorNumIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDoorNumIsNullException(FilinkDoorNumIsNullException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NUM_ERROR, I18nUtils.getString(LockI18n.DEVICE_DOOR_NUM_ERROR));
    }

    /**
     * 门磁映射不对应
     *
     * @param ex FilinkSensorListException
     * @return Result
     */
    @ExceptionHandler(FilinkSensorListException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkSensorListException(FilinkSensorListException ex) {
        log.error(LockI18n.SENSOR_LIST_ERROR);
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.SENSOR_LIST_ERROR, I18nUtils.getString(LockI18n.SENSOR_LIST_ERROR));
    }

    /**
     * 门磁映射达到上限
     *
     * @param ex FilinkDoorMaxNumException
     * @return Result
     */
    @ExceptionHandler(FilinkDoorMaxNumException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDoorMaxNumException(FilinkDoorMaxNumException ex) {
        log.error(I18nUtils.getString(LockI18n.DOOR_MAX_NUM));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.DOOR_MAX_NUM, I18nUtils.getString(LockI18n.DOOR_MAX_NUM));
    }

    /**
     * 同一设施下门名称重复
     *
     * @param ex FilinkDeviceDoorNameResuedException
     * @return Result
     */
    @ExceptionHandler(FilinkDeviceDoorNameResuedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDeviceDoorNameResuedException(FilinkDeviceDoorNameResuedException ex) {
        log.error(I18nUtils.getString(LockI18n.DEVICE_DOOR_NAME_REUSED));
        ex.printStackTrace();
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NAME_REUSED, I18nUtils.getString(LockI18n.DEVICE_DOOR_NAME_REUSED));
    }

    /**
     * 所选设施不支持该操作（布防、撤防等）
     *
     * @param ex FilinkDeviceOperateException
     * @return Result
     */
    @ExceptionHandler(FilinkDeviceOperateException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFilinkDeviceOperateException(FilinkDeviceOperateException ex) {
        log.error(I18nUtils.getString(ControlI18n.DEVICE_HAS_NO_CONTROL_INFO));
        ex.printStackTrace();
        return ResultUtils.warn(ControlResultCode.DEVICE_HAS_NO_CONTROL_INFO, I18nUtils.getString(ControlI18n.DEVICE_HAS_NO_CONTROL_INFO));
    }
}
