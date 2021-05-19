package com.fiberhome.filink.filinkoceanconnectserver.entity.platform;

import lombok.Data;

/**
 * 平台配置类
 * @author CongcaiYu
 */
@Data
public class OceanConfig {
    /**
     * 平台ip和端口
     */
    private String platformAddress;
    /**
     * 该服务接收消息ip和端口
     */
    private String localAddress;
    /**
     * 产品id
     */
    private String appId;
    /**
     * 密钥
     */
    private String secret;
    /**
     * cert证书地址
     */
    private String selfCertPath;
    /**
     * ca证书地址
     */
    private String trustCaPath;
    /**
     * cert证书密码
     */
    private String selfCertPwd;
    /**
     * ca证书密码
     */
    private String trustCaPwd;
}
