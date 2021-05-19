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
public class AlarmNameStatisticsDto {

    /**
     * 撬门
     */
    private Integer pryDoor;

    /**
     * 撬锁
     */
    private Integer pryLock;

    /**
     * 湿度
     */
    private Integer humidity;

    /**
     * 高温
     */
    private Integer highTemperature;

    /**
     * 低温
     */
    private Integer lowTemperature;

    /**
     * 通信中断
     */
    private Integer communicationInterrupt;

    /**
     * 水浸
     */
    private Integer leach;

    /**
     * 未关门
     */
    private Integer notClosed;

    /**
     * 未关锁
     */
    private Integer unLock;

    /**
     * 倾斜
     */
    private Integer lean;

    /**
     * 震动
     */
    private Integer shake;

    /**
     * 电量
     */
    private Integer electricity;

    /**
     * 非法开门
     */
    private Integer violenceClose;

    /**
     * 工单超时
     */
    private Integer orderOutOfTime;

    /**
     * 应急开锁告警
     */
    private Integer emergencyLock;

    /**
     * 非法开盖（内盖）
     */
    private Integer illegalOpeningInnerCover;
}
