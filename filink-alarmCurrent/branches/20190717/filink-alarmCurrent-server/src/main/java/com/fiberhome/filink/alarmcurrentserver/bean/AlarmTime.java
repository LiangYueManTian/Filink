package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <P>
 * 时间参数
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmTime {

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;
}
