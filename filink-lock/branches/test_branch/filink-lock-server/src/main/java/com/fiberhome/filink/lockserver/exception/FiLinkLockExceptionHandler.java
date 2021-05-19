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
        log.error("Bad things:", ex);
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(LockI18n.PARAMETER_ERROR));
    }

    /**
     * 设施id为空异常处理类
     *
     * @param ex FiLinkDeviceIsNullException
     * @return Result
     */
    @ExceptionHandler(FiLinkDeviceIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleDeviceIsNullException(FiLinkDeviceIsNullException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.DEVICE_ID_IS_NULL, I18nUtils.getSystemString(ControlI18n.DEVICE_ID_IS_NULL));
    }

    /**
     * 主控信息为空异常处理类
     *
     * @param ex FiLinkControlIsNullException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleControlIsNullException(FiLinkControlIsNullException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.CONTROL_IS_NULL, I18nUtils.getSystemString(ControlI18n.CONTROL_IS_NULL));
    }

    /**
     * 设施配置参数为空异常处理类
     *
     * @param ex FiLinkConfigValueIsNullException
     * @return Result
     */
    @ExceptionHandler(FiLinkConfigValueIsNullException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleConfigValueIsNullException(FiLinkConfigValueIsNullException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.CONFIG_VALUE_IS_NULL, I18nUtils.getSystemString(ControlI18n.CONFIG_VALUE_IS_NULL));
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
        log.error("Bad things:", ex);
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(ControlI18n.PARAMETER_ERROR));
    }

    /**
     * 二维码重复使用异常处理类
     *
     * @param ex FiLinkQrCodeReusedException
     * @return Result
     */
    @ExceptionHandler(FiLinkQrCodeReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleQrCodeReusedException(FiLinkQrCodeReusedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.QR_CODE_REUSED, I18nUtils.getSystemString(LockI18n.QR_CODE_REUSED));
    }

    /**
     * 锁芯id重复使用异常处理类
     *
     * @param ex FiLinkLockCodeReusedException
     * @return Result
     */
    @ExceptionHandler(FiLinkLockCodeReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleLockCodeReusedException(FiLinkLockCodeReusedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.LOCK_CODE_REUSED, I18nUtils.getSystemString(LockI18n.LOCK_CODE_REUSED));
    }

    /**
     * 二维码错误异常处理类
     *
     * @param ex FiLinkQrCodeErrorException
     * @return Result
     */
    @ExceptionHandler(FiLinkQrCodeErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleQrCodeErrorException(FiLinkQrCodeErrorException ex) {
        log.error("Bad things:", ex);

        return ResultUtils.warn(LockResultCode.QR_CODE_ERROR, I18nUtils.getSystemString(LockI18n.QR_CODE_ERROR));
    }

    /**
     * 系统异常处理类
     *
     * @param ex FiLinkSystemException
     * @return Result
     */
    @ExceptionHandler(FiLinkSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkSystemException(FiLinkSystemException ex) {
        log.error("Bad things:", ex);

        return ResultUtils.warn(LockResultCode.SYSTEM_ERROR, I18nUtils.getSystemString(LockI18n.SYSTEM_ERROR));
    }

    /**
     * 主控id重复使用处理类
     *
     * @param ex FiLinkControlIdReusedException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlIdReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkControlIdReusedException(FiLinkControlIdReusedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.CONTROL_ID_REUSED, I18nUtils.getSystemString(ControlI18n.CONTROL_ID_REUSED));
    }

    /**
     * 开锁没有权限
     *
     * @param ex FiLinkOpenLockException
     * @return Result
     */
    @ExceptionHandler(FiLinkOpenLockException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkControlIdReusedException(FiLinkOpenLockException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.OPEN_LOCK_REFUSED, I18nUtils.getSystemString(LockI18n.OPEN_LOCK_REFUSED));
    }

    /**
     * 没有电子锁数据权限
     *
     * @param ex FiLinkAccessDenyException
     * @return Result
     */
    @ExceptionHandler(FiLinkAccessDenyException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkDenyException(FiLinkAccessDenyException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.ACCESS_DENY, I18nUtils.getSystemString(LockI18n.ACCESS_DENY));
    }

    /**
     * 一个设施的门编号重复
     *
     * @param ex FiLinkDeviceDoorNumReusedException
     * @return Result
     */
    @ExceptionHandler(FiLinkDeviceDoorNumReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkDeviceDoorNumReusedException(FiLinkDeviceDoorNumReusedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NUM_REUSED, I18nUtils.getSystemString(LockI18n.DEVICE_DOOR_NUM_REUSED));
    }

    /**
     * 一个设施的主控名重复
     *
     * @param ex FiLinkControlNameResuedException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlNameResuedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkControlNameResuedException(FiLinkControlNameResuedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.CONTROL_NAME_REUSED, I18nUtils.getSystemString(ControlI18n.CONTROL_NAME_REUSED));
    }

    /**
     * 设施的主控数量已到最大值
     *
     * @param ex FiLinkControlMaxNumException
     * @return Result
     */
    @ExceptionHandler(FiLinkControlMaxNumException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkControlMaxNumException(FiLinkControlMaxNumException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.CONTROL_MAX_NUM, I18nUtils.getSystemString(ControlI18n.CONTROL_MAX_NUM));
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
    public Result handleFiLinkDoorNumIsNullException(FilinkDoorNumIsNullException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NUM_ERROR, I18nUtils.getSystemString(LockI18n.DEVICE_DOOR_NUM_ERROR));
    }

    /**
     * 门磁映射不对应
     *
     * @param ex FiLinkSensorListException
     * @return Result
     */
    @ExceptionHandler(FiLinkSensorListException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkSensorListException(FiLinkSensorListException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.SENSOR_LIST_ERROR, I18nUtils.getSystemString(LockI18n.SENSOR_LIST_ERROR));
    }

    /**
     * 门磁映射达到上限
     *
     * @param ex FiLinkDoorMaxNumException
     * @return Result
     */
    @ExceptionHandler(FiLinkDoorMaxNumException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkDoorMaxNumException(FiLinkDoorMaxNumException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.DOOR_MAX_NUM, I18nUtils.getSystemString(LockI18n.DOOR_MAX_NUM));
    }

    /**
     * 同一设施下门名称重复
     *
     * @param ex FiLinkDeviceDoorNameReusedException
     * @return Result
     */
    @ExceptionHandler(FiLinkDeviceDoorNameReusedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkDeviceDoorNameResuedException(FiLinkDeviceDoorNameReusedException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(LockResultCode.DEVICE_DOOR_NAME_REUSED, I18nUtils.getSystemString(LockI18n.DEVICE_DOOR_NAME_REUSED));
    }

    /**
     * 所选设施不支持该操作（布防、撤防等）
     *
     * @param ex FiLinkDeviceOperateException
     * @return Result
     */
    @ExceptionHandler(FiLinkDeviceOperateException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleFiLinkDeviceOperateException(FiLinkDeviceOperateException ex) {
        log.error("Bad things:", ex);
        return ResultUtils.warn(ControlResultCode.DEVICE_HAS_NO_CONTROL_INFO,
                I18nUtils.getSystemString(ControlI18n.DEVICE_HAS_NO_CONTROL_INFO));
    }

}
