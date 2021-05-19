package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 图片关系表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-28
 */
@Data
public class PicRelationInfo {

    private static final long serialVersionUID = 1L;

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

    /**
     * 图片类型（图片后缀）
     */
    private String type;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经纬度
     */
    private String positionBase;

    /**
     * 来源（告警或者工单）
     */
    private String resource;

    /**
     * 来源id
     */
    private String resourceId;

    /**
     * 关联告警名称
     */
    private String alarmName;

    /**
     * 关联工单名称
     */
    private String orderName;

    /**
     * 工单类型（0、销账前；1、销账后；-1、巡检）
     */
    private String orderType;

    /**
     * 删除标记
     */
    private String isDeleted;

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

    @Override
    public String toString() {
        return "PicRelationInfo{" +
        "devicePicId=" + devicePicId +
        ", deviceId=" + deviceId +
        ", picSize=" + picSize +
        ", type=" + type +
        ", resource=" + resource +
        ", resourceId=" + resourceId +
        ", isDeleted=" + isDeleted +
        ", createTime=" + createTime +
        ", createUser=" + createUser +
        ", updateTime=" + updateTime +
        ", updateUser=" + updateUser +
        "}";
    }
}
