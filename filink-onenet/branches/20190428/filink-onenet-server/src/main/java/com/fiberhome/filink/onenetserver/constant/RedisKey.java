package com.fiberhome.filink.onenetserver.constant;

/**
 * redisKey
 *
 * @author CongcaiYu
 */
public class RedisKey {

    /**
     * 协议
     */
    public static final String PROTOCOL_KEY = "oneNetProtocol";
    /**
     * 指令缓存
     */
    public static final String CMD_RESEND_BUFFER = "oneNetCmdResendBuffer";
    /**
     * 开锁指令缓存
     */
    public static final String UNLOCK_CMD_RESEND_BUFFER = "oneNetUnlockCmdResendBuffer";
    /**
     * 文件上传消息通知
     */
    public static final String DEVICE_ADVISE = "oneNetDeviceAdvise";
    /**
     * 文件上传
     */
    public static final String PIC_UPLOAD = "oneNetPicUpload";
    /**
     * 设备升级数据包
     */
    public static final String DEVICE_UPGRADE = "oneNetDeviceUpgrade";
    /**
     * 流水号
     */
    public static final String SERIAL_NUM = "oneNetSerialNum";
    /**
     * 设施指令时间
     */
    public static final String CMD_TIME = "oneNetCmdTime";
    /**
     * 设施流水号集合
     */
    public static final String DEVICE_SERIAL_NUMBER = "oneNetDeviceSerialNumber";
    /**
     * 设施离线
     */
    public static final String OFF_LINE = "oneNetOffLine";
    /**
     * 设施失联
     */
    public static final String OUT_OF_CONCAT = "oneNetOutOfConcat";
    /**
     * 开锁结果推送
     */
    public static final String UNLOCK_PUSH = "oneNetUnlockPush";
    /**
     * 部署状态缓存
     */
    public static final String DEPLOY_CMD = "oneNetDeployCmd";
    /**
     * 密钥
     */
    public static final String SECRET = "oneNetOceanSecret";
    /**
     * 升级包
     */
    public static final String UPGRADE_FILE_PREFIX = "oneNetUpgradeFilePrefix";
    /**
     * 正在升级设施数量
     */
    public static final String UPGRADE_DEVICE_COUNT = "oneNetUpgradeDeviceCount";
    /**
     *app推送密钥
     */
    public static final String ALI_PUSH_KEY = "oneNetAliYunPush";
    /**
     * 平台地址
     */
    public static final String ADDRESS_KEY = "oneNetAddress";
}
