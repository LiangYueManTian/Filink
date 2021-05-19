package com.fiberhome.filink.picture.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 批量上传图片
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-19
 */
@Data
public class BatchUploadPic {

    /**
     * 图片名字
     */
    private String picName;

    /**
     * 设施deviceId
     */
    private String deviceId;

    /**
     * 来源（告警或者工单）
     */
    private String resource;

    /**
     * 来源id
     */
    private String resourceId;

    /**
     * 来源类型（销账或巡检或告警）
     */
    private String type;

    /**
     * 关联告警名称
     */
    private String alarmName;

    /**
     * 关联工单名称
     */
    private String orderName;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 经纬度
     */
    private String positionBase;

    /**
     * 图片
     */
    private MultipartFile pic;

}
