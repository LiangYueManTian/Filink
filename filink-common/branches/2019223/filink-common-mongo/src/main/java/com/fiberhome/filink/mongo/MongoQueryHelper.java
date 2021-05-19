package com.fiberhome.filink.mongo;


import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * mogonDB 工具类
 * @author Mengjun Wen
 * @date 2019/1/7
 */
public class MongoQueryHelper {

    private static final String ASC = "asc";
    private static final String AND = "and";
    private static final String OR = "or";
    /**
     * 转换filterValue
     *
     * @param filterCondition Object
     * @return FilterCondition
     */
    private static FilterCondition convertFilterValue(Object filterCondition) {
        // filterCondition对象
        if (filterCondition instanceof FilterCondition) {
            FilterCondition subFilterCondition = (FilterCondition) filterCondition;
            Object subFilterValue = subFilterCondition.getFilterValue();
            if (subFilterValue instanceof List
                    && ((List) subFilterValue).size() > 0
                    && ((List) subFilterValue).get(0) instanceof Map) {
                List values = (List) subFilterValue;
                List<FilterCondition> conditions = new ArrayList<>();
                for (Object value : values) {
                    FilterCondition condition = convertFilterValue(value);
                    conditions.add(condition);
                    subFilterCondition.setFilterValue(conditions);
                }
            }
            return subFilterCondition;
            // filterCondition同属性的Map对象
        } else if (filterCondition instanceof Map && isFilterCondition((Map) filterCondition)) {
            FilterCondition subFilterCondition = new FilterCondition();
            Map filterConditionMap = (Map) filterCondition;
            subFilterCondition.setFilterField(filterConditionMap.get("filterField") == null
                    ? null : (String) filterConditionMap.get("filterField"));
            subFilterCondition.setOperator(filterConditionMap.get("operator") == null
                    ? null : (String) filterConditionMap.get("operator"));
            subFilterCondition.setExtra(filterConditionMap.get("extra") == null
                    ? null : (String) filterConditionMap.get("extra"));
            Object subFilterValue = filterConditionMap.get("filterValue");
            if (subFilterValue instanceof List
                    && ((List) subFilterValue).size() > 0
                    && ((List) subFilterValue).get(0) instanceof Map) {
                List values = (List) subFilterValue;
                List<FilterCondition> conditions = new ArrayList<>();
                for (Object value : values) {
                    FilterCondition condition = convertFilterValue(value);
                    conditions.add(condition);
                    subFilterCondition.setFilterValue(conditions);
                }
            } else {
                subFilterCondition.setFilterValue(subFilterValue);
            }
            return subFilterCondition;
        } else {
            throw new UnsupportedOperationException(filterCondition + "not support convert to FilterCondition");
        }
    }

    /**
     * 在使用and或者or时，需要将filterValue设置为list，并在list里存放FilterCondition对象，
     * 但是从springmvc映射过来会将对象转换成map，
     * 所以需要转回为FilterCondition对象
     *
     * @param filterConditions 过滤条件集合
     */
    public static void convertFilterConditions(List<FilterCondition> filterConditions) {
        filterConditions = filterConditions.stream().map(MongoQueryHelper::convertFilterValue).collect(Collectors.toList());
    }


    /**
     * 添加分页条件
     *
     * @param query         query
     * @param pageCondition PageCondition
     */
    public static void withPage(Query query, PageCondition pageCondition) {
        int pageNum = pageCondition.getPageNum() > 0 ? pageCondition.getPageNum() - 1 : 0;
        int pageSize = pageCondition.getPageSize() > 0 ? pageCondition.getPageSize() : 50;
        Pageable pageable = new PageRequest(pageNum, pageSize);
        query.with(pageable);
    }


    /**
     * 生成Mongo查询条件Query对象
     *
     * @param query          Query对象
     * @param queryCondition 查询条件
     * @return Query对象
     */
    public static Query buildQuery(Query query, QueryCondition queryCondition) {
        // 获取过滤条件
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        if (!CollectionUtils.isEmpty(filterConditions)) {
            for (FilterCondition filterCondition : filterConditions) {
                query.addCriteria(getCriteria(filterCondition));
            }
        }
        // 获取排序条件
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (null != sortCondition && !StringUtils.isEmpty(sortCondition.getSortField())) {
            if (ASC.equals(sortCondition.getSortRule())) {
                query.with(new Sort(Sort.Direction.ASC, sortCondition.getSortField()));
            } else {
                query.with(new Sort(Sort.Direction.DESC, sortCondition.getSortField()));
            }
        }
        return query;
    }

    /**
     * 根据过滤条件生成Criteria
     *
     * @param filterCondition 过滤条件
     * @return Criteria
     */
    private static Criteria getCriteria(FilterCondition filterCondition) {
        String filterField = filterCondition.getFilterField();
        String operator = filterCondition.getOperator();
        Object filterValue = filterCondition.getFilterValue();
        if (filterValue instanceof List
                && ((List) filterValue).size() > 0
                && ((List) filterValue).get(0) instanceof FilterCondition) {
            List<FilterCondition> values = (List<FilterCondition>) filterValue;
            Criteria result = new Criteria();
            Criteria[] tmpCriterias = new Criteria[values.size()];
            for (int i = 0; i < values.size(); i++) {
                FilterCondition tmpFilter = values.get(i);
                tmpCriterias[i] = getCriteria(tmpFilter);
            }
            // TODO: 2019/1/25 修改判断方式
//            if (!ArrayUtils.isEmpty(tmpCriterias)) {
            if (null != tmpCriterias && tmpCriterias.length > 0) {
                if (AND.equals(operator)) {
                    result.andOperator(tmpCriterias);
                } else if (OR.equals(operator)) {
                    result.orOperator(tmpCriterias);
                }
            }
            return result;
        } else {
            if (filterValue instanceof BigDecimal) {
                filterValue = ((BigDecimal) filterValue).doubleValue();
            }
            switch (operator) {
                case "contains":
                    Pattern pattern = Pattern.compile("\\Q" + filterValue + "\\E");
                    return Criteria.where(filterField).regex(pattern);
                case "like":
                    Pattern patternLike = Pattern.compile("\\Q" + filterValue + "\\E");
                    return Criteria.where(filterField).regex(patternLike);
                case "eq":
                    return Criteria.where(filterField).is(filterValue);
                case "neq":
                    return Criteria.where(filterField).ne(filterValue);
                case "gt":
                    return Criteria.where(filterField).gt(filterValue);
                case "gte":
                    return Criteria.where(filterField).gte(filterValue);
                case "lt":
                    return Criteria.where(filterField).lt(filterValue);
                case "lte":
                    return Criteria.where(filterField).lte(filterValue);
                case "in":
                    return Criteria.where(filterField).in((List) filterValue);
                case "nin":
                    return Criteria.where(filterField).nin((List) filterValue);
                default:
                    throw new UnsupportedOperationException("not support operation " + operator);
            }
        }

    }

    /**
     * 是否可以转为FilterCondition的map
     *
     * @param map Map
     * @return boolean
     */
    private static boolean isFilterCondition(Map map) {
        return map.containsKey("operator")
                && map.containsKey("filterValue");
    }
}
