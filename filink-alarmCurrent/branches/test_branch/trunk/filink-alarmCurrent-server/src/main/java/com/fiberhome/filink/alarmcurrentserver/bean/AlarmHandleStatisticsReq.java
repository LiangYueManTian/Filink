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
public class AlarmHandleStatisticsReq {

    /**
     * 区域
     */
    private String areaId;
    /**
     * 已清除
     */
    private Integer cleared;

    /**
     * 未清除
     */
    private Integer nucleared;
}
