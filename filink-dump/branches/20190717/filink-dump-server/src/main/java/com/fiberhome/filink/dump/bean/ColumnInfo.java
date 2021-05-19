package com.fiberhome.filink.dump.bean;

import lombok.Data;

/**
 * 列信息实体
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 16:20
 */
@Data
public class ColumnInfo {
    /**
     * 列明
     */
    private String columnName;
    /**
     * 属性名
     */
    private String propertyName;
    /**
     * 是否需要翻译
     */
    private Integer isTranslation = 0;
}
