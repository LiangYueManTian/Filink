package com.fiberhome.filink.protocol.bean;

import lombok.Data;

/**
 * <p>
 *     设施协议文件信息Redis实体类
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-02-16
 */
@Data
public class ProtocolVersionBean {

    /**
     * 软件版本
     */
    private String softwareVersion;


    /**
     * 硬件版本
     */
    private String hardwareVersion;
}
