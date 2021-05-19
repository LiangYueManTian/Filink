package com.fiberhome.filink.workflowbusinessserver.exception.handler;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.exception.*;
import com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate.*;
import com.fiberhome.filink.workflowbusinessserver.exception.procinspection.CommitProcInspectionException;
import com.fiberhome.filink.workflowbusinessserver.exception.procinspection.InspectionDeviceException;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procinspection.ProcInspectionResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
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
 * @author hedongwei@wistronits.com
 * @since 2019/2/27
 */
@Slf4j
@RestControllerAdvice
public class FilinkWorkflowBusinessExceptionHandler extends GlobalExceptionHandler {

    /**
     * 捕获方案数据异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkWorkflowBusinessDataException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerExportDateBaseException(FilinkWorkflowBusinessDataException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(WorkflowBusinessResultCode.DATA_ERROR, I18nUtils.getString(WorkflowBusinessI18n.DATA_ERROR));
    }


    /**
     * 捕获新增工单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AddProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAddProcErrorException(AddProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_ADD_ERROR, I18nUtils.getString(ProcBaseI18n.ADD_PROC_FAIL));
    }

    /**
     * 捕获修改工单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(UpdateProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerUpdateProcErrorException(UpdateProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_UPDATE_ERROR, I18nUtils.getString(ProcBaseI18n.UPDATE_PROC_FAIL));
    }


    /**
     * 工单指派异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AssignProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerAssignProcErrorException(AssignProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_ASSIGN_ERROR, I18nUtils.getString(ProcBaseI18n.PROC_ASSIGN_FAIL));
    }

    /**
     * 工单下载异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(DownLoadProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDownloadProcErrorException(DownLoadProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_DOWNLOAD_ERROR, I18nUtils.getString(ProcBaseI18n.DOWNLOAD_PROC_FAIL));
    }

    /**
     * 工单撤回异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(RevokeProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerRevokeProcErrorException(RevokeProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_REVOKE_ERROR, I18nUtils.getString(ProcBaseI18n.REVOKE_FAIL));
    }

    /**
     * 退单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(SingleBackProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerSingleBackProcErrorException(SingleBackProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_SINGLE_BACK_ERROR, I18nUtils.getString(ProcBaseI18n.SINGLE_BACK_FAIL));
    }


    /**
     * 转派异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(TurnProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerTurnProcErrorException(TurnProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_TURN_ERROR, I18nUtils.getString(ProcBaseI18n.PROC_TURN_FAIL));
    }

    /**
     * 确认退单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(CheckSingleBackProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerCheckSingleBackProcErrorException(CheckSingleBackProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.PROC_CHECK_SINGLE_BACK_ERROR, I18nUtils.getString(ProcBaseI18n.CHECK_SINGLE_BACK_FAIL));
    }


    /**
     * 巡检设施异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(InspectionDeviceException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerInspectionDeviceException(InspectionDeviceException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcInspectionResultCode.INSPECTION_DEVICE_FAILED, I18nUtils.getString(ProcInspectionI18n.INSPECTION_DEVICE_FAILED));
    }

    /**
     * 提交巡检工单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(CommitProcInspectionException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerCommitProcInspectionException(CommitProcInspectionException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcInspectionResultCode.COMMIT_INSPECTION_FAILED, I18nUtils.getString(ProcInspectionI18n.COMMIT_INSPECTION_FAILED));
    }

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
        return ResultUtils.warn(ProcBaseResultCode.USER_SERVER_ERROR, I18nUtils.getString(ProcBaseI18n.USER_SERVER_ERROR));
    }

    /**
     * 获取告警信息异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkObtainAlarmInfoException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerCommitProcInspectionException(FilinkObtainAlarmInfoException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.ALARM_SERVER_ERROR, I18nUtils.getString(ProcBaseI18n.ALARM_SERVER_ERROR));
    }

    /**
     * 获取设施信息异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(FilinkObtainDeviceInfoException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerObtainDeviceInfoException(FilinkObtainDeviceInfoException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.DEVICE_SERVER_ERROR, I18nUtils.getString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
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
     * 删除工单失败
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(DeleteProcErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handlerDeleteProcErrorException(DeleteProcErrorException ex) {
        log.info(ex.getMessage());
        ex.printStackTrace();
        return ResultUtils.warn(ProcBaseResultCode.DELETE_PROC_FAIL, I18nUtils.getString(ProcBaseI18n.DELETE_PROC_FAIL));
    }

}
