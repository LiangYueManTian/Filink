package com.fiberhome.filink.alarmcurrentserver.bean;


import lombok.Data;


/**
 * <p>
 * 短信模板
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmNotices {

    /**
     * 告警名称
     */
    private String alarmname;

    /**
     * 附加信息
     */
    private String alarmdes;

    /**
     * 设施名称
     */
    private String devicename;

    /**
     * 设施类型
     */
    private String devicetype;

    /**
     * 布放区域
     */
    private String region;

    /**
     * 告警时间
     */
    private String alarmtime;
}
