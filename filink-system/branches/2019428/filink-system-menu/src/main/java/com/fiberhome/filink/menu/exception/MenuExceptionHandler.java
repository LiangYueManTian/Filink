//package com.fiberhome.filink.menu.exception;
//
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.bean.ResultUtils;
//import com.fiberhome.filink.menu.bean.MenuI18n;
//import com.fiberhome.filink.menu.utils.MenuRusultCode;
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
//		return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, I18nUtils.getString(MenuI18n.DIRTY_DATA));
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
//		return ResultUtils.warn(MenuRusultCode.DATE_BASE_ERROR, I18nUtils.getString(MenuI18n.DATE_BASE_ERROR));
//	}
//}
