package com.fiberhome.filink.rfid.enums;

/**
 * 盘的 朝向 A面 B面 无
 *
 * @author chaofanrong
 * @date 2019/6/5
 */
public enum DiscSideEnum {

    /**
     * 盘的朝向 A 面
     */
    DISC_SIDE_STATE_A("A面"),
    /**
     * 盘的朝向 B面
     */
    DISC_SIDE_STATE_B("B面"),
    /**
     * 盘的朝向  无
     */
    DISC_SIDE_STATE_NULL("无");

    /**
     * 描述
     */
    private String desc;

    DiscSideEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
