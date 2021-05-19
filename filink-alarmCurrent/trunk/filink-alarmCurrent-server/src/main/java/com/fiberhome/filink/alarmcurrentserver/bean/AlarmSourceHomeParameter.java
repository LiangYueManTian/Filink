package com.fiberhome.filink.alarmcurrentserver.bean;


import lombok.Data;

/**
 * <p>
 * 设施详情告警统计的参数
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
public class AlarmSourceHomeParameter {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 1日 2周 3月
     */
    private int source;

    private String type;
}
