package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:11 2019/2/27 0027
 */
@Data
public class AlarmLevelStatisticsReq {

    /**
     * 区域
     */
    private String areaId;

    /**
     * 紧急告警数量
     */
    private Integer urgentAlarmCount;

    /**
     * 主要告警数量
     */
    private Integer mainAlarmCount;

    /**
     * 次要告警数量
     */
    private Integer minorAlarmCount;

    /**
     * 提示告警数量
     */
    private Integer hintAlarmCount;

}
