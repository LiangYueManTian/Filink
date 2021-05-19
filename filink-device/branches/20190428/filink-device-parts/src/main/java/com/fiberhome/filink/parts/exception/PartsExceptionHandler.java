package com.fiberhome.filink.parts.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.parts.constant.PartsI18n;
import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/12 15:42
 */
@Slf4j
@RestControllerAdvice
public class PartsExceptionHandler extends GlobalExceptionHandler {

	/**
	 * 捕获方案参数无效异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(FilinkPartsNameSameException.class)
	@ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
	@ResponseBody
	public Result handlerPartsNameSameException(FilinkPartsNameSameException ex) {
		log.info("------------"+ex.getMessage());
		return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(PartsI18n.PARTS_NAME_SAME));
	}

	/**
	 * 设施参数异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(FilinkPartsException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Result handlerDeviceException(FilinkPartsException ex) {
		log.info(ex.getMessage());
		return ResultUtils.warn(ResultCode.FAIL, ex.getMessage());
	}
}