package com.fiberhome.filink.parameter.utils;
/**
 * <p>
 *    FTP参数枚举
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-27
 */
public enum FtpNumEnum {
    /**
     * 长
     */
    LENGTH(1, 32),
    /**
     * 宽
     */
    PORT(0, 65535),
    /**
     * 大小
     */
    DISCONNECT_TIME(5, 20);
    /**minValue*/
    private Integer minValue;
    /**maxValue*/
    private Integer maxValue;

    FtpNumEnum(Integer minValue, Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * 校验值是否在数值范围内
     * @param value 校验值
     * @return true 不在 false在
     */
    public boolean checkValue(Integer value) {
        return value == null || value < minValue || value > maxValue;
    }
}
