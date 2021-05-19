package com.fiberhome.filink.dprotocol.constant;

/**
 * <p>
 *    设施协议静态常量
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-14
 */
public class DeviceProtocolConstants {
    /**国际化信息硬件版本*/
    public static final String HARDWARE_VERSION = "${hardwareVersion}";
    /**国际化信息软件版本*/
    public static final String SOFTWARE_VERSION = "${softwareVersion}";
    /**国际化信息文件大小*/
    public static final String FILE_SIZE = "${size}";
    /**国际化信息文件名称长度*/
    public static final String FILE_NAME_LENGTH = "${nameLength}";
    /**XML硬件版本*/
    public static final String XML_HARDWARE_VERSION = "hardwareVersion";
    /**XML软件版本*/
    public static final String XML_SOFTWARE_VERSION = "softwareVersion";
    /**XML设施协议名称*/
    public static final String PROTOCOL_NAME = "protocolName";
    /**XML设施协议ID*/
    public static final String  PROTOCOL_ID = "protocolId";
    /**新增设施协议日志XMLFunctionCode*/
    public static final String  ADD_FUNCTION_CODE = "2102101";
    /**修改设施协议日志XMLFunctionCode*/
    public static final String  UPDATE_FUNCTION_CODE = "2102102";
    /**删除设施协议日志XMLFunctionCode*/
    public static final String  DELETE_FUNCTION_CODE = "2102103";
    /**软硬件版本长度*/
    public static final Integer VERSION_LENGTH = 32;
}
