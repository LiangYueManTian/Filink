package com.fiberhome.filink.alarmcurrentserver.bean;


import lombok.Data;

/**
 * <p>
 * 告警名片首页的参数
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
public class AlarmSourceLevelParameter {

    /**
     * 告警id
     */
    private String id;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 告警级别
     */
    private String alarmLevel;
}
