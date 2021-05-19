package com.fiberhome.filink.alarmcurrentserver.enums;

/**
 * 告警名称枚举类
 *
 * @author weikaun@fiberhome.com
 * create on 2019/6/16 5:39 PM
 */
public enum AlarmNameTypeEnum {

    /**
     * 湿度
     */
    Humidity("humidity", "湿度"),

    /**
     * 电量
     */
    Electricity("electricity", "电量"),

    /**
     * 水浸
     */
    Leach("leach", "水浸"),

    /**
     * 高温
     */
    HighTemperature("temperature", "高温"),

    /**
     * 低温
     */
    LowTemperature("temperature", "低温"),

    /**
     * 倾斜
     */
    Lean("lean", "倾斜");

    private String desc;
    private String code;

    AlarmNameTypeEnum(String desc, String code) {
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

    /**
     * 根据 code 获取 对应的名称
     *
     * @param value code
     * @return 名称
     */
    public static String getDesc(String value) {
        for (AlarmNameTypeEnum dt : AlarmNameTypeEnum.values()) {
            if (value.equals(dt.code)) {
                return dt.desc;
            }
        }
        return null;
    }
}
