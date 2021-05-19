package com.fiberhome.filink.parameter.utils;
/**
 * <p>
 *    大屏显示时间设置枚举
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-27
 */
public enum DisplayTimeEnum {

    /**
     * 本地时间
     */
    LOCAL_TIME("local"),
    /**
     *UTC时间
     */
    UTC_TIME("universal");

    /**value*/
    private String value;

    public String getValue() {
        return value;
    }

    DisplayTimeEnum(String value) {
        this.value = value;
    }

    /**
     * 检查枚举是否含有此值
     * @param value 检查值
     * @return true 含有 false没有
     */
    public static boolean hasValue(String value) {
        for (DisplayTimeEnum timeEnum : DisplayTimeEnum.values()) {
            if (timeEnum.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
