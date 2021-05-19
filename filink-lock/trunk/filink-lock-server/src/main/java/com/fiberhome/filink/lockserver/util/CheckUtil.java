package com.fiberhome.filink.lockserver.util;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <p>
 * 校验类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/28
 */
public class CheckUtil {

    /**
     * 根据正则表达式，检验字符串格式是否正确
     * 检验前去掉字符串前后空格
     *
     * @param str   字符串
     * @param regex 正则表达式
     * @return true 格式正确 false 格式错误
     */
    public static boolean checkStrWithRegex(String str, String regex) {
        String defineStr = str.trim();
        if (StringUtils.isEmpty(defineStr)) {
            return false;
        }
        return defineStr.matches(regex);
    }

    /**
     * 根据正则表达式校验配置策略
     *
     * @param configParams
     * @param configPatterns
     * @return true-校验成功  false-校验失败
     */
    public static boolean checkConfig(Map<String, String> configParams, Map<String, String> configPatterns) {
        boolean flag = true;
        for (Map.Entry<String, String> configParam : configParams.entrySet()) {
            for (Map.Entry<String, String> configPattern : configPatterns.entrySet()) {
                if (!flag) {
                    return flag;
                }
                if (configParam.getKey().equals(configPattern.getKey())) {
                    flag &= checkStrWithRegex(configParam.getValue(), configPattern.getValue());
                }
            }
        }
        return flag;
    }

}
