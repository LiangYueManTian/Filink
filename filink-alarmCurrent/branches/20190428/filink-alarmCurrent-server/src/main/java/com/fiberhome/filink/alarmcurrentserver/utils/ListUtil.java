package com.fiberhome.filink.alarmcurrentserver.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工具类
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public class ListUtil {

    /**
     * list判断是否为空
     *
     * @param list 集合
     * @return 判断结果
     */
    public static Boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * string数组判断是否为空
     *
     * @param str 数组
     * @return 判断结果
     */
    public static Boolean stringIsEmpty(String[] str) {
        return str == null || str.length == 0;
    }

    /**
     * map判断是否为空
     *
     * @param map map集合
     * @return 判断结果
     */
    public static Boolean mapIsEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * set判断是否为空
     *
     * @param set set集合
     * @return 判断结果
     */
    public static boolean isSetEmpty(Set set) {
        return set == null || set.size() == 0;
    }

    /**
     * 判断list
     *
     * @param list list集合
     * @return 判断结果
     */
    public static boolean isListNotExpty(List list) {
        return list != null && list.size() == 1;
    }
}
