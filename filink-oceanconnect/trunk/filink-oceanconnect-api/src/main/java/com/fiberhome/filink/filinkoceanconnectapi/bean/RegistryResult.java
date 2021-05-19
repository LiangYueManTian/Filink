package com.fiberhome.filink.filinkoceanconnectapi.bean;

import lombok.Data;

/**
 * 注册设施返回值
 * @author CongcaiYu
 */
@Data
public class RegistryResult {

    /**
     * 设施id
     */
    private String deviceId;
    /**
     * psk码
     */
    private String psk;
    /**
     * 验证码有效时间
     */
    private Integer timeout;
    /**
     * 验证码
     */
    private String verifyCode;
}
