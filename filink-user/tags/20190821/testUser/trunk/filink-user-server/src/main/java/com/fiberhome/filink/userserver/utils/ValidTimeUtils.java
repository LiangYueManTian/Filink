package com.fiberhome.filink.userserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * <p>
 * 验证用户是否超过有效时间
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 9:37 2019/1/9 0009
 */
public class ValidTimeUtils {

    public static boolean validTime(String validTime, Long endTime) {
        boolean flag = true;
        if (StringUtils.isNotEmpty(validTime)) {
            String type = validTime.substring(validTime.length() - 1);
            String date = validTime.substring(0, validTime.length() - 1);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            int time = Integer.parseInt(date);
            switch (type) {
                case "y":
                    c.add(Calendar.YEAR, -time);
                    break;
                case "m":
                    c.add(Calendar.MONTH, -time);
                    break;
                case "d":
                    c.add(Calendar.DATE, -time);
                    break;
                default:
                    break;
            }
            if (c.getTimeInMillis() > endTime) {
                flag = false;
            }
        }
        return flag;
    }
}
