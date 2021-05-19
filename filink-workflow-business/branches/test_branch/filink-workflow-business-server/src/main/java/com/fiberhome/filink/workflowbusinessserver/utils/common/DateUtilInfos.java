package com.fiberhome.filink.workflowbusinessserver.utils.common;

import java.util.*;

/**
 * 日期帮助类
 * @author hedongwei@wistronits.com
 * @date 2019/4/17 14:16
 */

public class DateUtilInfos {

    /**
     * 获取两个日期相差的月份
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 0:20
     * @param startTime 时间
     * @param endTime 时间
     * @return 转换时间格式为年月日
     */
    public static int getMonthNum(Date startTime, Date endTime) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startTime);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endTime);
        return (calEnd.get(Calendar.YEAR)-calStart.get(Calendar.YEAR))*12+(calEnd.get(Calendar.MONTH)-calStart.get(Calendar.MONTH));
    }

    /**
     * 获取周统计时间集合
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:57
     * @param startDate 开始时间
     * @param week 周数
     * @return 获取周统计时间信息
     */
    public static List<Long> getWeekDateList(Long startDate, int week) {
        List<Long> weekList = new ArrayList<>();
        if (week >= 0) {
            Calendar calendar = null;
            for (int i = 0; i <= week; i++) {
                calendar = new GregorianCalendar();
                calendar.setTimeInMillis(startDate);
                calendar.set(Calendar.DAY_OF_WEEK, 2);
                calendar.add(Calendar.DATE, 7 * i);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Long time = calendar.getTimeInMillis();
                weekList.add(time);
            }
        } else {
            return new ArrayList<>();
        }
        return weekList;
    }

    /**
     * 获取统计月份
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:54
     * @param startDate 开始时间
     * @param month 增加月份
     * @return 获取统计月份
     */
    public static List<Long> getMonthDateList(Long startDate, int month) {
        List<Long> monthList = new ArrayList<>();
        if (month >= 0) {
            Calendar calendar = null;
            for (int i = 0; i <= month; i++) {
                calendar = new GregorianCalendar();
                calendar.setTimeInMillis(startDate);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, i);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Long time = calendar.getTimeInMillis();
                monthList.add(time);
            }
        } else {
            return new ArrayList<>();
        }
        return monthList;
    }

    /**
     * 获取统计年份
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 10:00
     * @param startDate 开始时间
     * @param year 年数
     * @return 返回统计年份
     */
    public static List<Long> getYearDateList(Long startDate, int year) {
        List<Long> yearList = new ArrayList<>();
        if (year >= 0) {
            Calendar calendar = null;
            for (int i = 0; i <= year; i++) {
                calendar = new GregorianCalendar();
                calendar.setTimeInMillis(startDate);
                calendar.add(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Long time = calendar.getTimeInMillis();
                yearList.add(time);
            }
        } else {
            return new ArrayList<>();
        }
        return yearList;
    }



    /**
     * 获取时间的月份
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:30
     * @param date 时间戳
     * @return 返回时间的月份
     */
    public static int getDateMonth(Long date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date);
        //获取月份
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 获取时间的年份
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 9:30
     * @param date 时间戳
     * @return 返回时间的年份
     */
    public static int getDateYear(Long date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date);
        //获取年份
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取两个时间相差的周
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 13:55
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @return 获取两个时间相差的周
     */
    public static int getWeekNum(Long startTime, Long endTime) {
        //开始周
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(startTime);
        startDate.set(Calendar.DAY_OF_WEEK, 2);
        startDate.add(Calendar.DATE, 0);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        Long formatWeekDate = startDate.getTimeInMillis();


        //结束周
        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(endTime);
        endDate.set(Calendar.DAY_OF_WEEK, 2);
        endDate.add(Calendar.DATE, 0);
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        Long formatWeekEndDate = endDate.getTimeInMillis();


        //每天毫秒数
        long weekTime = 1000 * 24 * 60 * 60 * 7;
        //开始时间和结束时间相差的毫秒数
        long diffTime = formatWeekEndDate - formatWeekDate;

        long weekCount = diffTime/weekTime;
        int weekInt = new Long(weekCount).intValue();
        return weekInt;
    }

    /**
     * 获取两个日期相差的月份
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 0:20
     * @param startTime 时间
     * @param endTime 时间
     * @return 两个日期相差的月份
     */
    public static int getMonthNum(Long startTime, Long endTime) {
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(startTime);
        //开始年份
        int startYear = startDate.get(Calendar.YEAR);
        //开始月份
        int startMonth = startDate.get(Calendar.MONTH) + 1;

        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(endTime);
        //结束年份
        int endYear = endDate.get(Calendar.YEAR);
        //结束月份
        int endMonth = endDate.get(Calendar.MONTH);
        return (endYear - startYear) * 12 + (endMonth - startMonth);
    }

    /**
     * 获取两个日期相差的年份
     * @author hedongwei@wistronits.com
     * @date  2019/6/18 10:14
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 两个日期相差的年份
     */
    public static int getYearNum(Long startTime, Long endTime) {
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(startTime);
        //开始年份
        int startYear = startDate.get(Calendar.YEAR);

        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(endTime);
        //结束年份
        int endYear = endDate.get(Calendar.YEAR);

        return endYear - startYear;
    }
}
