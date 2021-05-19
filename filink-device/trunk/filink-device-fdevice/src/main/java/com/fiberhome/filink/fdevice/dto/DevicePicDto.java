package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 图片关系表
 *
 * @author zhaoliang
 * @since 2019-05-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DevicePicDto {

    /**
     * 主键id
     */
    private String devicePicId;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片大小
     */
    private String picSize;

    /**
     * 原图url
     */
    private String picUrlBase;

    /**
     * 缩略图url
     */
    private String picUrlThumbnail;

}
