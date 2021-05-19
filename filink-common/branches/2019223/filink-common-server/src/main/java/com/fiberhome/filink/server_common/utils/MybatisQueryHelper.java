package com.fiberhome.filink.server_common.utils;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;

import java.util.List;

/**
 * Mybaties查询工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/28 14:16
 */
@Deprecated
public class MybatisQueryHelper {

    private static final String CONTAINS = "contains";
    private static final String EQ = "eq";
    private static final String NEQ = "neq";
    private static final String GT = "gt";
    private static final String GTE = "gte";
    private static final String LT = "lt";
    private static final String LTE = "lte";
    /**
     * 构建过滤条件
     *
     * @param queryCondition 参数
     */
    public static void buildQuery(QueryCondition queryCondition) {
        List<FilterCondition> filterConditionList = queryCondition.getFilterConditions();
        // 过滤条件处理
        if (filterConditionList != null && filterConditionList.size() != 0) {
            filterConditionList.forEach(filterCondition -> {
                // 将字段的大写字母变成下划线小写6
                String filterField = filterCondition.getFilterField();
                String upper2LineLower = upper2LineLower(filterField);
                filterCondition.setFilterField(upper2LineLower);
                String operator = filterCondition.getOperator();
                Object filterValue = filterCondition.getFilterValue();

                if (CONTAINS.equals(operator)) {
                    filterCondition.setOperator("like");
                    // 对过滤值中的特殊字符进行处理
                    String value = (String) filterValue;
                    if (hasSpeciaSign(value)) {
                        value = speciaSignHandle(value);
                        filterCondition.setFilterValue(value);
                        filterCondition.setExtra("escape '/'");
                    }
                    filterCondition.setFilterValue("%" + value + "%");
                } else if (EQ.equals(operator)) {
                    // 分为单选和复选
                    if (filterValue instanceof List) {
                        filterCondition.setOperator("in");
                    } else {
                        filterCondition.setOperator("=");
                    }
                } else if (NEQ.equals(operator)) {
                    filterCondition.setOperator("<>");
                } else if (GT.equals(operator)) {
                    filterCondition.setOperator(">");
                } else if (GTE.equals(operator)) {
                    filterCondition.setOperator(">=");
                } else if (LT.equals(operator)) {
                    filterCondition.setOperator("<");
                } else if (LTE.equals(operator)) {
                    filterCondition.setOperator("<=");
                } else {
                    throw new UnsupportedOperationException("not support operation" + operator);
                }
            });
        }
        // 分页条件处理
        PageCondition pageCondition = queryCondition.getPageCondition();
        if (null != pageCondition) {
            Integer pageSize = pageCondition.getPageSize();
            Integer pageNum = pageCondition.getPageNum();
            pageCondition.setBeginNum((pageNum - 1) * pageSize);
        }
        // 排序条件处理
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (null != sortCondition) {
            String sortField = sortCondition.getSortField();
            sortField = upper2LineLower(sortField);
            sortCondition.setSortField(sortField);
        }

    }

    /**
     * 将特殊字符处理
     *
     * @param value 需要处理的值
     * @return 处理后的值
     */
    private static String speciaSignHandle(String value) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = value.toCharArray();
        for (char aChar : chars) {
            if ('%' == aChar || '_' == aChar || '/' == aChar) {
                stringBuilder.append("/").append(aChar);
            } else {
                stringBuilder.append(aChar);
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 判断值是否包含特殊字符
     *
     * @param value 值
     * @return 是否包含 true -是  false -否
     */
    private static boolean hasSpeciaSign(String value) {
        return value.contains("%") || value.contains("_") || value.contains("/");
    }

    /**
     * 把大写字母变成下划线和小写字母
     *
     * @param s 需要转换的字符串
     * @return 转换后的字符串
     */
    private static String upper2LineLower(String s) {
        char[] chars = s.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 'A' && c <= 'Z') {
                stringBuilder.append('_').append(String.valueOf(c).toLowerCase());
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
}
