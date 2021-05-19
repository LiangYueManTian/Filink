package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 名字去除特殊字符的方法
 *
 * @author xuangong
 */
public class NameUtils {

    public static String removeBlank(String name) {

        if (StringUtils.isEmpty(name)) {
            return name;
        }
        name = name.trim();
        while (name.startsWith(UserConstant.BLANK_CODE)) {
            name = name.substring(1, name.length()).trim();
        }
        while (name.endsWith(UserConstant.BLANK_CODE)) {
            name = name.substring(0, name.length() - 1).trim();
        }
        return name;
    }
}
