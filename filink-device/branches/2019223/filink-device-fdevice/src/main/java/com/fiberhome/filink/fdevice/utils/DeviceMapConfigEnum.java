package com.fiberhome.filink.fdevice.utils;

/**
 * <p>
 *     首页地图设施配置枚举类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/24
 */
public enum DeviceMapConfigEnum {
    /**设施配置类型值*/
    DEVICE_CONFIG_TYPE("1"),
    /**地图设施图标尺寸类型值*/
    MAP_CONFIG_TYPE("2"),
    /**设施类型状态值*/
    DEVICE_CONFIG_VALUE("1"),
    /**地图设施图标尺寸值*/
    MAP_CONFIG_VALUE("18-24"),
    /**地图配置设施类型为000*/
    MAP_DEVICE_TYPE("000");

    /**值*/
    private String value;

    DeviceMapConfigEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
