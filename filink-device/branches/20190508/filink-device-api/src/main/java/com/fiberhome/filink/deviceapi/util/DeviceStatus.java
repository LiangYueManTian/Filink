package com.fiberhome.filink.deviceapi.util;
/**
 * 设施状态枚举
 *
 * @author chaofang
 * @since 2019/1/19
 */
public enum DeviceStatus {
    /**
     * 未配置
     */
    Unconfigured("UNCONFIGURED", "1"),

    /**
     * 正常
     */
    Normal("NORMAL", "2"),

    /**
     * 告警
     */
    Alarm("ALARM", "3"),

    /**
     * 离线
     */
    Offline("OFFLINE", "4"),

    /**
     * 失联
     */
    Out_Contact("OUT_CONTACT", "5");


    private String desc;
    private String code;

    DeviceStatus(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static String getDesc(String value) {
        for (DeviceStatus dt : DeviceStatus.values()) {
            if (value.equals(dt.code)) {
                return dt.desc;
            }
        }
        return null;
    }
}
