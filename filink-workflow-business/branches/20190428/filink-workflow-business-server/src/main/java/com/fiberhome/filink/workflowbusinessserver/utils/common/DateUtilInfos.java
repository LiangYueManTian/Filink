package com.fiberhome.filink.workflowbusinessserver.utils.common;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期帮助类
 *
 * @author hedongwei@wistronits.com
 * @date 2019/4/17 14:16
 */

public class DateUtilInfos {

    /**
     * 获取两个日期相差的月份
     *
     * @param startTime 时间
     * @param endTime   时间
     * @return 转换时间格式为年月日
     * @author hedongwei@wistronits.com
     * @date 2019/4/17 0:20
     */
    public static int getMonthNum(Date startTime, Date endTime) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startTime);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endTime);
        return (calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR)) * 12 + (calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH));
    }
}
