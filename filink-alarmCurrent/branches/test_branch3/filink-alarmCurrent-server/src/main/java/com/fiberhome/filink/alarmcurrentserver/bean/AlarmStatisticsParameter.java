package com.fiberhome.filink.alarmcurrentserver.bean;

import java.util.List;
import lombok.Data;

/**
 * <p>
 * 告警统计的参数
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
public class AlarmStatisticsParameter {

    /**
     * 区域
     */
    private List<String> areaList;

    /**
     * 类型
     */
    private List<String> deviceIds;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 名次
     */
    private int topCount;

    /**
     * 告警类型
     */
    private List<String> alarmCodes;

    private String alarmStatisticsName;
}
