package com.fiberhome.filink.ossapi.bean;

import lombok.Data;

/**
 * <p>
 *     图片路径实体类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/25
 */
@Data
public class ImageUrl {
    /**
     * 原图路径
     */
    private String originalUrl;
    /**
     * 缩略图路径
     */
    private String thumbUrl;
}
