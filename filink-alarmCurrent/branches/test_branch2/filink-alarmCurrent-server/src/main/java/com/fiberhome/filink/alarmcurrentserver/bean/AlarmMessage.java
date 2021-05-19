package com.fiberhome.filink.alarmcurrentserver.bean;


import lombok.Data;

/**
 * <p>
 * 发送指示灯消息实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmMessage {

    /**
     * 告警级别
     */
    private String alarmLevel;

    /**
     * 颜色
     */
    private String alarmColor;

    /**
     * 提示音
     */
    private String prompt;

    /**
     * 提示音 0是否 1是有
     */
    private String isPrompt;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 是否过滤 0表示过滤之前 1表示过滤之后
     */
    private String filter;

    /**
     * 告警级别编码
     */
    private String alarmLevelCode;
}
