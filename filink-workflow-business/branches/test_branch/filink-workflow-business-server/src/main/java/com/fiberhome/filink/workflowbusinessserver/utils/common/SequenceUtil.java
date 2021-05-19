package com.fiberhome.filink.workflowbusinessserver.utils.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 序列工具类
 * @author hedongwei@wistronits.com
 * @date 2019/4/25 19:01
 */

public class SequenceUtil {

    /**
     * 序列
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 17:48
     * @param top 开始的字符
     * @param end 结束追加的字符
     * @return 对应序列的值
     */
    public static String getSequenceInfo(String top, String end, Date nowDate) {
        //开头
        String sequenceInfo = top;
        //五位随机数
        sequenceInfo += "" + (int)((Math.random()*9+1)*10000);

        //获取时间年月日的值
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        String pattern = "yyyyMMdd";
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        String nowDateString = sf.format(calendar.getTime());

        //获取年(后两位) + 月(两位) + 日(两位)
        nowDateString = nowDateString.substring(2);
        sequenceInfo += nowDateString;
        //后缀
        sequenceInfo += end;
        return sequenceInfo;
    }
}
