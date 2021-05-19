package com.fiberhome.filink.rfid.enums;

import com.fiberhome.filink.rfid.constant.template.TemplateConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * 模版选择时 编号规则
 *
 * @author liyj
 * @date 2019/5/9
 */
public enum TemplateCodeRuleEnum {
    /**
     * 模板的走向 左上
     */
    TEMPLATE_TREND_LEFT_UP("leftUp", TemplateConstant.TEMPLATE_CODE_RULE_LEFT_UP),
    /**
     * 模板的走向 左下
     */
    TEMPLATE_TREND_LEFT_DOWN("leftDown", TemplateConstant.TEMPLATE_CODE_RULE_LEFT_DOWN),
    /**
     * 模板的走向 右上
     */
    TEMPLATE_TREND_RIGHT_UP("rightUp", TemplateConstant.TEMPLATE_CODE_RULE_RIGHT_UP),
    /**
     * 模板的走向 右下
     */
    TEMPLATE_TREND_RIGHT_DOWN("rightDown", TemplateConstant.TEMPLATE_CODE_RULE_RIGHT_DOWN);

    /**
     * 描述
     */
    private String desc;
    /**
     * 下标
     */
    private String index;

    TemplateCodeRuleEnum(String index, String desc) {
        this.desc = desc;
        this.index = index;
    }

    /**
     * 根据下标获取对应的枚举值
     *
     * @param ordinal 下标
     * @return TemplateCodeRuleEnum 模板枚举
     */
    public static TemplateCodeRuleEnum getTemplateCodeRuleByOrdinal(int ordinal) {
        for (TemplateCodeRuleEnum codeRuleEnum : TemplateCodeRuleEnum.values()) {
            if (codeRuleEnum.ordinal() == ordinal) {
                return codeRuleEnum;
            }
        }

        return null;
    }


    public String getDesc() {
        return I18nUtils.getSystemString(desc);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
