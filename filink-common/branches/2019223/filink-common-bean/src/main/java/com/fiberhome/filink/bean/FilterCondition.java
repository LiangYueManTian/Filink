package com.fiberhome.filink.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 过滤条件
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/19 17:45
 */
@Data
public class FilterCondition implements Serializable {

    /**过滤字段*/
    private String filterField;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 过滤条件值
     */
    private Object filterValue;

    /**
     * 附加条件
     */
    private String extra;
}
