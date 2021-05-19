package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * tpn告警统计的参数
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
public class AlarmStatisticsParameterInfo {


    /**
     * code
     */
    private String alarmCode;

    /**
     * 名称
     */
    private String alarmName;

    /**
     * 数量
     */
    private Long count;

}
