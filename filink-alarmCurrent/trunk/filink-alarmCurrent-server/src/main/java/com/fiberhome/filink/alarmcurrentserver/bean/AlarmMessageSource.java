package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 发送消息实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmMessageSource {

    private AlarmMessage alarmMessage;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 告警级别
     */
    private String alarmLevel;

    /**
     * 最近发生时间
     */
    private long alarmNearTime;

    /**
     * 责任单位
     */
    private String responsibleDepartment;

    /**
     * 设施类型
     */
    private String alarmSourceTypeId;
}
