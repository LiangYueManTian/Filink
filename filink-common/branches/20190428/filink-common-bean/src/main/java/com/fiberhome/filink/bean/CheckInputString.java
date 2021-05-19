package com.fiberhome.filink.bean;
import org.springframework.util.StringUtils;

/**
 * <p>
 *   公共输入正则校验
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-18
 */
public class CheckInputString {
    /**
     * 名称32长度正则
     */
    private static final String SHORT_REGEX = "^[\\s\\da-zA-Z\\u4e00-\\u9fa5`\\-=\\[\\]\\\\;',./~!@#$%^&*\\(\\)_+{}|:\"<>?·【】、；'、‘’，。、！￥……（）——+｛｝：“”《》？]{1,32}$";
    /**
     * 备注255长度正则
     */
    private static final String LONG_REGEX = "^[\\s\\da-zA-Z\\u4e00-\\u9fa5`\\-=\\[\\]\\\\;',./~!@#$%^&*\\(\\)_+{}|:\"<>?·【】、；'、‘’，。、！￥……（）——+｛｝：“”《》？]{1,255}$";

    /**
     * 名称 32校验
     * @param str 字符串
     * @return 字符串
     */
    public static String nameCheck(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        str = str.trim();
        if (!str.matches(SHORT_REGEX)) {
            return null;
        }
        return str;
    }

    /**
     * 备注 255校验
     * @param str 字符串
     * @return 字符串
     */
    public static String markCheck(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        str = str.trim();
        if (!str.matches(LONG_REGEX)) {
            return null;
        }
        return str;
    }
}
