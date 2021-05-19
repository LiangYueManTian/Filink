package com.fiberhome.filink.lockserver.enums;

/**
 * <p>
 * 平台类型枚举值
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/6
 */
public enum PlatFormType {
    /***
     * 电信云
     */
    OceanConnect("0"),

    /**
     * 移动云
     */
    OneNet("1"),

    /**
     * 私有tcp
     */
    TCP("2"),

    /**
     * 私有udp
     */
    UDP("3");

    PlatFormType(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }


}
