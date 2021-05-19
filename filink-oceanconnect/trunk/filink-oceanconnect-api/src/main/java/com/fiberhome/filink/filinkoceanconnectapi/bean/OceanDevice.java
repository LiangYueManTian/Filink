package com.fiberhome.filink.filinkoceanconnectapi.bean;

import lombok.Data;

/**
 * oceanConnect设施实体
 *
 * @author CongcaiYu
 */
@Data
public class OceanDevice {

    /**
     * 设施id
     */
    private String deviceId;
    /**
     * appId
     */
    private String appId;
    /**
     * 设备信息
     */
    private DeviceInfoDto deviceInfo;
    /**
     * 终端用户id
     */
    private String endUserId;
    /**
     * NB-IoT终端的IMSI
     */
    private String imsi;
    /**
     * 指定设备是否为安全设备
     */
    private boolean isSecure;
    /**
     * 设备唯一标识
     */
    private String nodeId;
    /**
     * 请求中指定psk，则平台使用指定的psk；请求中不指定psk，则由平台生成psk
     */
    private String psk;
    /**
     * 超时时间
     */
    private String timeout;
    /**
     * 验证码
     */
    private String verifyCode;
    /**
     * 设备所属的产品ID
     */
    private String productId;

}
