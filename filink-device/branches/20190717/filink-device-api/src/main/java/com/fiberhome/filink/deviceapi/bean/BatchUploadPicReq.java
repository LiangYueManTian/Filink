package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

/**
 * <p>
 * 批量上传图片请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-19
 */
@Data
public class BatchUploadPicReq {

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
     * 图片后缀
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
     * 图片16进制字符串
     */
    private String hexString;

    /**
     * 创建人
     */
    private String createUser;
}
