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
public class AlarmHandleStatisticsDto {

    /**
     * 已清除
     */
    private Integer cleared;

    /**
     * 设备清除
     */
    private Integer deviceCleared;

    /**
     * 未清除
     */
    private Integer nucleared;
}
