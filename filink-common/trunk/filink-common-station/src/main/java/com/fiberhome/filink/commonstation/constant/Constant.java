package com.fiberhome.filink.commonstation.constant;

/**
 * 常量类
 * @author CongcaiYu
 */
public class Constant {

    /**
     * 分隔符
     */
    public static final String SEPARATOR = "-";

    /**
     * 开始升级
     */
    public static final String START_UPDATE = "1";

    /**
     * 取消升级
     */
    public static final String CACEL_UPDATE = "2";

    /**
     * 开锁结果推送
     */
    public static final String UNLOCK_RESULT = "unlock";
    /**
     * 告警恢复
     */
    public static final String CANCEL_ALARM = "1";
    /**
     * 告警
     */
    public static final String ALARM = "2";
    /**
     * 同步
     */
    public static final String SYNC = "1";
    /**
     * 每个升级包最大字节数
     */
    public static final int MAX_UPGRADE_DATA = 900;
    /**
     * 最大升级数量
     */
    public static final int MAX_UPGRADE_COUNT = 200;
    /**
     * udp指令重发job
     */
    public static final String UDP_RESEND_JOB_NAME = "udpCmdResendJob";
    /**
     * udp指令重发group
     */
    public static final String UDP_RESEND_GROUP_NAME = "udpCmdResendGroup";
    /**
     * oceanConnect指令重发job
     */
    public static final String OCEAN_CONNECT_RESEND_JOB_NAME = "oceanConnectResendJob";
    /**
     * oceanConnect指令重发group
     */
    public static final String OCEAN_CONNECT_RESEND_GROUP_NAME = "oceanConnectResendGroup";
    /**
     * oneNet指令重发job
     */
    public static final String ONE_NET_RESEND_JOB_NAME = "oneNetResendJob";
    /**
     * oneNet指令重发group
     */
    public static final String ONE_NET_RESEND_GROUP_NAME = "oneNetResendGroup";
    /**
     * udp重发任务类型
     */
    public static final String UDP_RESEND_TYPE = "1";
    /**
     * oceanConnect重发任务类型
     */
    public static final String OCEAN_CONNECT_RESEND_TYPE = "2";
    /**
     * oneNet重发任务类型
     */
    public static final String ONE_NET_RESEND_TYPE = "3";
    /**
     * app推送标题
     */
    public static final String TITLE = "unlock result";
    /**
     * 推送消息类型
     */
    public static final String MESSAGE_TYPE = "MESSAGE";
    /**
     * 激活状态
     */
    public static final String ACTIVE_STATUS = "1";
    /**
     * 休眠状态
     */
    public static final String SLEEP_STATUS = "0";
    /**
     * 升级包文件后缀
     */
    public static final String UPGRADE_FILE_SUFFIX = ".zip";
    /**
     * 事件附加信息占位符
     */
    public static final String REMARK_PLACEHOLDER = "${num}";
    /**
     * 开状态
     */
    public static final String OPEN_STATE = "1";
    /**
     * 关状态
     */
    public static final String CLOSE_STATE = "2";
    /**
     * 升级成功
     */
    public static final String UPGRADE_SUCCESS = "0";
    /**
     * 升级失败
     */
    public static final String UPGRADE_FAIL = "1";
    /**
     * 开锁成功
     */
    public static final String UNLOCK_SUCCESS = "1";
    /**
     * 开锁失败
     */
    public static final String UNLOCK_FAIL = "0";
    /**
     * pda升级
     */
    public static final String PDA_UPGRADE = "0";
    /**
     * 网管平台升级
     */
    public static final String PLATFORM_UPGRADE = "1";
    /**
     * 电信平台升级
     */
    public static final String OCEAN_CONNECT_UPGRADE = "2";
    /**
     * 移动平台升级
     */
    public static final String ONE_NET_UPGRADE = "3";
}
