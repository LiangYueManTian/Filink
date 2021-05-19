package com.fiberhome.filink.workflowbusinessserver.utils.common;


import com.fiberhome.filink.bean.FiLinkTimeUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

/**
 * 计算帮助类
 * @author hedongwei@wistronits.com
 * @date 2019/6/28 9:08
 */

public class CalculateUtil {

    /**
     * 计算百分比
     * @author hedongwei@wistronits.com
     * @date  2019/6/28 9:16
     * @param dividend 被除数
     * @param divisor 除数
     * @return 百分比
     */
    public static double calculatePercent(Integer dividend, Integer divisor) {
        double percentDouble = 0;
        //百分比
        float percentFloat =  (float)dividend / (float)divisor * 100;
        //小数点后两位
        Integer scale = 2;
        if (!ObjectUtils.isEmpty(dividend) && !ObjectUtils.isEmpty(divisor)) {
            if (0 == divisor.intValue()) {
                percentDouble = 100;
                return percentDouble;
            }
            //精确百分比到小数点后两位
            percentDouble = new BigDecimal(percentFloat).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return percentDouble;
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/7/18 10:16
     * @param lastDays 剩余天数
     * @return 当前时间加上毫秒数
     */
    public static Long castStatisticalUtil(Long lastDays) {
        Long time = 3600L * 1000L * 24L;
        Long info = lastDays * time;
        Long nowTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
        Long retTime = nowTime + info;
        retTime = retTime / 1000L;
        return retTime;
    }
}
