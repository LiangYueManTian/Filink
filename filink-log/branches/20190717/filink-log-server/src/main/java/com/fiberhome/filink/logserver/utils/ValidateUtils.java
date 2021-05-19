package com.fiberhome.filink.logserver.utils;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import org.springframework.util.StringUtils;

/**
 * 验证工具类
 *
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 14:46
 */

public class ValidateUtils {

    /**
     * 验证数据长度
     *
     * @param validateData 校验数据
     * @param minLength    最小长度
     * @param maxLength    最大长度
     * @return boolean 校验结果
     * @author hedongwei@wistronits.com
     * @date 2019/2/26 14:51
     */
    public static boolean validateDataLengthResult(String validateData, Integer minLength, Integer maxLength) {
        if (null != validateData && null != minLength && null != maxLength) {
            int dataLength = validateData.length();
            return dataLength >= minLength && dataLength <= maxLength;

        } else {
            return false;
        }
    }


    /**
     * 校验参数是否为空
     *
     * @param validateData 校验的数据
     * @return Result 返回提示信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/26 15:38
     */
    public static boolean validateDataInfoIsEmpty(Object validateData) {
        if (StringUtils.isEmpty(validateData)) {
            return false;
        }
        return true;
    }

    /**
     * 校验参数长度是否满足需求
     *
     * @param validateData 校验的数据
     * @param minLength    最小长度
     * @param maxLength    最大长度
     * @return Result 返回提示信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/26 15:38
     */
    public static boolean validateDataLength(String validateData, Integer minLength,
                                             Integer maxLength) {
        if (!ValidateUtils.validateDataLengthResult(validateData, minLength, maxLength)) {
            return false;
        }
        return true;
    }


    /**
     * 校验pageCondition参数信息
     *
     * @param queryCondition 校验pageCondition参数信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/28 15:37
     */
    public static boolean checkPageCondition(QueryCondition queryCondition) {
        boolean checkResult = true;
        //分页条件不能为空
        if (null == queryCondition.getPageCondition()) {
            checkResult = false;
        } else {
            //pageNumber不能为空
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageNum())) {
                checkResult = false;
            }
            //pageSize不能为空
            if (StringUtils.isEmpty(queryCondition.getPageCondition().getPageSize())) {
                checkResult = false;
            }
        }
        return checkResult;
    }


    /**
     * 查询条件过滤
     *
     * @param queryCondition 查询条件过滤
     * @return 返回查询条件过滤之后的查询条件
     * @author hedongwei@wistronits.com
     * @date 2019/3/7 14:55
     */
    public static QueryCondition filterQueryCondition(QueryCondition queryCondition) {
        // 无排序时的默认排序（当前按照创建时间降序）
        if (null == queryCondition.getSortCondition()) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("createTime");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        } else {
            if (StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
                SortCondition sortCondition = new SortCondition();
                sortCondition.setSortField("createTime");
                sortCondition.setSortRule("desc");
                queryCondition.setSortCondition(sortCondition);
            }
        }
        return queryCondition;
    }

}
