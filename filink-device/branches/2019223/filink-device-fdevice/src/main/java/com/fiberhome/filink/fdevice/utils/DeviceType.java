package com.fiberhome.filink.fdevice.utils;

/**
 * 设施类型泛型，（以后要考虑动态扩容的问题）
 *
 * @author zepenggao@wistronits.com
 * @Date 2019/1/7
 */
public enum DeviceType {

    /**光交箱*/
    Optical_Box("光交箱", "001"),
     /**人井*/
    Well("人井", "030"),
     /**配线架*/
    Distribution_Frame("配线架", "060"),
     /**接头盒*/
    Junction_Box("接头盒", "090"),
     /**分纤箱*/
    Splitting_Box("分纤箱", "150");


    /**
     * 类型名称
     */
    private String desc;
    /**
     * 类型编码
     */
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
