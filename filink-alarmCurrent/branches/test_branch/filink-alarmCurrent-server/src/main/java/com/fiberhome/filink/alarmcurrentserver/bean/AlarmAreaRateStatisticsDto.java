package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:11 2019/2/27 0027
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmAreaRateStatisticsDto {

    /**
     * 区域告警数量
     */
    private Integer areaAlarmCount;

    /**
     * 区域告警比率
     */
    private String areaAlarmRate;
}
