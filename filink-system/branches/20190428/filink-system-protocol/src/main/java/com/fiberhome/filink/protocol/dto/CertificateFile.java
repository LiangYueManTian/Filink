package com.fiberhome.filink.protocol.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *   通信协议 协议证书文件Bean
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/26
 */
@Data
public class CertificateFile implements Serializable {


    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String fileUrl;

}
