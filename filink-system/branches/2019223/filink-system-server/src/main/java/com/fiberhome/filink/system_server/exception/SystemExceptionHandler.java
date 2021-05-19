package com.fiberhome.filink.system_server.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.utils.DeviceProtocolResultCode;
import com.fiberhome.filink.menu.bean.MenuI18n;
import com.fiberhome.filink.menu.exception.FilinkMenuDateBaseException;
import com.fiberhome.filink.menu.exception.FilinkMenuDateFormatException;
import com.fiberhome.filink.menu.exception.FilinkMenuDirtyDataException;
import com.fiberhome.filink.menu.utils.MenuRusultCode;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 系统协议异常捕获
 *
 * @author chaofang@wistronits.com
 * @since \2019/2/18
 */
@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler extends GlobalExceptionHandler {
    //=========================
    //=======设施协议==========
    //=========================
    /**
     * 捕获设施协议新增异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolAddException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolAddException(FilinkDeviceProtocolAddException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_FAIL));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_ADD_FAIL,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_FAIL));
    }
    /**
     * 捕获设施协议删除异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolDeleteException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolDeleteException(FilinkDeviceProtocolDeleteException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_FAIL));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_DELETE_FAIL,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_FAIL));
    }
    /**
     * 捕获设施协议文件内容异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileContentException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileContentException(FilinkDeviceProtocolFileContentException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_CONTENT_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_CONTENT_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_CONTENT_ERROR));
    }
    /**
     * 捕获设施协议文件上传异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileUploadException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileUploadException(FilinkDeviceProtocolFileUploadException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_UPLOAD_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_UPLOAD_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_UPLOAD_ERROR));
    }
    /**
     * 捕获设施协议文件名称异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileNameException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileNameException(FilinkDeviceProtocolFileNameException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_NAME_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME_ERROR));
    }
    /**
     * 捕获设施协议文件大小异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileSizeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileNameException(FilinkDeviceProtocolFileSizeException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_SIZE_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE_ERROR));
    }
    /**
     * 捕获设施协议文件已存在异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileExistException(FilinkDeviceProtocolFileExistException ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_EXIST,
                ex.getMessage());
    }
    /**
     * 捕获设施协议文件格式异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolFileFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileFormatException(FilinkDeviceProtocolFileFormatException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_READ_FAIL));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_READ_FAIL,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_READ_FAIL));
    }
    /**
     * 捕获设施协议名称已存在异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolNameExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolNameExistException(FilinkDeviceProtocolNameExistException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NAME_EXIST));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_NAME_EXIST,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NAME_EXIST));
    }
    /**
     * 捕获设施协议已删除异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolNotExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolNotExistException(FilinkDeviceProtocolNotExistException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NOT_EXIST));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_NOT_EXIST,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NOT_EXIST));
    }
    /**
     * 捕获设施协议更新异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolUpdateException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolUpdateException(FilinkDeviceProtocolUpdateException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_FAIL));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_UPDATE_FAIL,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_FAIL));
    }

    /**
     * 捕获设施协议传入参数异常
     *
     * @param ex 异常
     * @return 结果
     */

    @ExceptionHandler(FilinkDeviceProtocolParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolParamException(FilinkDeviceProtocolParamException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
    }

    /**
     * 捕获设施协议名称异常
     *
     * @param ex 异常
     * @return 结果
     */

    @ExceptionHandler(FilinkDeviceProtocolNameException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolNameException(FilinkDeviceProtocolNameException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NAME_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_NAME_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_NAME_ERROR));
    }

    //=========================
    //=======菜单管理==========
    //=========================
    /**
     * 捕获方案数据异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDirtyDataException(FilinkMenuDirtyDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, I18nUtils.getString(MenuI18n.DIRTY_DATA));
    }
    /**
     * 捕获方案数据异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerMenuDateBaseException(FilinkMenuDateBaseException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(MenuRusultCode.DATE_BASE_ERROR, I18nUtils.getString(MenuI18n.DATE_BASE_ERROR));
    }
    /**
     * 捕获参数格式异常
     *
     * @param ex 参数格式异常
     * @return 返回结果
     */
    @ExceptionHandler(FilinkMenuDateFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerMenuDateFormatException(FilinkMenuDateFormatException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(MenuRusultCode.PARAMETER_FORMAT_IS_INCORRECT,I18nUtils.getString(MenuI18n.PARAMETER_FORMAT_IS_INCORRECT));
    }
}
