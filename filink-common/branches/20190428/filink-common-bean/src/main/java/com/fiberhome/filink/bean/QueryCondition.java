package com.fiberhome.filink.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询条件封装
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/28 14:12
 */
@Data
public class QueryCondition<R> implements Serializable {

    /**分页条件*/
    private PageCondition pageCondition;

    /**过滤条件*/
    private List<FilterCondition> filterConditions;

    /**排序条件*/
    private SortCondition sortCondition;

    /**业务条件*/
    private R bizCondition;

}
