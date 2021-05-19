package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *  当前告警用户信息
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmStatus {

    /**
     * 当前告警信息
     */
    private List<AlarmCurrent> alarmCurrents;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * token信息
     */
    private String token;
}
