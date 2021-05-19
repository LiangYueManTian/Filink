package com.fiberhome.filink.stationserver.util.lockenum;

/**
 * 指令
 * @author CongcaiYu
 */
public class CmdId {
    /**
     * 开锁
     */
    public static final String UNLOCK = "0x2201";
    /**
     * 激活事件
     */
    public static final String ACTIVE = "0x2204";
    /**
     * 休眠
     */
    public static final String SLEEP = "0x2205";
    /**
     * 参数上报
     */
    public static final String PARAMS_UPLOAD = "0x2208";
    /**
     * 配置设施策略
     */
    public static final String SET_CONFIG = "0x2207";
    /**
     * 开锁结果上报
     */
    public static final String OPEN_LOCK_UPLOAD = "0x3201";
    /**
     * 关锁结果上报
     */
    public static final String CLOSE_LOCK_UPLOAD = "0x3202";
    /**
     * 箱门状态变化事件
     */
    public static final String DOOR_STATE_CHANGE = "0x3204";
}
