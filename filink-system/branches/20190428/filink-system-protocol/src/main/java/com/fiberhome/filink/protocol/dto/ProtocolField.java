package com.fiberhome.filink.protocol.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 通信协议  配置字段
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/28
 */
@Data
public class ProtocolField implements Serializable {
    /**
     * 服务接口地址
     */
    private String ip;
    /**
     * 服务端口
     */
    private String port;
    /**
     * 超时时间
     */
    private String maxWait;
    /**
     * 启用连接数限制 1-启用,0-不启用
     */
    private String enabled;
    /**
     * 允许连接数 启用连接数限制生效
     */
    private String maxActive;
    /**
     * 协议证书
     */
    private CertificateFile certificateFile;


}
