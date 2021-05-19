package com.fiberhome.filink.exportapi.bean;

import lombok.Data;

/**
 * 列信息实体
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 14:15
 */
@Data
public class ColumnInfo {
    /**
     * 列名
     */
    private String columnName;
    /**
     * 属性名称
     */
    private String propertyName;
    /**
     * 是否翻译
     */
    private Integer isTranslation = 0;

}
