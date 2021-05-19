package com.fiberhome.filink.server_common.utils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.SortCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Mybaties 查询条件解析封装
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/7 20:47
 */
public class MpQueryHelper {

    private static final String EQ = "eq";
    private static final String OR = "or";
    private static final String IN = "in";
    private static final String LIKE = "like";
    private static final String NEQ = "neq";
    private static final String GT = "gt";
    private static final String GTE = "gte";
    private static final String LT = "lt";
    private static final String LTE = "lte";
    private static final String ASC = "asc";
    private static final String DESC = "desc";

    // TODO: 2019/1/7 常量抽取 
    /**
     * 构造分页条件
     * @param queryCondition 查询条件
     * @return page对象
     */
    public static <T> Page MyBatiesBuildPage(QueryCondition<T> queryCondition) {
        // 分页条件封装
        PageCondition pageCondition = queryCondition.getPageCondition();
        if (pageCondition != null) {
            // 开始条数通过显示数量和当前页码计算得来
            Integer pageSize = pageCondition.getPageSize();
            Integer pageNum = pageCondition.getPageNum();
            return new Page<>(pageNum, pageSize);
        }
        return null;
    }

    /**
     * 构造分页查询结果
     * @param page 用于查询的page条件
     * @param count 查询出来的总数
     * @param data 查询结果数据
     * @return 分页封装
     */
    public static PageBean MyBatiesBuildPageBean(Page page, Integer count, Object data) {
        PageBean pageBean = new PageBean();
        // 每一页数量
        pageBean.setSize(page.getSize());
        // 页码
        pageBean.setPageNum(page.getCurrent());
        // 总页数 总条数/每一页数量
        pageBean.setTotalPage(count / page.getSize() + 1);
        // 总条数
        pageBean.setTotalCount(count);
        // 数据
        pageBean.setData(data);
        return pageBean;
    }

    /**
     * 构造业务条件
     * @param queryCondition 查询条件
     * @return 条件封装
     */
    public static <T> EntityWrapper MyBatiesBuildQuery(QueryCondition<T> queryCondition) {
        // 业务条件
        EntityWrapper<T> wrapper = new EntityWrapper<>();
        buildBizCondition(queryCondition, wrapper);
        buildSortCondition(queryCondition, wrapper);
        buildFilterCondition(queryCondition, wrapper);
        return wrapper;
    }

    /**
     * 构造排序条件
     * @param queryCondition 查询条件
     * @param wrapper 条件封装
     * @param <T> 对应实体
     */
    private static <T> void buildFilterCondition(QueryCondition<T> queryCondition, EntityWrapper<T> wrapper) {
        // 过滤条件
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        if (CollectionUtils.isNotEmpty(filterConditions)) {
            filterConditions.forEach(filterCondition -> {
                String filterField = filterCondition.getFilterField();
                String field = upper2LineLower(filterField);
                String operator = filterCondition.getOperator();
                Object filterValue = filterCondition.getFilterValue();
                if (StringUtils.equals(EQ, operator)) {
                    wrapper.eq(field, filterValue);
                }
                if (StringUtils.equals(OR, operator)) {
                    List<?> value = (List<?>) filterValue;
                    for (int i = 0; i < value.size(); i++) {
                        if (i == value.size() - 1) {
                            wrapper.eq(field, value.get(i));
                        }else {
                            wrapper.eq(field, value.get(i)).or();
                        }
                    }
                }
                if (StringUtils.equals(IN, operator)) {
                    Collection<?> value = (Collection<?>) filterValue;
                    wrapper.in(field, (Collection<?>) filterValue);
                }
                if (StringUtils.equals(LIKE, operator)) {
                    wrapper.like(field, (String) filterValue);
                }
                if (StringUtils.equals(NEQ, operator)) {
                    wrapper.ne(field, filterValue);
                }
                if (StringUtils.equals(GT, operator)) {
                    wrapper.gt(field, filterValue);
                }
                if (StringUtils.equals(GTE, operator)) {
                    wrapper.ge(field, filterValue);
                }
                if (StringUtils.equals(LT, operator)) {
                    wrapper.lt(field, filterValue);
                }
                if (StringUtils.equals(LTE, operator)) {
                    wrapper.le(field, filterValue);
                }
            });
        }
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

    /**
     * 构造业务条件
     * @param queryCondition 查询条件
     * @param wrapper 条件封装
     * @param <T> 对应实体
     */
    private static <T> void buildBizCondition(QueryCondition<T> queryCondition, EntityWrapper<T> wrapper) {
        T bizCondition = queryCondition.getBizCondition();
        if (bizCondition != null) {
            wrapper.setEntity(bizCondition);
        }
    }

    /**
     * 构造排序条件
     * @param queryCondition 查询条件
     * @param wrapper 条件封装
     * @param <T> 对应实体
     */
    private static <T> void buildSortCondition(QueryCondition<T> queryCondition, EntityWrapper<T> wrapper) {
        // 排序条件
        // TODO: 2019/1/7 多列排序暂不考虑 因为前端不支持 后续需要直接修改value即可
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition != null
                && StringUtils.isNotEmpty(sortCondition.getSortField())
                && StringUtils.isNotEmpty(sortCondition.getSortRule())) {
            String sortField = sortCondition.getSortField();
            // 转下划线
            String field = upper2LineLower(sortField);
            String sortRule = sortCondition.getSortRule();
            if (StringUtils.equals(ASC, sortRule)) {
                wrapper.orderAsc(Arrays.asList(field));
            } else if (StringUtils.equals(DESC, sortRule)) {
                wrapper.orderDesc(Arrays.asList(field));
            }
        }
    }
}