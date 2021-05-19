package com.fiberhome.filink.parameter.constant;
/**
 * <p>
 *    系统参数限制枚举
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-13
 */
public enum SystemParameterLimitEnum {
    /**
     * 消息保留时间
     */
    RETENTION_TIME(1, 10),
    /**
     * 大屏滚动时间间隔
     */
    SCREEN_SCROLL_TIME(10, 60);
    /**
     * 最小值
     */
    private Integer minValue;
    /**
     * 最大值
     */
    private Integer maxValue;
    /**
     * 构造
     */
    SystemParameterLimitEnum(Integer minValue, Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    /**
     * 与最小值、最大值比较
     */
    public boolean checkValue(Integer value) {
        return value == null || value < minValue || value > maxValue;
    }
}
