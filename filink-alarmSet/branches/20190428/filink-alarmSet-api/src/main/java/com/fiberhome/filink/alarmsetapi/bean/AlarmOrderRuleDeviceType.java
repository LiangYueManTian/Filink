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
public class AlarmOrderRuleDeviceType {

    private static final long serialVersionUID = 1L;

    /**
     * 告警转工单id
     */
    private String ruleId;

    /**
     * 设施类型id
     */
    private String deviceTypeId;

}
