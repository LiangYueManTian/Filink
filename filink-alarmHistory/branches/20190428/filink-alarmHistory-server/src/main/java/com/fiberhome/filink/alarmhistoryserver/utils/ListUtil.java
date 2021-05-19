package com.fiberhome.filink.alarmhistoryserver.utils;

import java.util.List;

/**
 * 工具类
 *
 * @author weikuan
 */
public class ListUtil {

    /**
     * list判断是否为空
     *
     * @param list 集合
     * @return 判断结果
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * string数组判断是否为空
     *
     * @param str 数组
     * @return 判断结果
     */
    public static boolean stringIsEmpty(String[] str) {
        return str == null || str.length == 0;
    }
}
