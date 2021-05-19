package com.fiberhome.filink.logserver.utils;

import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * @author hedongwei@wistronits.com
 * @Description 日志i18n转换
 * @date 2019/1/26 10:30
 */

public class LogI18nCast {

    /**
     * @author hedongwei@wistronits.com
     * description 危险级别获取
     * date 10:33 2019/1/26
     * param [dangerLevel]
     */
    public static String getDangerLevel(Integer dangerLevel) {
        //危险级别名称
        String dangerLevelName = "";
        if (LogConstants.DANGER_LEVEL_PROMPT.equals(dangerLevel)) {
            //危险级别提示
            dangerLevelName = I18nUtils.getString(I18nConstants.DANGER_LEVEL_PROMPT);
        } else if (LogConstants.DANGER_LEVEL_DANGER.equals(dangerLevel)) {
            //危险级别危险
            dangerLevelName = I18nUtils.getString(I18nConstants.DANGER_LEVEL_DANGER);
        } else if (LogConstants.DANGER_LEVEL_GENERAL.equals(dangerLevel)) {
            //危险级别一般
            dangerLevelName = I18nUtils.getString(I18nConstants.DANGER_LEVEL_GENERAL);
        }
        return dangerLevelName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 危险级别获取
     * date 10:33 2019/1/26
     * param [optType]
     */
    public static String getOptType(String optType) {
        //操作类型名称
        String optTypeName = "";
        if (LogConstants.OPT_TYPE_PDA.equals(optType)) {
            //PDA操作日志
            optTypeName = I18nUtils.getString(I18nConstants.LOG_OPT_TYPE_PDA);
        } else if (LogConstants.OPT_TYPE_WEB.equals(optType)) {
            //网管操作日志
            optTypeName = I18nUtils.getString(I18nConstants.LOG_OPT_TYPE_WEB);
        }
        return optTypeName;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 危险级别获取
     * date 10:33 2019/1/26
     * param [optType]
     */
    public static String getOptResult(String optResult) {
        //操作结果
        String optResultName = "";
        if (LogConstants.OPT_RESULT_SUCCESS.equals(optResult)) {
            //操作成功
            optResultName = I18nUtils.getString(I18nConstants.OPT_RESULT_SUCCESS);
        } else if (LogConstants.OPT_RESULT_FAILURE.equals(optResult)) {
            //网管操作日志
            optResultName = I18nUtils.getString(I18nConstants.OPT_RESULT_FAILURE);
        }
        return optResultName;
    }
}
