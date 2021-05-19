package com.fiberhome.filink.picture.resp.app;

import lombok.Data;

/**
 * <p>
 * 获取实景图请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-05-16
 */
@Data
public class LivePicInfo {

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

    /**
     * 图片类型（图片后缀）
     */
    private String type;

    /**
     * 经纬度
     */
    private String positionBase;

}
