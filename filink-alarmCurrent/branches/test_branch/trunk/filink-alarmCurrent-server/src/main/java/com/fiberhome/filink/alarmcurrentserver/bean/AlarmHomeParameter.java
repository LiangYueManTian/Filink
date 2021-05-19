package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 首页参数实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:11 2019/2/27 0027
 */
@Data
public class AlarmHomeParameter {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 传递时间
     */
    private int time;

    /**
     * 获取之前时间
     */
    private Long beginTime;
}
