package com.fiberhome.filink.deviceapi.util;

/**
 * 设施类型泛型
 *
 * @author zepenggao@wistronits.com
 * @Date 2019/6/14
 */
public enum DeviceType {

    /**
     * 光交箱
     */
    Optical_Box("OPTICAL_BOX", "001"),

    /**
     * 人井
     */
    Well("WELL", "030"),

    /**
     * 配线架
     */
    Distribution_Frame("DISTRIBUTION_FRAME", "060"),

    /**
     * 接头盒
     */
    Junction_Box("JUNCTION_BOX", "090"),

    /**
     * 室外柜
     */
    Outdoor_Cabinet("OUTDOOR_CABINET", "210");


    private String desc;
    private String code;

    DeviceType(String desc, String code) {
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
        for (DeviceType dt : DeviceType.values()) {
            if (value.equals(dt.code)) {
                return dt.desc;
            }
        }
        return null;
    }
}
