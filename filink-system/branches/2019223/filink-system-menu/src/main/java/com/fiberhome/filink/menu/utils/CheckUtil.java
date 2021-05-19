package com.fiberhome.filink.menu.utils;

import com.fiberhome.filink.bean.PageCondition;

/**
 * 检测参数工具类
 * @author qiqizhu@wistronits.com
 * @Date: 2019/2/28 15:59
 */
public class CheckUtil {
    /**
     * 检测pageCondition工具类
     * @param pageCondition 传入参数
     * @return 检测结果
     */
    public static boolean checkPageConditionNull(PageCondition pageCondition) {
        if ( pageCondition.getPageNum() == null || pageCondition.getPageSize() == null) {
            return true;
        }
        return false;
    }
}
