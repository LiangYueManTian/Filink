package com.fiberhome.filink.system_commons.utils;
/**
 * <p>
 *    系统服务统一参数所有类型与Redis Key值枚举类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
public enum ParamTypeRedisEnum {
    /**
     * 账号安全策略
     */
    ACCOUNT("ACCOUNT_SECURITY_STRATEGY", "0"),
    /**
     * 密码安全策略
     */
    PASSWORD("PASSWORD_SECURITY_STRATEGY", "1"),
    /**
     * HTTP通信协议
     */
    HTTP_PROTOCOL("HTTP_PROTOCOL", "2"),
    /**
     * HTTPS通信协议
     */
    HTTPS_PROTOCOL("HTTPS_PROTOCOL", "3"),
    /**
     * WEBSERVICE通信协议
     */
    WEBSERVICE_PROTOCOL("WEBSERVICE_PROTOCOL", "4"),
    /**
     * 短信服务配置
     */
    MESSAGE("SYSTEM_MESSAGE", "5"),
    /**
     * 邮箱服务配置
     */
    MAIL("SYSTEM_MAIL", "6"),
    /**
     * 消息通知设置
     */
    MESSAGE_NOTIFICATION("SYSTEM_MESSAGE_NOTIFICATION", "7"),
    /**
     * 推送服务配置
     */
    MOBILE_PUSH("SYSTEM_MOBILE_PUSH", "8"),
    /**
     * 大屏显示
     */
    DISPLAY_SETTINGS("DISPLAY_SETTINGS","9"),
    /**
     * FTP
     */
    FTP_SETTINGS("FTP_SETTINGS","10");
    /**Redis key*/
    private String key;
    /**类型*/
    private String type;

    ParamTypeRedisEnum(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    /**
     * 获取对应Redis Key值
     * @param type 类型
     * @return Key值
     */
    public static String getKeyByType(String type) {
        String key = "";
        for (ParamTypeRedisEnum value : ParamTypeRedisEnum.values()) {
            if (value.getType().equals(type)) {
                key = value.getKey();
                break;
            }
        }
        return key;
    }

    /**
     * 判断是否有该类型
     *
     * @param type 类型
     * @return true 有 false 没有
     */
    public static boolean hasType(String type) {
        for (ParamTypeRedisEnum value : ParamTypeRedisEnum.values()) {
            if (value.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
