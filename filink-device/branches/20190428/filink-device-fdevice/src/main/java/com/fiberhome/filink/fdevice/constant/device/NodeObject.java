package com.fiberhome.filink.fdevice.constant.device;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/18 18:49
 * @Description: com.fiberhome.filink.fdevice.constant.device
 * @version: 1.0
 */
public enum NodeObject {
    /**
     * 无源锁
     */
    PASSIVE_LOCK("PASSIVE_LOCK", "0"),

    /**
     * 机械锁芯
     */
    MECHANICAL_LOCK_CYLINDER("MECHANICAL_LOCK_CYLINDER", "1"),

    /**
     * 电子锁芯
     */
    ELECTRONIC_LOCK_CYLINDER("ELECTRONIC_LOCK_CYLINDER", "2");


    private String desc;
    private String code;

    NodeObject(String desc, String code) {
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
        for (NodeObject no : NodeObject.values()) {
            if (no.code.equals(value)) {
                return no.desc;
            }
        }
        return null;
    }
}
