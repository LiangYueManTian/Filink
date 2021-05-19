package com.fiberhome.filink.bean;

import lombok.Data;

/**
 * 排序条件
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/19 17:47
 */
@Data
public class SortCondition {

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序规则 asc 升序 desc降序
     */
    private String sortRule;
}
