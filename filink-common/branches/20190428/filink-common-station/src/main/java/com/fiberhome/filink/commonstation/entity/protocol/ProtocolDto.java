package com.fiberhome.filink.commonstation.entity.protocol;

import lombok.Data;

/**
 * 协议文件传输类
 * @author CongcaiYu
 */
@Data
public class ProtocolDto {

    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 文件路径
     */
    private String fileHexData;
}
