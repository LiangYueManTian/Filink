package com.fiberhome.filink.systemserver.exception;

import com.fiberhome.filink.about.constant.AboutI18n;
import com.fiberhome.filink.about.constant.AboutResultCode;
import com.fiberhome.filink.about.exception.AboutParamsException;
import com.fiberhome.filink.about.exception.AboutSystemException;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolResultCode;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.export.constant.ExportI18nConstant;
import com.fiberhome.filink.export.exception.FilinkExportDateBaseException;
import com.fiberhome.filink.export.exception.FilinkExportDirtyDataException;
import com.fiberhome.filink.menu.constant.MenuI18nConstant;
import com.fiberhome.filink.menu.constant.MenuResultCodeConstant;
import com.fiberhome.filink.menu.exception.FilinkMenuDateBaseException;
import com.fiberhome.filink.menu.exception.FilinkMenuDateFormatException;
import com.fiberhome.filink.menu.exception.FilinkMenuDirtyDataException;
import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
import com.fiberhome.filink.parameter.exception.*;
import com.fiberhome.filink.protocol.constant.ProtocolI18n;
import com.fiberhome.filink.protocol.constant.ProtocolResultCode;
import com.fiberhome.filink.protocol.exception.ProtocolParamsErrorException;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.securitystrategy.exception.*;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.constant.SysParamI18n;
import com.fiberhome.filink.systemcommons.constant.SysParamResultCode;
import com.fiberhome.filink.systemcommons.exception.FilinkSysParamDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ????????????????????????
 *
 * @author chaofang@wistronits.com
 * @since \2019/2/18
 */
@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler extends GlobalExceptionHandler {
    //=========================
    //=======????????????==========
    //=========================

    /**
     * ??????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolDeleteException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolDeleteException(FilinkDeviceProtocolDeleteException ex) {
        log.error("FilinkDeviceProtocolDeleteException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_DELETE_FAIL,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_FAIL));
    }
    /**
     * ??????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolAddException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolAddException(FilinkDeviceProtocolAddException ex) {
        log.error("FilinkDeviceProtocolAddException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_ADD_FAIL,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_FAIL));
    }
    /**
     * ??????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolUpdateException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolUpdateException(FilinkDeviceProtocolUpdateException ex) {
        log.error("FilinkDeviceProtocolUpdateException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_UPDATE_FAIL,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_FAIL));
    }

    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolVersionException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileContentException(FilinkDeviceProtocolVersionException ex) {
        log.error("FilinkDeviceProtocolVersionException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_VERSION_ERROR,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_VERSION_ERROR));
    }

    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolFileUploadException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileUploadException(FilinkDeviceProtocolFileUploadException ex) {
        log.error("FilinkDeviceProtocolFileUploadException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_UPLOAD_ERROR,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_UPLOAD_ERROR));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolFileExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileExistException(FilinkDeviceProtocolFileExistException ex) {
        log.error("FilinkDeviceProtocolFileExistException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_EXIST,
                ex.getMessage());
    }

    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolFileFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileFormatException(FilinkDeviceProtocolFileFormatException ex) {
        log.error("FilinkDeviceProtocolFileFormatException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_READ_FAIL,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_READ_FAIL));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolNameExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolNameExistException(FilinkDeviceProtocolNameExistException ex) {
        log.error("FilinkDeviceProtocolNameExistException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_NAME_EXIST,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_NAME_EXIST));
    }

    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolFileNameException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileNameException(FilinkDeviceProtocolFileNameException ex) {
        log.error("FilinkDeviceProtocolFileNameException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_NAME_ERROR,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME_ERROR));
    }
    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolFileSizeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolFileSizeException(FilinkDeviceProtocolFileSizeException ex) {
        log.error("FilinkDeviceProtocolFileSizeException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_FILE_SIZE_ERROR,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE_ERROR));
    }

    /**
     * ?????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */
    @ExceptionHandler(FilinkDeviceProtocolNotExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolNotExistException(FilinkDeviceProtocolNotExistException ex) {
        log.error("FilinkDeviceProtocolNotExistException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_NOT_EXIST,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_NOT_EXIST));
    }

    /**
     * ????????????????????????????????????
     *
     * @param ex ??????
     * @return ??????
     */

    @ExceptionHandler(FilinkDeviceProtocolParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeviceProtocolParamException(FilinkDeviceProtocolParamException ex) {
        log.error("FilinkDeviceProtocolParamException:", ex);
        return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                I18nUtils.getSystemString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
    }
    //=========================
    //=======????????????==========
    //=========================

    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAreaDirtyDataException(FilinkMenuDirtyDataException ex) {
        log.error("FilinkMenuDirtyDataException:", ex);
        return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, I18nUtils.getSystemString(MenuI18nConstant.DIRTY_DATA));
    }

    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerMenuDateBaseException(FilinkMenuDateBaseException ex) {
        log.error("FilinkMenuDateBaseException:", ex);
        return ResultUtils.warn(MenuResultCodeConstant.DATE_BASE_ERROR, I18nUtils.getSystemString(MenuI18nConstant.DATABASE_ERROR));
    }

    //=========================
    //=======????????????==========
    //=========================

    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ProtocolSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerProtocolSystemException(ProtocolSystemException ex) {
        log.error("ProtocolSystemException:", ex);
        return ResultUtils.warn(ProtocolResultCode.PROTOCOL_SYSTEM_ERROR, I18nUtils.getSystemString(ProtocolI18n.PROTOCOL_SYSTEM_ERROR));
    }


    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ProtocolParamsErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerProtocolParamsEmptyException(ProtocolParamsErrorException ex) {
        log.error("ProtocolParamsErrorException:", ex);
        return ResultUtils.warn(ProtocolResultCode.PROTOCOL_PRAMS_ERROR, I18nUtils.getSystemString(ProtocolI18n.PROTOCOL_PRAMS_ERROR));
    }

    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkMenuDateFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerMenuDateFormatException(FilinkMenuDateFormatException ex) {
        log.error("FilinkMenuDateFormatException:", ex);
        return ResultUtils.warn(MenuResultCodeConstant.PARAMETER_FORMAT_IS_INCORRECT, I18nUtils.getSystemString(MenuI18nConstant.PARAMETER_FORMAT_IS_INCORRECT));
    }

    //=========================
    //=======????????????==========
    //=========================

    /**
     * ???????????????????????????????????????
     *
     * @param ex ?????????????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkSecurityStrategyDatabaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSecurityStrategyDatabaseException(FilinkSecurityStrategyDatabaseException ex) {
        log.error("FilinkSecurityStrategyDatabaseException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_DATABASE_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_DATABASE_ERROR));
    }

    /**
     * ??????????????????????????????
     *
     * @param ex ????????????IP????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkSecurityStrategyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSecurityStrategyDataException(FilinkSecurityStrategyDataException ex) {
        log.error("FilinkSecurityStrategyDataException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_DATA_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_DATA_ERROR));
    }

    /**
     * ??????????????????IP????????????????????????
     *
     * @param ex ????????????IP????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeFormatException(FilinkIpRangeFormatException ex) {
        log.error("FilinkIpRangeFormatException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_FORMAT_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_FORMAT_ERROR));
    }
    /**
     * ??????????????????IP?????????????????????
     *
     * @param ex ????????????IP?????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotExistException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotExistException(FilinkIpRangeNotExistException ex) {
        log.error("FilinkIpRangeNotExistException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_EXIST_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_NOT_EXIST_ERROR));
    }
    /**
     * ??????????????????IP????????????????????????????????????
     *
     * @param ex ????????????IP????????????????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotRangeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotRangeException(FilinkIpRangeNotRangeException ex) {
        log.error("FilinkIpRangeNotRangeException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_RANGE_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_NOT_RANGE_ERROR));
    }
    /**
     * ??????????????????IP?????????????????????
     *
     * @param ex ????????????IP?????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeNotZeroException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeNotZeroException(FilinkIpRangeNotZeroException ex) {
        log.error("FilinkIpRangeNotZeroException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_NOT_ZERO_ERROR,
                I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_NOT_ZERO_ERROR));
    }
    /**
     * ??????????????????IP????????????????????????
     *
     * @param ex ????????????IP????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeOverlapException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeOverlapException(FilinkIpRangeOverlapException ex) {
        log.error("FilinkIpRangeOverlapException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_OVERLAP_ERROR,
                ex.getMessage());
    }
    /**
     * ??????????????????IP????????????????????????
     *
     * @param ex ????????????IP????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkIpRangeContainException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerIpRangeContainException(FilinkIpRangeContainException ex) {
        log.error("FilinkIpRangeContainException:", ex);
        return ResultUtils.warn(SecurityStrategyResultCode.IP_RANGE_CONTAIN_ERROR,
                ex.getMessage());
    }


    //=========================
    //=======??????==========
    //=========================

    /**
     * ??????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AboutSystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAboutSystemException(AboutSystemException ex) {
        log.error("AboutSystemException:", ex);
        return ResultUtils.warn(AboutResultCode.ABOUT_SYSTEM_ERROR, I18nUtils.getSystemString(AboutI18n.ABOUT_SYSTEM_ERROR));
    }

    /**
     * ??????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AboutParamsException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAboutParamsException(AboutParamsException ex) {
        log.error("AboutParamsException:", ex);
        return ResultUtils.warn(AboutResultCode.ABOUT_PARAMS_ERROR, I18nUtils.getSystemString(AboutI18n.ABOUT_PARAMS_ERROR));
    }
    //=========================
    //=======????????????==========
    //=========================
    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkExportDirtyDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerExportDirtyDataException(FilinkExportDirtyDataException ex) {
        log.error("FilinkExportDirtyDataException:", ex);
        String msg=ex.getMessage();
        if(StringUtils.isEmpty(msg)){
            return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, I18nUtils.getSystemString(ExportI18nConstant.DIRTY_DATA));
        }
        return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, msg);
    }
    /**
     * ????????????????????????
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkExportDateBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerExportDateBaseException(FilinkExportDateBaseException ex) {
        log.error("FilinkExportDateBaseException:", ex);
        return ResultUtils.warn(MenuResultCodeConstant.DATE_BASE_ERROR, I18nUtils.getSystemString(ExportI18nConstant.DATA_BASE_ERROR));
    }

    //=========================
    //=======????????????????????????commons==========
    //=========================
    /**
     * ???????????????????????????????????????????????????
     *
     * @param ex ?????????????????????????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkSysParamDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSysParamDataException(FilinkSysParamDataException ex) {
        log.error("FilinkSysParamDataException:", ex);
        return ResultUtils.warn(SysParamResultCode.SYSTEM_PARAM_DATA_ERROR,
                I18nUtils.getSystemString(SysParamI18n.SYSTEM_PARAM_DATA_ERROR));
    }
    //=========================
    //=======????????????==========
    //=========================
    /**
     * ???????????????????????????????????????
     *
     * @param ex ?????????????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkSystemParameterDatabaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSystemParameterDatabaseException(FilinkSystemParameterDatabaseException ex) {
        log.error("FilinkSystemParameterDatabaseException:", ex);
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATABASE_ERROR,
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_DATABASE_ERROR));
    }
    /**
     * ???????????????????????????????????????
     *
     * @param ex ?????????????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkSystemParameterDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSystemParameterDataException(FilinkSystemParameterDataException ex) {
        log.error("FilinkSystemParameterDataException:", ex);
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATA_ERROR,
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR));
    }
    /**
     * ????????????????????????????????????LOGO????????????
     *
     * @param ex ??????????????????????????????LOGO????????????
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoSizeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoSizeException(FilinkDisplaySettingsLogoSizeException ex) {
        log.error("FilinkDisplaySettingsLogoSizeException:", ex);
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_SIZE_ERROR,
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_LOGO_SIZE_ERROR));
    }
    /**
     * ????????????????????????????????????LOGO????????????
     *
     * @param ex ??????????????????????????????LOGO????????????
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoFormatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoFormatException(FilinkDisplaySettingsLogoFormatException ex) {
        log.error("FilinkDisplaySettingsLogoFormatException:", ex);
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_FORMAT_ERROR,
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_LOGO_FORMAT_ERROR));
    }
    /**
     * ????????????????????????????????????LOGO???????????????????????????
     *
     * @param ex ??????????????????????????????LOGO???????????????????????????
     * @return Result
     */
    @ExceptionHandler(FilinkDisplaySettingsLogoUploadException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDisplaySettingsLogoUploadException(FilinkDisplaySettingsLogoUploadException ex) {
        log.error("FilinkDisplaySettingsLogoUploadException:", ex);
        return ResultUtils.warn(SystemParameterResultCode.SYSTEM_LOGO_UPLOAD_ERROR,
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_LOGO_UPLOAD_ERROR));
    }
}
