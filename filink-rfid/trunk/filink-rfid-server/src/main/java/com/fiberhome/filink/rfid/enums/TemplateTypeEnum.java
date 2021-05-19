package com.fiberhome.filink.rfid.enums;

/**
 * 模板的类型
 *
 * @author liyj
 * @date 2019/5/9
 */
public enum TemplateTypeEnum {
    /**
     * 模版类型-箱
     */
    TEMPLATE_TYPE_BOX,
    /**
     * 模版类型-框
     */
    TEMPLATE_TYPE_FRAME,
    /**
     * 模版类型-盘
     */
    TEMPLATE_TYPE_DISC;

    /**
     * 根据下标来获取枚举
     *
     * @param index index
     * @return TemplateTypeEnum
     */
    public static TemplateTypeEnum getTemplateByIndex(int index) {
        for (TemplateTypeEnum typeEnum : TemplateTypeEnum.values()) {
            if (typeEnum.ordinal() == index) {
                return typeEnum;
            }
        }
        return null;
    }
}
