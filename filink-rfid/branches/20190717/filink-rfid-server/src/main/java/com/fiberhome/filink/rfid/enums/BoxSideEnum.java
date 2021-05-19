package com.fiberhome.filink.rfid.enums;

/**
 * 箱架的 朝向 A面 B面 无
 *
 * @author chaofanrong
 * @date 2019/6/5
 */
public enum BoxSideEnum {

    /**
     * 箱架的朝向 A 面
     */
    BOX_SIDE_STATE_A("A面"),
    /**
     * 箱架的朝向 B面
     */
    BOX_SIDE_STATE_B("B面"),
    /**
     * 箱架的朝向  无
     */
    BOX_SIDE_STATE_NULL("无");

    /**
     * 描述
     */
    private String desc;

    BoxSideEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
