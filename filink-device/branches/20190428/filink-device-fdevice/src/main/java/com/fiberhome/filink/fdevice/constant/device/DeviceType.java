package com.fiberhome.filink.fdevice.constant.device;

/**
 * 设施类型泛型，（以后要考虑动态扩容的问题）
 *
 * @author zepenggao@wistronits.com
 * @Date 2019/1/7
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
     * 分纤箱
     */
    Splitting_Box("SPLITTING_BOX", "150");


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
