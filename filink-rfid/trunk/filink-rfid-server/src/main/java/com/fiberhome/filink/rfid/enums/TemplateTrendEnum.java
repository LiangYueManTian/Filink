package com.fiberhome.filink.rfid.enums;

/**
 * 模版的 走向
 *
 * @author liyj
 * @date 2019/5/9
 */
public enum TemplateTrendEnum {
    /**
     * 模板的走向 列优先
     */
    TEMPLATE_TREND_COL("列优"),
    /**
     * 模板的走向 行优先
     */
    TEMPLATE_TREND_ROW("行优");
    /**
     * 描述
     */
    private String desc;

    TemplateTrendEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
