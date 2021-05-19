package com.fiberhome.filink.rfid.constant.statistics;
/**
 * 端口状态枚举定义
 *
 * @author congcongsun2
 * @since 2019-06-10
 */
public enum PortStatusEnum {
    /**
     * 预占端口数量
     */
    ADVANCE_COUNT(0,"advanceCount"),
    /**
     * 占用端口数量
     */
    USED_COUNT(1,"usedCount"),
    /**
     * 空闲端口数量
     */
    UNUSED_COUNT(2,"unusedCount"),
    /**
     * 异常端口数量
     */
    EXCEPTION_COUNT(3,"exceptionCount"),
    /**
     * 虚占端口数量
     */
    VIRTUAL_COUNT(4,"virtualCount");


    private Integer value;
    private String label;

    PortStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        String label = "";
        for (PortStatusEnum item : PortStatusEnum.values()) {
            if (item.getValue().equals(value)) {
                label = item.label;
            }
        }
        return label;
    }
    public Integer getValue() {
        return value;
    }
}
