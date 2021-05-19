package com.fiberhome.filink.alarmsetapi.bean;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
@Data
public class AlarmOrderRuleName {

    private static final long serialVersionUID = 1L;

    /**
     * 告警转工单id
     */
    private String ruleId;

    /**
     * 告警名称id
     */
    private String alarmNameId;

    /**
     * 告警名称
     */
    private AlarmName alarmName;
}
