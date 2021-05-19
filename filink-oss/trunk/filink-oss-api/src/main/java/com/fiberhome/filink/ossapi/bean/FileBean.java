package com.fiberhome.filink.ossapi.bean;

import lombok.Data;

/**
 * <p>
 *     文件实体类
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-01-12
 */
@Data
public class FileBean {
    /**
     * 设施协议ID（UUID）
     */
    private String fileId;

    /**
     * 文件下载路径
     */
    private String fileUrl;
}
