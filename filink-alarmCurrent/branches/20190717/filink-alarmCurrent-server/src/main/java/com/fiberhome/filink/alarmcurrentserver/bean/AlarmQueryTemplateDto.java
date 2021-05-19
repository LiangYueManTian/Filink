package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 告警模板Dto实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-09
 */
@Data
public class AlarmQueryTemplateDto extends AlarmQueryTemplate {

    /**
     * 排序的字段名
     */
    private String sortField;

    /**
     * 排序規則
     */
    private String sortRule;
}
