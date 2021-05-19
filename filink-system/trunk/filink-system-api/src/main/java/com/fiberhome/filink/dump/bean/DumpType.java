package com.fiberhome.filink.dump.bean;

/**
 * <p>
 * 转储策略枚举类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/4
 */
public enum DumpType {

    /**
     * ALARM_DUMP
     */
    ALARM_LOG_DUMP("ALARM_LOG_DUMP","11"),
    /**
     * DEVICE_LOG_DUMP
     */
    DEVICE_LOG_DUMP("DEVICE_LOG_DUMP","12"),
    /**
     * SYSTEM_LOG_DUMP
     */
    SYSTEM_LOG_DUMP("SYSTEM_LOG_DUMP","13");
    /**Redis key*/
    private String key;
    /**类型*/
    private String type;

    DumpType(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

}
