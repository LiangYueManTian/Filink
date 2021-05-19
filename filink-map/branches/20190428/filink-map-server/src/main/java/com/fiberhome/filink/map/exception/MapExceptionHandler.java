package com.fiberhome.filink.map.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.map.utils.MapI18n;
import com.fiberhome.filink.map.utils.MapResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>
 *   地区服务异常处理
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/7
 */
@Slf4j
@ControllerAdvice
public class MapExceptionHandler {
    /**
     * 捕获地区服务系统异常
     * @param ex
     * @return 异常结果
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MapSystemException.class)
    @ResponseBody
    public Result handlerMapSystemException(MapSystemException ex) {
        log.error(I18nUtils.getString(MapI18n.MAP_SYSTEM_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(MapResultCode.MAP_SYSTEM_ERROR,I18nUtils.getString(MapI18n.MAP_SYSTEM_ERROR));
    }

    /**
     * 捕获地区服务请求参数异常
     * @param ex
     * @return 异常结果
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MapParamsException.class)
    @ResponseBody
    public Result handlerMapParamsException(MapParamsException ex) {
        log.error(I18nUtils.getString(MapI18n.MAP_PARAMS_ERROR));
        ex.printStackTrace();
        return ResultUtils.warn(MapResultCode.MAP_PARAMS_ERROR,I18nUtils.getString(MapI18n.MAP_PARAMS_ERROR));
    }
}
