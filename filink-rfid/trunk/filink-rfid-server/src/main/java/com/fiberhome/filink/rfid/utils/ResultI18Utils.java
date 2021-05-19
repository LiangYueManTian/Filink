package com.fiberhome.filink.rfid.utils;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * i18n 结果转换
 *
 * @author liyj
 * @date 2019/6/14
 */
public class ResultI18Utils {

    /**
     * 返回结果  Result 结果转换
     *
     * @param code code
     * @param key  key 国际化
     * @return Reuslt
     */
    public static Result convertWarnResult(Integer code, String key) {
        String resultMsg = I18nUtils.getSystemString(key);
        return ResultUtils.warn(code, resultMsg);
    }

    /**
     * 转换成功结果
     *
     * @param key 国际化key
     * @return Result
     */
    public static Result convertSuccess(String key) {
        String resultMsg = I18nUtils.getSystemString(key);
        return ResultUtils.success(ResultCode.SUCCESS, resultMsg);
    }

    /**
     * 转换成功结果
     *
     * @param code code
     * @param key  key
     * @return Result
     */
    public static Result convertSuccess(Integer code, String key) {
        String resultMsg = I18nUtils.getSystemString(key);
        return ResultUtils.success(code, resultMsg);
    }
}
