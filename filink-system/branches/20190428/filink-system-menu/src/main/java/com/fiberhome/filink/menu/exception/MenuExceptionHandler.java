//package com.fiberhome.filink.menu.exception;
//
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.bean.ResultUtils;
//import com.fiberhome.filink.menu.constant.MenuI18nConstant;
//import com.fiberhome.filink.menu.constant.MenuResultCodeConstant;
//import com.fiberhome.filink.server_common.exception.GlobalExceptionHandler;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * <p>
// *AreaException处理类
// * </p>
// *
// * @author qiqizhu@wistronits.com
// * @since 2019-01-23
// */
//@Slf4j
//@RestControllerAdvice
//public class MenuExceptionHandler extends GlobalExceptionHandler {
//
//	/**
//	 * 捕获方案数据异常
//	 *
//	 * @param ex
//	 * @return
//	 */
//	@ExceptionHandler(FilinkMenuDirtyDataException.class)
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public Result handlerAreaDirtyDataException(FilinkMenuDirtyDataException ex) {
//		log.info(ex.getMessage());
//		ex.printStackTrace();
//		return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, I18nUtils.getString(MenuI18nConstant.DIRTY_DATA));
//	}
//	/**
//	 * 捕获方案数据异常
//	 *
//	 * @param ex
//	 * @return
//	 */
//	@ExceptionHandler(FilinkMenuDateBaseException.class)
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public Result handlerMenuDateBaseException(FilinkMenuDateBaseException ex) {
//		log.info(ex.getMessage());
//		ex.printStackTrace();
//		return ResultUtils.warn(MenuResultCodeConstant.DATE_BASE_ERROR, I18nUtils.getString(MenuI18nConstant.DATE_BASE_ERROR));
//	}
//}
