package com.fiberhome.filink.onenetserver.constant;
/**
 * <p>
 *   oneNet平台静态常量实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class OneNetHttpConstants {
    /**oneNet平台协议*/
    public static final String PROTOCOL = "LWM2M";
    /**签名方法，目前仅支持md5\sha1\sha256*/
    public static final String SIGNATURE_METHOD = "sha1";
    /**参数组版本号，日期格式，目前仅支持"2018-10-31"*/
    public static final String VERSION = "2018-10-31";
    /**平台创建设备成功返回码"*/
    public static final String CREATE_SUCCESS = "succ";
}
