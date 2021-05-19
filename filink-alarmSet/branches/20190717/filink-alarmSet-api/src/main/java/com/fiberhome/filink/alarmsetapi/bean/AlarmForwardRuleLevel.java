package com.fiberhome.filink.alarmsetapi.bean;

import lombok.Data;

/**
 * <p>
 *  告警远程通知的告警级别实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-02
 */
@Data
public class AlarmForwardRuleLevel {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    private String ruleId;

    /**
     * 告警级别id
     */
    private String alarmLevelId;

    /**
     * 告警级别
     */
    private AlarmLevel alarmLevel;
}
