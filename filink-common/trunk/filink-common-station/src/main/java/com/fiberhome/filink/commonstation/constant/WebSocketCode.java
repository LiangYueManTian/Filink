package com.fiberhome.filink.commonstation.constant;

/**
 * webSocket推送消息code
 */
public class WebSocketCode {

    /**
     * 人井开锁成功 用于消息推送
     */
    public static final int WELL_UNLOCKED_SUCCESSFULLY = 2500004;

    /**
     * 开锁失败
     */
    public static final int UNLOCKING_FAILED = 250005;

    /**
     * station,oceanConnect,oneNet开锁结果消息推送
     */
    public static final int NLOCKED_RESULT = 2400101;
}
