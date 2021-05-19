package com.fiberhome.filink.rfid.enums;

/**
 * 模版的 朝向 A面 B面 无
 *
 * @author liyj
 * @date 2019/5/9
 */
public enum TemplateSideEnum {

    /**
     * 模板的朝向 A 面
     */
    TEMPLATE_SIDE_STATE_A("A面"),
    /**
     * 模板的朝向 B面
     */
    TEMPLATE_SIDE_STATE_B("B面"),
    /**
     * 模板的朝向  无
     */
    TEMPLATE_SIDE_STATE_NULL("无");

    /**
     * 描述
     */
    private String desc;

    TemplateSideEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
