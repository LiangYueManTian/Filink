package com.fiberhome.filink.alarmsetapi.bean;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-26
 */
@Data
public class AlarmForwardRuleDeviceType {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    private String ruleId;

    /**
     * 告警设施id
     */
    private String deviceTypeId;

}
