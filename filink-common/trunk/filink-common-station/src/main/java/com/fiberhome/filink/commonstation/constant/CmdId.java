package com.fiberhome.filink.commonstation.constant;

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
     * 文件类信息通知
     */
    public static final String FILE_ADVISE = "0x2202";
    /**
     * 文件数据上传
     */
    public static final String FILE_UPLOAD = "0x2203";
    /**
     * 激活事件
     */
    public static final String ACTIVE = "0x2204";
    /**
     * 休眠
     */
    public static final String SLEEP = "0x2205";
    /**
     * 心跳
     */
    public static final String HEART_BEAT = "0x2206";
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

    /**
     * 升级成功事件
     */
    public static final String UPGRADE_SUCCESS = "0x3203";
    /**
     * 电子锁升级控制
     */
    public static final String UPGRADE_ADVISE = "0x220a";
    /**
     * 电子锁升级数据
     */
    public static final String UPGRADE_DATA = "0x220b";
    /**
     * 部署状态
     */
    public static final String DEPLOY_STATUS = "0x220c";
    /**
     * 人井心跳
     */
    public static final String WEII_HEART_BEAT = "0x215a";
}
