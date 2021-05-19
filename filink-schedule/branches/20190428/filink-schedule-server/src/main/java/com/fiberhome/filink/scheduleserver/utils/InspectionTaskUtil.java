package com.fiberhome.filink.scheduleserver.utils;

/**
 * 巡检任务工具类
 * @author hedongwei@wistronits.com
 * @date 2019/3/29 19:33
 */
public class InspectionTaskUtil {

    /**
     * 巡检时间周期
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 19:41
     * @param taskPeriod 巡检时间周期
     */
    public static int getMonthTime(Integer taskPeriod) {
        //月份
        Integer month = taskPeriod;
        //一个月有多少秒，自己定义一个月有30天的时间
        int hourSecond = 3600;
        hourSecond = hourSecond * 24;
        hourSecond = hourSecond * 30;
        hourSecond = hourSecond * month;
        return hourSecond;
    }
}
