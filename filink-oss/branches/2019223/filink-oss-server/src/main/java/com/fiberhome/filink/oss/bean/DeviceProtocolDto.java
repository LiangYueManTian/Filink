package com.fiberhome.filink.oss.bean;

import lombok.Data;

/**
 * <p>
 *     设施协议文件封装
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-01-12
 */
@Data
public class DeviceProtocolDto {
    /**
     * 设施协议ID（UUID）
     */
    private String protocolId;

    /**
     * 文件下载路径
     */
    private String fileDownloadUrl;
}
