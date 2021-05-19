package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 分组的实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:11 2019/2/27 0027
 */
@Data
public class AlarmStatisticsGroupInfo {

    /**
     * 分组的级别
     */
    private String groupLevel;

    /**
     * 分组的地区
     */
    private String groupArea;

    /**
     * 分组的数量
     */
    private Integer groupNum;

}
