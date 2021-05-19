package com.fiberhome.filink.picture.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 图片关系表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-13
 */
@TableName("pic_relation_info")
@Data
public class PicRelationInfo extends Model<PicRelationInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("device_pic_id")
    private String devicePicId;

    /**
     * 设施id
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 图片名称
     */
    @TableField("pic_name")
    private String picName;

    /**
     * 图片大小
     */
    @TableField("pic_size")
    private String picSize;

    /**
     * 原图url
     */
    @TableField("pic_url_base")
    private String picUrlBase;

    /**
     * 缩略图url
     */
    @TableField("pic_url_thumbnail")
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
    @TableField("position_base")
    private String positionBase;

    /**
     * 来源（告警或者工单）
     */
    private String resource;

    /**
     * 来源id
     */
    @TableField("resource_id")
    private String resourceId;

    /**
     * 关联告警名称
     */
    @TableField("alarm_name")
    private String alarmName;

    /**
     * 关联工单名称
     */
    @TableField("order_name")
    private String orderName;

    /**
     * 工单类型（0、销账前；1、销账后；-1、巡检）
     */
    @TableField("order_type")
    private String orderType;

    /**
     * 删除标记
     */
    @TableField("is_deleted")
    private String isDeleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("create_user")
    private String createUser;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_user")
    private String updateUser;

    @Override
    protected Serializable pkVal() {
        return this.devicePicId;
    }

    @Override
    public String toString() {
        return "PicRelationInfo{" +
                "devicePicId=" + devicePicId +
                ", deviceId=" + deviceId +
                ", picSize=" + picSize +
                ", picName=" + picName +
                ", picUrlBase=" + picUrlBase +
                ", picUrlThumbnail=" + picUrlThumbnail +
                ", positionBase=" + positionBase +
                ", type=" + type +
                ", resource=" + resource +
                ", resourceId=" + resourceId +
                ", alarmName=" + alarmName +
                ", orderName=" + orderName +
                ", orderType=" + orderType +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", createUser=" + createUser +
                ", updateTime=" + updateTime +
                ", updateUser=" + updateUser +
                "}";
    }
}
