package com.fiberhome.filink.workflowbusinessserver.utils.common;

import org.springframework.util.ObjectUtils;

/**
 * 操作工具类
 * @author hedongwei@wistronits.com
 * @date 2019/5/16 9:41
 */

public class OperateUtil {

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 9:44
     * @param operate 获取操作符
     */
    public static String getOperateValue(String operate) {
        if (ObjectUtils.isEmpty(operate)) {
            return "";
        }
        String operateValue = "";
        switch(operate)
        {
            case "gt":
                operateValue = ">";
                break;
            case "gte":
                operateValue = ">=";
                break;
            case "lt":
                operateValue = "<";
                break;
            case "lte":
                operateValue = "<=";
                break;
            case "eq":
                operateValue = "=";
                break;
            default:
                operateValue = "=";
        }
        return operateValue;
    }
}
