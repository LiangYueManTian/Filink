package com.fiberhome.filink.rfid.constant.statistics;

/**
 * 光缆级别枚举定义
 *
 * @author congcongsun2
 * @since 2019-05-31
 */
public enum OpticalCableLevelEnum {

    /**
     * 本地接入-主干光缆
     */
    TRUNK_COUNT("0", "trunkCount"),

    /**
     * 本地接入-末端光缆
     */
    TERMINAL_COUNT("1", "terminalCount"),
    /**
     * 一级干线
     */
    STAIR_COUNT("2", "stairCount"),

    /**
     * 二级干线数量
     */
    SECONDARY_COUNT("3", "secondaryCount"),
    /**
     * 本地中继数量
     */
    RELAY_COUNT("4", "relayCount"),

    /**
     * 本地核心数量
     */
    CORE_COUNT("5", "coreCount"),
    /**
     * 本地汇聚数量
     */

    CONVERGE_COUNT("6", "convergeCount"),
    /**
     * 汇接层光缆
     */
    TANDEM_COUNT("7", "tandemCount"),
    /**
     * 联络光缆
     */
    CONTACT_COUNT("8", "contactCount"),
    /**
     * 局内光缆
     */
    INTERNAL_COUNT("9", "internalCount");


    private String value;
    private String label;

    OpticalCableLevelEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(String value) {
        String label = "";
        for (OpticalCableLevelEnum item : OpticalCableLevelEnum.values()) {
            if (item.getValue().equals(value)) {
                label = item.label;
            }
        }
        return label;
    }

    public String getValue() {
        return value;
    }
}
