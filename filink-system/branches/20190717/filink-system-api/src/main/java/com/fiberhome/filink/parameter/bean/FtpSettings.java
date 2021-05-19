package com.fiberhome.filink.parameter.bean;

import lombok.Data;


/**
 * <p>
 *   FTP服务设置实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-01
 */
@Data
public class FtpSettings {

    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 内网IP地址
     */
    private String innerIpAddress;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 断开（超时）时间
     */
    private Integer disconnectTime;
}
