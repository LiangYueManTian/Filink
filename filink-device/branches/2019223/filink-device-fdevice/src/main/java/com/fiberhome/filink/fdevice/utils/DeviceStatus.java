package com.fiberhome.filink.fdevice.utils;
/**
 * 设施状态枚举
 *
 * @author chaofang
 * @since 2019/1/19
 */
public enum DeviceStatus {

    /**正常*/
    Normal("正常", "1"),
     /**告警*/
    Alarm("告警", "2"),
     /**未知*/
    Unknown("未知", "3"),
     /**失联*/
    Out_Contact("失联", "4"),
     /**离线*/
    Offline("离线", "5");

    /**状态描述*/
    private String desc;
    /**状态编码*/
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
