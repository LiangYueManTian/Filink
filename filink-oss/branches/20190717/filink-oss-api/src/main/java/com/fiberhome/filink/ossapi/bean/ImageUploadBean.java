package com.fiberhome.filink.ossapi.bean;

import lombok.Data;

/**
 * <p>
 *     图片上传实体类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/25
 */
@Data
public class ImageUploadBean {
    /**
     * 16进制文件内容
     */
    private String fileHexData;
    /**
     * 文件扩展名
     */
    private String fileExtension;
}
