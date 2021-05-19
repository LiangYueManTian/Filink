package com.fiberhome.filink.oss.bean;

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
     * ID
     */
    private String fileId;
    /**
     * 文件路径
     */
    private String fileUrl;
}
