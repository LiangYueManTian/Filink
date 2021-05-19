package com.fiberhome.filink.logserver.utils;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/4/8 9:43
 */

public class LogParamValidate {


    /**
     * 校验操作名称
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 9:48
     * @param optName 操作名称
     * @return 校验操作名称结果
     */
    public static Result checkOptName(String optName) {
        Integer optNameMinLength = 1;
        Integer optNameMaxLength = 255;
        if (!LogParamValidate.checkObject(optName, optNameMinLength, optNameMaxLength)) {
            //提示操作名称校验错误
            return ResultUtils.warn(LogResultCode.OPT_NAME_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_NAME_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验操作用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 10:53
     * @param optUserCode 操作用户code
     * @return 校验操作用户结果
     */
    public static Result checkOptUserCode(String optUserCode) {
        Integer optUserCodeMinLength = 1;
        Integer optUserCodeMaxLength = 32;
        if (!LogParamValidate.checkObject(optUserCode, optUserCodeMinLength, optUserCodeMaxLength)) {
            //提示操作用户校验错误
            return ResultUtils.warn(LogResultCode.OPT_USER_CODE_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_USER_CODE_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验操作时间
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 10:57
     * @param optTime 操作时间
     * @return 校验操作时间
     */
    public static Result checkOptTime(Long optTime) {
        if (ObjectUtils.isEmpty(optTime)) {
            //提示操作时间校验错误
            return ResultUtils.warn(LogResultCode.OPT_TIME_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_TIME_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验操作终端
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 10:57
     * @param optTerminal 操作终端
     * @return 校验操作终端
     */
    public static Result checkOptTerminal(String optTerminal) {
        if (!ValidateUtils.validateDataInfoIsEmpty(optTerminal)) {
            //提示操作终端校验错误
            return ResultUtils.warn(LogResultCode.OPT_TERMINAL_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_TERMINAL_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验操作对象
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 10:57
     * @param optObj 操作对象
     * @return 校验操作对象
     */
    public static Result checkOptObj(String optObj) {
        Integer optObjMinLength = 1;
        Integer optObjMaxLength = 255;
        if (!LogParamValidate.checkObject(optObj, optObjMinLength, optObjMaxLength)) {
            //提示操作对象校验错误
            return ResultUtils.warn(LogResultCode.OPT_OBJ_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_OBJ_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验操作结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 11:09
     * @param optResult 操作结果
     * @return 校验操作结果
     */
    public static Result checkOptResult(String optResult) {
        Integer optResultMinLength = 1;
        Integer optResultMaxLength = 255;
        if (!LogParamValidate.checkObject(optResult, optResultMinLength, optResultMaxLength)) {
            //提示操作结果校验错误
            return ResultUtils.warn(LogResultCode.OPT_RESULT_IS_WRONG, I18nUtils.getSystemString(I18nConstants.OPT_RESULT_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验详细信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 11:09
     * @param detailInfo 详细信息
     * @return 校验详细信息
     */
    public static Result checkDetailInfo(String detailInfo) {
        Integer detailInfoMinLength = 1;
        Integer detailInfoMaxLength = 1024;
        if (!LogParamValidate.checkObject(detailInfo, detailInfoMinLength, detailInfoMaxLength)) {
            //提示详细信息校验错误
            return ResultUtils.warn(LogResultCode.DETAIL_INFO_IS_WRONG, I18nUtils.getSystemString(I18nConstants.DETAIL_INFO_IS_WRONG));
        }
        return null;
    }

    /**
     * 校验危险级别信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 11:09
     * @param dangerLevel 危险级别
     * @return 校验危险级别
     */
    public static Result checkDangerLevel(Integer dangerLevel) {
        if (ObjectUtils.isEmpty(dangerLevel)) {
            //提示危险级别校验错误
            return ResultUtils.warn(LogResultCode.DANGER_LEVEL_IS_WRONG, I18nUtils.getSystemString(I18nConstants.DANGER_LEVEL_IS_WRONG));
        }
        return null;
    }



    /**
     * 返回校验数据结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 9:53
     * @param data 数据
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 返回校验结果
     */
    public static boolean checkObject(String data , Integer minLength, Integer maxLength) {
        if (!ValidateUtils.validateDataInfoIsEmpty(data)) {
            return false;
        }

        if (!ValidateUtils.validateDataLength(data, minLength, maxLength)) {
            return false;
        }
        return true;
    }

}
