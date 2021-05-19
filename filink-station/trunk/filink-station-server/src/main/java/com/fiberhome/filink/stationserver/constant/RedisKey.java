package com.fiberhome.filink.stationserver.constant;

/**
 * redisKey
 *
 * @author CongcaiYu
 */
public class RedisKey {

    /**
     * 协议
     */
    public static final String PROTOCOL_KEY = "protocol";

    /**
     * 设施端口
     */
    public static final String DEVICE_KEY = "device";
    /**
     * 指令缓存
     */
    public static final String CMD_RESEND_BUFFER = "cmdResendBuffer";
    /**
     * 开锁指令缓存
     */
    public static final String UNLOCK_CMD_RESEND_BUFFER = "unlockCmdResendBuffer";
    /**
     * 文件上传消息通知
     */
    public static final String DEVICE_ADVISE = "deviceAdvise";
    /**
     * 文件上传
     */
    public static final String PIC_UPLOAD = "picUpload";
    /**
     * 设备升级数据包
     */
    public static final String DEVICE_UPGRADE = "deviceUpgrade";
    /**
     * 流水号
     */
    public static final String SERIAL_NUM = "serialNum";
    /**
     * 设施指令时间
     */
    public static final String CMD_TIME = "cmdTime";
    /**
     * 设施离线
     */
    public static final String OFF_LINE = "offLine";
    /**
     * 设施失联
     */
    public static final String OUT_OF_CONCAT = "outOfConcat";
    /**
     * 开锁结果推送
     */
    public static final String UNLOCK_PUSH = "unlockPush";
    /**
     * 部署状态
     */
    public static final String DEPLOY_CMD = "deployCmd";
    /**
     * 升级包
     */
    public static final String UPGRADE_FILE_PREFIX = "upgradeFilePrefix";
    /**
     * 正在升级设施数量
     */
    public static final String UPGRADE_DEVICE_COUNT = "upgradeDeviceCount";
    /**
     * 设施流水号集合
     */
    public static final String DEVICE_SERIAL_NUMBER = "deviceSerialNumber";
    /**
     * app推送密钥
     */
    public static final String ALI_PUSH_KEY = "aliYunPush";
    /**
     * 主控信息
     */
    public static final String CONTROL_INFO = "controlInfo";
}
