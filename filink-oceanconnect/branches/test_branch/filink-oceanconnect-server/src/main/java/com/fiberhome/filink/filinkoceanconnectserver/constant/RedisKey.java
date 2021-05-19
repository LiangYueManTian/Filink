package com.fiberhome.filink.filinkoceanconnectserver.constant;

/**
 * redisKey
 *
 * @author CongcaiYu
 */
public class RedisKey {

    /**
     * 协议
     */
    public static final String PROTOCOL_KEY = "oceanProtocol";
    /**
     * 指令缓存
     */
    public static final String CMD_RESEND_BUFFER = "oceanCmdResendBuffer";
    /**
     * 开锁指令缓存
     */
    public static final String UNLOCK_CMD_RESEND_BUFFER = "oceanUnlockCmdResendBuffer";
    /**
     * 文件上传消息通知
     */
    public static final String DEVICE_ADVISE = "oceanDeviceAdvise";
    /**
     * 文件上传
     */
    public static final String PIC_UPLOAD = "oceanPicUpload";
    /**
     * 设备升级数据包
     */
    public static final String DEVICE_UPGRADE = "oceanDeviceUpgrade";
    /**
     * 设施指令时间
     */
    public static final String CMD_TIME = "oceanCmdTime";
    /**
     * 设施离线
     */
    public static final String OFF_LINE = "oceanOffLine";
    /**
     * 设施失联
     */
    public static final String OUT_OF_CONCAT = "oceanOutOfConcat";
    /**
     * 开锁结果推送
     */
    public static final String UNLOCK_PUSH = "oceanUnlockPush";
    /**
     * 部署状态缓存
     */
    public static final String DEPLOY_CMD = "oceanDeployCmd";
    /**
     * 密钥
     */
    public static final String SECRET = "oceanSecret";
    /**
     * 应用token前缀
     */
    public static final String APP_TOKEN_PREFIX = "oceanAppToken";
    /**
     * 升级包
     */
    public static final String UPGRADE_FILE_PREFIX = "oceanUpgradeFilePrefix";
    /**
     * 正在升级设施数量
     */
    public static final String UPGRADE_DEVICE_COUNT = "oceanUpgradeDeviceCount";
    /**
     * 流水号
     */
    public static final String SERIAL_NUM = "oceanSerialNumber";
    /**
     * 过滤相同流水号
     */
    public static final String DEVICE_SERIAL_NUMBER = "oceanDeviceSerialNumber";
    /**
     * app推送密钥
     */
    public static final String ALI_PUSH_KEY = "aliYunPush";
    /**
     * profile
     */
    public static final String PROFILE_KEY = "profile";
    /**
     * 平台地址
     */
    public static final String ADDRESS_KEY = "address";
    /**
     * 人井睡眠
     */
    public static final String SLEEP_TIME = "sleepTime";
    /**
     * 参数下发缓存
     */
    public static final String SET_CONFIG_CMD = "oceanSetConfigCmd";
}
