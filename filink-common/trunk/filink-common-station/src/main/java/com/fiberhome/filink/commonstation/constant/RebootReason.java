package com.fiberhome.filink.commonstation.constant;

/**
 * 唤醒原因
 * @author CongcaiYu
 */
public class RebootReason {

    /**
     * 自启动
     */
    public static final String START_SELF = "0";

    /**
     * 周期检测告警
     */
    public static final String CYCLE_CHECK_ALARM = "1";

    /**
     * 按键唤醒
     */
    public static final String PRESS_REBOOT = "2";

    /**
     * 锁状态变化
     */
    public static final String LOCK_STATE_CHANGE = "3";

    /**
     * 门状态变化
     */
    public static final String DOOR_STATE_CHANGE = "4";

    /**
     * 震动
     */
    public static final String SHAKE = "5";

    /**
     * 水浸
     */
    public static final String LEACH = "6";
}
