package com.fiberhome.filink.alarmsetapi.bean;


import lombok.Data;



/**
 * <p>
 *  当前告警名称设置实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmName  {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 定制级别
     */
    private String alarmLevel;

    /**
     * 告警描述
     */
    private String alarmDesc;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 默认级别
     */
    private String alarmDefaultLevel;

    /**
     * 是否自动确认 0是未确认 1已确认
     */
    private String alarmAutomaticConfirmation;

    /**
     * 是否删除 0是未删除 1是已删除
     */
    private String iSdeleted;


}
