package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
public class DateUtil {

    /**
     * 根据传入的时间戳。返回每天的日期List
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 每天日期信息
     */
    public static List<String> getTimeDayBetween(Long beginTime, Long endTime) {
        List<String> timeList = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String b = format.format(beginTime);
            String e = format.format(endTime);
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(b);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(e);
            Calendar dd = Calendar.getInstance();
            dd.setTime(d1);
            while (dd.getTime().before(d2)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(dd.getTime());
                timeList.add(str);
                dd.add(Calendar.DATE, 1);
            }
            timeList.add(e);
        } catch (Exception e) {
            log.error("时间异常" + e.getMessage());
        }
        return timeList;
    }

    /**
     * 前15周的日期集合
     *
     * @return 15周日期信息
     */
    public static List<String> getTimeWeekBetween() {
        List<String> timeList = new ArrayList<>();
        try {
            for (int i = 1; i <= LogFunctionCodeConstant.ALARM_FIFTEEN; i++) {
                Calendar c1 = new GregorianCalendar();
                c1.set(Calendar.DAY_OF_WEEK, 2);
                c1.add(Calendar.DATE, -7 * i);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(c1.getTime());
                timeList.add(str);
            }
        } catch (Exception e) {
            log.error("时间异常" + e.getMessage());
        }
        return timeList;
    }

    /**
     * 根据传入的时间戳。返回每周的日期List
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 周日期信息
     */
    public static List<String> getTimeWeekBetween(Long beginTime, Long endTime) {
        List<String> timeList = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String b = format.format(beginTime);
            String e = format.format(endTime);
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(b);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(e);
            Calendar dd = Calendar.getInstance();
            dd.setTime(d1);
            while (dd.getTime().before(d2)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(dd.getTime());
                timeList.add(str);
                dd.add(Calendar.DAY_OF_MONTH, 7);
            }
            timeList.add(e);
        } catch (Exception e) {
            log.error("时间异常" + e.getMessage());
        }
        return timeList;
    }

    /**
     * 根据输入时间求每周时间
     *
     * @param beginTime
     * @param endTime
     * @return 时间信息
     */
    public static List<Long> getTimeLongWeekBetween(Long beginTime, Long endTime) {
        List<Long> longs = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String b = format.format(beginTime);
            String e = format.format(endTime);
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(b);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(e);
            Calendar dd = Calendar.getInstance();
            dd.setTime(d1);
            while (dd.getTime().before(d2)) {
                long timeInMillis = dd.getTimeInMillis();
                longs.add(timeInMillis);
                dd.add(Calendar.DAY_OF_MONTH, 7);
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return longs;
    }

    /**
     * 前12月日期集合
     *
     * @return 12月日期信息
     */
    public static List<String> getTimeMonthBetween() {
        List<String> timeList = new ArrayList<>();
        try {
            for (int i = 1; i <= LogFunctionCodeConstant.ALARM_TWELVE; i++) {
                Calendar c1 = new GregorianCalendar();
                c1.add(Calendar.MONTH, -i);
                c1.set(Calendar.DAY_OF_MONTH, 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(c1.getTime());
                timeList.add(str);
            }
        } catch (Exception e) {
            log.error("时间异常" + e.getMessage());
        }
        return timeList;
    }

    /**
     * 提前num的天数(开始时间)
     *
     * @param num 天数信息
     * @return 时间信息
     */
    public static Long getAdvanceNumberDay(int num) {
        Calendar c1 = new GregorianCalendar();
        c1.add(Calendar.DATE, -num);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
//        System.out.println(c1.getTimeInMillis() + "-------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c1.getTime()));
        return c1.getTimeInMillis();
    }

    /**
     * 获取时间0点参数
     *
     * @param time 时间参数
     * @return 获取当前0时区的时间
     * @author hedongwei@wistronits.com
     * @date 2019/6/26 14:15
     */
    public static Long getTimeZeroTime(Long time) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(time);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
//        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    public static Long getTimeZeroTimeEnd(Long time) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(time);
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        System.out.println(date.getTimeInMillis() + "------------------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
        return date.getTimeInMillis();
    }

    /**
     * 前一天的结束
     *
     * @return 时间信息
     */
    public static Long getAdvanceNumberEndDay() {
        Calendar c2 = new GregorianCalendar();
        c2.add(Calendar.DATE, -1);
        c2.set(Calendar.HOUR_OF_DAY, 23);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 999);
//        System.out.println(c2.getTimeInMillis() + "----" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c2.getTime()));
        return c2.getTimeInMillis();
    }

    /**
     * 提前数字的周(开始时间)
     *
     * @param num 天数信息
     * @return 时间信息
     */
    public static Long getAdvanceNumberWeek(int num) {
        Calendar c1 = new GregorianCalendar();
        c1.set(Calendar.DAY_OF_WEEK, 2);
        c1.add(Calendar.DATE, -7 * num);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
//        System.out.println(c1.getTimeInMillis() + "------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c1.getTime()));
        return c1.getTimeInMillis();
    }

    /**
     * 上周的结束时间
     *
     * @return 时间信息
     */
    public static Long getAdvanceNumberEndWeek() {
        Calendar c2 = new GregorianCalendar();
        c2.set(Calendar.DAY_OF_WEEK, 2);
        c2.add(Calendar.DATE, -1);
        c2.set(Calendar.HOUR_OF_DAY, 23);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 999);
//        System.out.println(c2.getTimeInMillis() + "------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c2.getTime()));
        return c2.getTimeInMillis();
    }

    /**
     * 提前数字的月信息
     *
     * @param num 天数信息
     * @return 月时间信息
     */
    public static Long getAdvanceNumberMonth(int num) {
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.MONTH, -num);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
//        System.out.println(c1.getTimeInMillis() + "----" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c1.getTime()));
        return c1.getTimeInMillis();
    }

    /**
     * 上月的结束时间
     *
     * @return 时间信息
     */
    public static Long getAdvanceNumberEndMonth() {
        Calendar c2 = new GregorianCalendar();
        c2.set(Calendar.DAY_OF_MONTH, 0);
        c2.set(Calendar.HOUR_OF_DAY, 23);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 999);
//        System.out.println(c2.getTimeInMillis() + "-----" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c2.getTime()));
        return c2.getTimeInMillis();
    }
}
