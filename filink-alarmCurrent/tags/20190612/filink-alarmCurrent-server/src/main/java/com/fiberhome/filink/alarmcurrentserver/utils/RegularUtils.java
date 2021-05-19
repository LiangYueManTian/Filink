package com.fiberhome.filink.alarmcurrentserver.utils;

import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.bean.CheckInputString;

/**
 * <p>
 * 正则表达式
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-13
 */
public class RegularUtils {

    /**
     * 备注校验
     *
     * @param protocolName 备注信息
     * @return
     */
    public static boolean checkIdAndName(String protocolName) {
        if (protocolName != null) {
            protocolName = CheckInputString.markCheck(protocolName);
            if (StringUtils.isEmpty(protocolName)) {
                return false;
            }
        }
        return StringUtils.isEmpty(protocolName);
    }

    /**
     * 参数校验
     *
     * @param protocolName 参数信息
     * @return 判断结果
     */
    public static boolean checkIdAndNames(String protocolName) {
        protocolName = CheckInputString.nameCheck(protocolName);
        return StringUtils.isEmpty(protocolName);
    }
}
