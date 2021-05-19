package com.fiberhome.filink.cprotocol.bean;

/**
 * <p>
 * 通信协议类型
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
public enum ProtocolEnum {
    /**
     * HTTP通信协议
     */
    HTTP_PROTOCOL("HTTP_PROTOCOL", "2"),
    /**
     * HTTPS通信协议
     */
    HTTPS_PROTOCOL("HTTPS_PROTOCOL", "3"),
    /**
     * WEBSERVICE通信协议
     */
    WEBSERVICE_PROTOCOL("WEBSERVICE_PROTOCOL", "4");

    /**
     * Redis key
     */
    private String key;
    /**
     * 类型
     */
    private String type;

    ProtocolEnum(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }


}
