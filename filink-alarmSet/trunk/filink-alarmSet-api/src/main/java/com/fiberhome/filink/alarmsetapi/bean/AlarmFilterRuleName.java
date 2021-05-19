package com.fiberhome.filink.alarmsetapi.bean;

import lombok.Data;

/**
 * <p>
 *  告警过滤名称实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-18
 */
@Data
public class AlarmFilterRuleName {

    private static final long serialVersionUID = 1L;

    /**
     * 告警名称id
     */
    private String alarmNameId;

    /**
     * 告警id
     */
    private String ruleId;

    /**
     * 告警名称
     */
    private AlarmName alarmName;
}
