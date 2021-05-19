package com.fiberhome.filink.elasticsearch;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import org.apache.commons.beanutils.BeanMap;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * queryCondition构造查询条件辅助类
 *
 * @author yuanyao@wistronits.com
 * create on 2019-07-16 17:18
 */
@SuppressWarnings("all")
public class ElasticsearchBuildHelper {

    /**
     * 构造整个QueryCondition
     *
     * @param indexName 索引名称，对应mongo库名
     * @param typeName 类型名称，对面mongo表名
     */
    public static SearchRequest buildQueryCondition(QueryCondition queryCondition,String indexName, String typeName) {
        if (queryCondition == null) {
            throw new RuntimeException("queryCondition不能为空");
        }
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();


        // 分页
        if (queryCondition.getPageCondition() != null) {
            buildPageCondition(queryCondition.getPageCondition(), searchSourceBuilder);
        }

        // 排序条件
        if (queryCondition.getSortCondition() != null) {
            buildSortCondition(queryCondition.getSortCondition(), searchSourceBuilder);
        }

        // 精确过滤条件
        if (queryCondition.getBizCondition() != null) {
            buildBizCondition(queryCondition.getBizCondition(), boolBuilder);
        }

        // 模糊，范围等条件
        if (queryCondition.getFilterConditions() != null && queryCondition.getFilterConditions().size() > 0) {
            buildFilterCondition(queryCondition.getFilterConditions(), boolBuilder);
        }

        searchSourceBuilder.query(boolBuilder);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }



    private static void conditionMapHandler(Map<String, List<FilterCondition>> conditionMaps,
                                            BoolQueryBuilder boolBuilder) {

        // 两个like要是and关系 也就是map中的每一次循环要是must关系 循环完毕要must
        // 每一次循环构造一个QueryBuilders
        for (Map.Entry<String, List<FilterCondition>> entry : conditionMaps.entrySet()) {
            List<FilterCondition> conditions = entry.getValue();

            // 每一次循环构造一个QueryBuilders
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(entry.getKey());
            boolean isLtAndGt = false;
            // 遍历里面的集合
            for (FilterCondition filterCondition : conditions) {
                if (filterCondition == null) {
                    throw new RuntimeException("参数异常");
                }
                String extra = filterCondition.getExtra();
                String operator = filterCondition.getOperator();
                String filterField = filterCondition.getFilterField();
                Object filterValue = filterCondition.getFilterValue();
                if ("LT_AND_GT".equals(extra)) {
                    isLtAndGt = true;
                    // 范围查询 先分map
                    switch (operator) {
                        case "gt":
                            rangeQuery.gt(filterValue);
                            break;
                        case "gte":
                            rangeQuery.gte(filterValue);
                            break;
                        case "lt":
                            rangeQuery.lt(filterValue);
                            break;
                        case "lte":
                            rangeQuery.lte(filterValue);
                            break;
                        default:
                            throw new UnsupportedOperationException("not support operation " + operator);
                    }
                }else {
                    // TODO: 2019-07-25 先试一波 传入queryBuilder
                    buildNotRangeFilterCondition(operator, filterField, filterValue, queryBuilder);
                }
            }
            // 如果范围条件不为空，则添加进查询条件中
            if (isLtAndGt) {
                queryBuilder.must(rangeQuery);
            }

            // 循环完毕要must
            boolBuilder.must(queryBuilder);
        }
    }

    /**
     * 构造不是范围查询的条件
     *  @param operator
     * @param filterField
     * @param filterValue
     * @param boolBuilder
     */
    private static void buildNotRangeFilterCondition(String operator, String filterField, Object filterValue, BoolQueryBuilder boolBuilder) {
        // 模糊查询或者条件查询
        switch (operator) {
            case "like":
                // 区分中英文
                if (filterValue instanceof String && ((String) filterValue).matches("^[A-Za-z0-9]+$")) {
                    boolBuilder.must(QueryBuilders.wildcardQuery(filterField, ("*" + filterValue + "*").toLowerCase()));
                }else {
                    boolBuilder.must(QueryBuilders.matchQuery(filterField, filterValue));
                }
//
//                if (filterField.matches("^[A-Za-z0-9]+$")) {
//                    boolBuilder.must(QueryBuilders.wildcardQuery(filterField, ("*" + filterValue + "*").toLowerCase()));
//                }else {
//                    boolBuilder.must(QueryBuilders.matchQuery(filterField, filterValue));
//                }
                break;
            case "eq":
                boolBuilder.must(QueryBuilders.termQuery(getFilterField(filterField,filterValue), filterValue));
                break;
            case "neq":
                boolBuilder.mustNot(QueryBuilders.termQuery(getFilterField(filterField, filterValue), filterValue));
                break;
            case "gt":
                boolBuilder.must(QueryBuilders.rangeQuery(filterField).gt(filterValue));
                break;
            case "gte":
                boolBuilder.must(QueryBuilders.rangeQuery(filterField).gte(filterValue));
                break;
            case "lt":
                boolBuilder.must(QueryBuilders.rangeQuery(filterField).lt(filterValue));
                break;
            case "lte":
                boolBuilder.must(QueryBuilders.rangeQuery(filterField).lte(filterValue));
                break;
            case "in":
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                // in value是一个集合
                if (filterValue instanceof List && ((List) filterValue).size() > 0) {
                    List filterValues = (List) filterValue;
                    for (Object value : filterValues) {
                        queryBuilder.should(QueryBuilders.termQuery(getFilterField(filterField,value), value));
                    }
                }else {
                    throw new RuntimeException("参数错误，filterValue类型不对" + operator);
                }

                boolBuilder.must(queryBuilder);
                break;
            case "nin":
                // in value是一个集合
                if (filterValue instanceof List && ((List) filterValue).size() > 0) {
                    List filterValues = (List) filterValue;
                    for (Object value : filterValues) {
                        boolBuilder.mustNot(QueryBuilders.termQuery(getFilterField(filterField,value), value));
                    }
                }else {
                    throw new RuntimeException("参数错误，filterValue类型不对" + operator);
                }
                break;
            default:
                throw new UnsupportedOperationException("not support operation " + operator);
        }
    }



    /**
     * filterConditions集合转换成map集合
     * key值相同的为同一组
     *
     * @param filterConditions
     * @return
     */
    @SuppressWarnings("all")
    public static Map<String, List<FilterCondition>> filterConditionListToMap(List<FilterCondition> filterConditions) {
        // 为了范围查询，先过滤为map集合
        // 先把条件list结合根据field转map
        Map<String, List<FilterCondition>> conditionMaps = new HashMap<>();
        for (FilterCondition filterCondition : filterConditions) {
            String filterField = filterCondition.getFilterField();
            if (conditionMaps.containsKey(filterField)) {
                conditionMaps.get(filterField).add(filterCondition);
            } else {
                List<FilterCondition> conditionList = new ArrayList<>();
                conditionList.add(filterCondition);
                conditionMaps.put(filterField, conditionList);
            }
        }
        return conditionMaps;
    }

    /**
     * 复合条件过滤
     * @param filterConditions
     * @param boolBuilder
     */
    private static void buildFilterCondition(List filterConditions, BoolQueryBuilder boolBuilder) {

        // 数据结构转换
        Map<String, List<FilterCondition>> conditionMaps =
                filterConditionListToMap(filterConditions);

        // 数据处理
        conditionMapHandler(conditionMaps,boolBuilder);

    }

    /**
     * 精确条件过滤
     * @param bizCondition
     * @param boolBuilder
     */
    private static void buildBizCondition(Object bizCondition, BoolQueryBuilder boolBuilder) {
        BeanMap beanMap = new BeanMap(bizCondition);
        if (beanMap.size() > 0) {
            for (Map.Entry<Object, Object> entry : beanMap.entrySet()) {
                if (entry.getValue() != null && !"class".equals(entry.getKey())) {
                    boolBuilder.must(QueryBuilders.termQuery(
                            getFilterField((String) entry.getKey(),
                                    entry.getValue()),
                            entry.getValue()));
                }
            }
        }
    }

    /**
     * // 不加.keyword 使用alarmName 匹配不了中文
     * @param filterField
     * @param filterValue
     * @return
     */
    public  static String getFilterField(String filterField,Object filterValue) {
        if (filterValue instanceof Integer || filterValue instanceof Long) {
            return filterField;
        }else {
            return filterField + ".keyword";
        }
    }

    /**
     * 构造排序条件
     * @param sortCondition
     * @param searchSourceBuilder
     */
    private static void buildSortCondition(SortCondition sortCondition, SearchSourceBuilder searchSourceBuilder) {
        String sortField = sortCondition.getSortField();
        String sortRule = sortCondition.getSortRule();
        // 参数校验
        boolean isSort = false;
        if (sortField != null && !"".equals(sortField)) {
            if (sortRule != null && !"".equals(sortRule)) {
                isSort = true;
            }
        }
        // 构造排序条件
        if (isSort) {
            FieldSortBuilder fsb = SortBuilders.fieldSort(sortField);
            fsb.order(SortOrder.fromString(sortRule));
            searchSourceBuilder.sort(fsb);
        }

    }

    /**
     * 构造分页条件
     *
     * @param pageCondition 分页条件
     * @param searchSourceBuilder
     */
    private static void buildPageCondition(PageCondition pageCondition, SearchSourceBuilder searchSourceBuilder) {
        // 开始页码 0，1，2，3
        Integer pageNum = pageCondition.getPageNum();
        // 每页展示条数 10 ， 20 ，50 。。。
        Integer pageSize = pageCondition.getPageSize();

        // 分页
        // 开始条数
        searchSourceBuilder.from((pageNum - 1) * pageSize);
        // 查询条数
        searchSourceBuilder.size(pageSize);
    }

}
