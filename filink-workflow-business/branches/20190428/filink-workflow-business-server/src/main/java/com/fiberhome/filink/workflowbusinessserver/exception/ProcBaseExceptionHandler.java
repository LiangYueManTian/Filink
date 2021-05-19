package com.fiberhome.filink.workflowbusinessserver.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author chaofanrong@wistronits.com
 * @date 2019/3/6 11:27
 */
@Slf4j
@RestControllerAdvice
public class ProcBaseExceptionHandler extends GlobalExceptionHandler {

	//==================================
	//===============工单===============
	//==================================

	/**
	 * 获取部门信息异常
	 * @param ex FilinkObtainDeptInfoException
	 */
	@ExceptionHandler(FilinkObtainDeptInfoException.class)
	@ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
	@ResponseBody
	public Result handleFilinkObtainDeptInfoException(FilinkObtainDeptInfoException ex) {
		log.info(ex.getMessage());
		return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
	}

	/**
	 * 获取设施信息异常
	 * @param ex FilinkObtainDeviceInfoException
	 */
	@ExceptionHandler(FilinkObtainDeviceInfoException.class)
	@ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
	@ResponseBody
	public Result handleFilinkObtainDeviceInfoException(FilinkObtainDeviceInfoException ex) {
		log.info(ex.getMessage());
		return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
	}
}
