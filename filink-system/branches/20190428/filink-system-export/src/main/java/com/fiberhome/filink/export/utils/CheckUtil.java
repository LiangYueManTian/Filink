package com.fiberhome.filink.export.utils;

import com.fiberhome.filink.bean.PageCondition;

/**
 * @author qiqizhu@wistronits.com
 * @Date:  2019/2/28 15:59
 * 校验工具类
 */
public class CheckUtil {
    /**
     * 校验pageCondition
     * @param pageCondition 校验字段
     * @return 校验结果
     */
    public static boolean checkPageConditionNull(PageCondition pageCondition) {
        if ( pageCondition.getPageNum() == null || pageCondition.getPageSize() == null) {
            return true;
        }
        return false;
    }
}
