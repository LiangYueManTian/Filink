package com.fiberhome.filink.dump.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 时间转换工具类
 * @author hedongwei@wistronits.com
 * @date 2019/6/4 14:55
 */

public class CastDateUtil {


    /**
     * 获取时间1点钟的值
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 11:39
     * @param addDay 当前时间加上偏移量
     * @param hour 时间
     * @return 获取计算后的时间
     */
    public static Long getNowDayTimeAdd(int addDay, int hour) {
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, addDay);
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }


    /**
     * 获取当前时间偏移月
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:31
     * @param num 偏移月
     * @param hour 小时
     * @return 返回月的时间
     */
    public static Long getMonthOneDay(int num, Long parseDate, int hour) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(parseDate);
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.add(Calendar.MONTH, num);
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }
}
