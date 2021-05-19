package com.fiberhome.filink.rfid.utils;

import java.util.Date;

/**
 * utc时间工具类
 * @author chaofanrong@wistronits.com
 * @date 2019/6/22
 */

public class UtcTimeUtil {

    /*---------------------------------------获取utc时间公共方法start------------------------------------------*/
    /**
     * java 获取UTC时间
     */
    public static Date getUtcTime() {
        //1、取得本地时间：
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        //2、取得时间偏移量：
        final int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        //3、取得夏令时差：
        final int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        //4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTime();
    }
    /*---------------------------------------获取utc时间公共方法end------------------------------------------*/
}
