package com.fiberhome.filink.rfid.constant.statistics;
/**
 * 光缆段和纤芯使用状况枚举定义
 *
 * @author congcongsun2
 * @since 2019-06-03
 */
public enum UsageStateEnum {

    /**
     * 已使用
     */
    USED_COUNT("0","used"),

    /**
     * 未使用
     */
    UNUSED_COUNT("1","unused");

    private String value;
    private String label;

    UsageStateEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(String value) {
        String lable = "";
        for (UsageStateEnum item : UsageStateEnum.values()) {
            if (item.getValue().equals(value)) {
                lable = item.label;
            }
        }
        return lable;
    }

    public String getValue() {
        return value;
    }

}
