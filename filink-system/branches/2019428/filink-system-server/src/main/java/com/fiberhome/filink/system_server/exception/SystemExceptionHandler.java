package com.fiberhome.filink.system_server.exception;

import com.fiberhome.filink.about.bean.AboutI18n;
import com.fiberhome.filink.about.bean.AboutResultCode;
import com.fiberhome.filink.about.exception.AboutParamsException;
import com.fiberhome.filink.about.exception.AboutSystemException;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.utils.DeviceProtocolResultCode;
import com.fiberhome.filink.export.bean.ExportI18n;
import com.fiberhome.filink.export.exception.FilinkExportDateBaseException;
import com.fiberhome.filink.export.exception.FilinkExportDirtyDataException;
import com.fiberhome.filink.menu.bean.MenuI18n;
import com.fiberhome.filink.menu.exception.FilinkMenuDateBaseException;
import com.fiberhome.filink.menu.exception.FilinkMenuDateFormatException;
import com.fiberhome.filink.menu.exception.FilinkMenuDirtyDataException;
import com.fiberhome.filink.menu.utils.MenuRusultCode;
import com.fiberhome.filink.parameter.bean.SystemParameterI18n;
import com.fiberhome.filink.parameter.bean.SystemParameterResultCode;
import com.fiberhome.filink.parameter.exception.*;
import com.fiberhome.filink.protocol.bean.ProtocolI18n;
import com.fiberhome.filink.protocol.bean.ProtocolResultCode;
import com.fiberhome.filink.protocol.exception.ProtocolParamsErrorException;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;
import com.fiberhome.filink.securitystrategy.bean.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.exception.*;
import com.fiberhome.filink.securitystrategy.utils.SecurityStrategyResultCode;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.system_commons.exception.FilinkSysParamDataException;
import com.fiberhome.filink.system_commons.bean.SysParamI18n;
import com.fiberhome.filink.system_commons.bean.SysParamResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
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
     * 捕获设施协议删除异常
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
     * 捕获设施协议文件内容异常
     *
     * @param ex 异常
     * @return 结果
     */
    @ExceptionHandler(FilinkDeviceProtocolVersionException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileContentException(FilinkDeviceProtocolVersionException ex) {
        log.error(I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_VERSION_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_VERSION_ERROR,
                I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_VERSION_ERROR));
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
        return ResultUtils.warn(MenuRusultCode.DATE_BASE_ERROR, I18nUtils.getString(MenuI18n.DATABASE_ERROR));
    }

    //=========================
    //=======通信协议==========
    //=========================

    /**
     * 通信协议系统异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ProtocolSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerProtocolSystemException(ProtocolSystemException ex) {
        log.error(I18nUtils.getString(ProtocolI18n.PROTOCOL_SYSTEM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(ProtocolResultCode.PROTOCOL_SYSTEM_ERROR, I18nUtils.getString(ProtocolI18n.PROTOCOL_SYSTEM_ERROR));
    }


    /**
     * 通信协议参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ProtocolParamsErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerProtocolParamsEmptyException(ProtocolParamsErrorException ex) {
        log.error(I18nUtils.getString(ProtocolI18n.PROTOCOL_PRAMS_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(ProtocolResultCode.PROTOCOL_PRAMS_ERROR, I18nUtils.getString(ProtocolI18n.PROTOCOL_PRAMS_ERROR));
    }

    /**
     * 捕获参数格式异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDateFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerMenuDateFormatException(FilinkMenuDateFormatException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(MenuRusultCode.PARAMETER_FORMAT_IS_INCORRECT, I18nUtils.getString(MenuI18n.PARAMETER_FORMAT_IS_INCORRECT));
    }

    //=========================
    //=======安全策略==========
    //=========================
    /**
     * 捕获安全策略参数异常
     *
     * @param ex 安全策略参数异常
     * @return Result
     */
    @ExceptionHandler(FilinkSecurityStrategyParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSecurityStrategyParamException(FilinkSecurityStrategyParamException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
    }

    /**
     * 捕获安全策略数据库操作异常
     *
     * @param ex 安全策略数据库操作异常
     * @return Result
     */
    @ExceptionHandler(FilinkSecurityStrategyDatabaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSecurityStrategyDatabaseException(FilinkSecurityStrategyDatabaseException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_DATABASE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_DATABASE_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_DATABASE_ERROR));
    }

    /**
     * 捕获安全策略数据异常
     *
     * @param ex 安全策略IP地址范围数据异常
     * @return Result
     */
    @ExceptionHandler(FilinkSecurityStrategyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSecurityStrategyDataException(FilinkSecurityStrategyDataException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_DATA_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_DATA_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_DATA_ERROR));
    }

    /**
     * 捕获安全策略IP地址范围格式异常
     *
     * @param ex 安全策略IP地址范围格式异常
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeFormatException(FilinkIpRangeFormatException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_FORMAT_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_FORMAT_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_FORMAT_ERROR));
    }
    /**
     * 捕获安全策略IP地址范围已删除
     *
     * @param ex 安全策略IP地址范围已删除
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotExistException(FilinkIpRangeNotExistException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_EXIST_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_EXIST_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_EXIST_ERROR));
    }
    /**
     * 捕获安全策略IP地址范围不是有效范围异常
     *
     * @param ex 安全策略IP地址范围不是有效范围异常
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotRangeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotRangeException(FilinkIpRangeNotRangeException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_RANGE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_RANGE_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_RANGE_ERROR));
    }
    /**
     * 捕获安全策略IP地址全零段异常
     *
     * @param ex 安全策略IP地址全零段异常
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotZeroException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotZeroException(FilinkIpRangeNotZeroException ex) {
        log.info(I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_ZERO_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_ZERO_ERROR,
                I18nUtils.getString(SecurityStrategyI18n.IP_RANGE_NOT_ZERO_ERROR));
    }
    /**
     * 捕获安全策略IP地址范围重叠异常
     *
     * @param ex 安全策略IP地址范围重叠异常
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeOverlapException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeOverlapException(FilinkIpRangeOverlapException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_OVERLAP_ERROR,
                ex.getMessage());
    }
    /**
     * 捕获安全策略IP地址范围包含异常
     *
     * @param ex 安全策略IP地址范围包含异常
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeContainException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeContainException(FilinkIpRangeContainException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_CONTAIN_ERROR,
                ex.getMessage());
    }


    //=========================
    //=======关于==========
    //=========================

    /**
     * 关于系统异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AboutSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAboutSystemException(AboutSystemException ex) {
        log.error(I18nUtils.getString(AboutI18n.ABOUT_SYSTEM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(AboutResultCode.ABOUT_SYSTEM_ERROR, I18nUtils.getString(AboutI18n.ABOUT_SYSTEM_ERROR));
    }

    /**
     * 关于参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AboutParamsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAboutParamsException(AboutParamsException ex) {
        log.error(I18nUtils.getString(AboutI18n.ABOUT_PARAMS_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(AboutResultCode.ABOUT_PARAMS_ERROR, I18nUtils.getString(AboutI18n.ABOUT_PARAMS_ERROR));
    }
    //=========================
    //=======列表导出==========
    //=========================
    /**
     * 捕获方案数据异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkExportDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerExportDirtyDataException(FilinkExportDirtyDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        String msg=ex.getMessage();
        if(StringUtils.isEmpty(msg)){
            return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, I18nUtils.getString(ExportI18n.DIRTY_DATA));
        }
        return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, msg);
    }
    /**
     * 捕获方案数据异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkExportDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerExportDateBaseException(FilinkExportDateBaseException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(MenuRusultCode.DATE_BASE_ERROR, I18nUtils.getString(ExportI18n.DATA_BASE_ERROR));
    }

    //=========================
    //=======系统服务统一参数commons==========
    //=========================
    /**
     * 捕获系统服务统一参数数据库数据异常
     *
     * @param ex 系统服务统一参数数据库数据异常
     * @return Result
     */
    @ExceptionHandler(FilinkSysParamDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSysParamDataException(FilinkSysParamDataException ex) {
        log.info(I18nUtils.getString(SysParamI18n.SYSTEM_PARAM_DATA_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SysParamResultCode.SYSTEM_PARAM_DATA_ERROR,
                I18nUtils.getString(SysParamI18n.SYSTEM_PARAM_DATA_ERROR));
    }
    //=========================
    //=======系统参数==========
    //=========================
    /**
     * 捕获系统参数请求参数异常
     *
     * @param ex 系统参数请求参数异常
     * @return Result
     */
    @ExceptionHandler(FilinkSystemParameterParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSystemParameterParamException(FilinkSystemParameterParamException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
    }
    /**
     * 捕获系统参数数据库操作异常
     *
     * @param ex 系统参数数据库操作异常
     * @return Result
     */
    @ExceptionHandler(FilinkSystemParameterDatabaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSystemParameterDatabaseException(FilinkSystemParameterDatabaseException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATABASE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATABASE_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATABASE_ERROR));
    }
    /**
     * 捕获系统参数数据库数据异常
     *
     * @param ex 系统参数数据库数据异常
     * @return Result
     */
    @ExceptionHandler(FilinkSystemParameterDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSystemParameterDataException(FilinkSystemParameterDataException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATA_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR));
    }
    /**
     * 捕获系统参数显示设置系统LOGO规格异常
     *
     * @param ex 系统参数显示设置系统LOGO规格异常
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoSizeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoSizeException(FilinkDisplaySettingsLogoSizeException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_SIZE_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_SIZE_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_SIZE_ERROR));
    }
    /**
     * 捕获系统参数显示设置系统LOGO格式异常
     *
     * @param ex 系统参数显示设置系统LOGO格式异常
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoFormatException(FilinkDisplaySettingsLogoFormatException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_FORMAT_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_FORMAT_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_FORMAT_ERROR));
    }
    /**
     * 捕获系统参数显示设置系统LOGO上传文件服务器异常
     *
     * @param ex 系统参数显示设置系统LOGO上传文件服务器异常
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoUploadException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoUploadException(FilinkDisplaySettingsLogoUploadException ex) {
        log.info(I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_UPLOAD_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_UPLOAD_ERROR,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_LOGO_UPLOAD_ERROR));
    }
}
