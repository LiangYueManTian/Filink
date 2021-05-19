package com.fiberhome.filink.commonstation.utils;

/**
 * String工具类
 * @author CongcaiYu
 */
public class StringUtil {

    /**
     * 字符串是否为空
     * @param s 需要判断的字符串
     * @return 是否为空
     */
    public static boolean strIsNullOrEmpty(String s) {
        return (null == s || s.trim().length() < 1);
    }
}
