package com.fiberhome.filink.picture.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 图片常量类
 * @author chaofanrong@wistronits.com
 * @date 2019/3/19 10:56
 */

public class PicRelationConstants extends ResultCode {

    /**
     * 图片来源（告警）
     */
    public static final String PIC_RESOURCE_1 = "1";

    /**
     * 图片来源（工单）
     */
    public static final String PIC_RESOURCE_2 = "2";

    /**
     * 图片来源（实景）
     */
    public static final String PIC_RESOURCE_3 = "3";

    /**
     * 来源类型（销账前）
     */
    public static final Integer PIC_RESOURCE_TYPE_1 = 1;

    /**
     * 来源类型（销账后）
     */
    public static final Integer PIC_RESOURCE_TYPE_2 = 2;

    /**
     * 来源类型（巡检）
     */
    public static final Integer PIC_RESOURCE_TYPE_INSPECTION = 0;

    /**
     * 逻辑删除状态（正常）
     */
    public static final String IS_DELETED_0 = "0";

    /**
     * 逻辑删除状态（删除）
     */
    public static final String IS_DELETED_1 = "1";

    /**
     * 上传图片最大张数
     */
    public static final Integer PIC_UPLOAD_MAX_NUM = 10;

    /**
     * 图片最大100kb
     */
    public static final Integer PIC_UPLOAD_MAX_SIZE = 100*1024;

    /**
     * 设施图片来源（1、告警；2、工单；3、实景图）
     */
    public static final String DEVICE_PIC_RESOURCE_1 = "1";

    /**
     * 设施图片来源（1、告警；2、工单；3、实景图）
     */
    public static final String DEVICE_PIC_RESOURCE_2 = "2";

    /**
     * 设施图片来源（1、告警；2、工单；3、实景图）
     */
    public static final String DEVICE_PIC_RESOURCE_3 = "3";

    /**
     * 图片名字目的（告警）
     */
    public static final String DEVICE_PIC_PURPOSE_ALARM = "A";

    /**
     * 图片名字目的（巡检工单）
     */
    public static final String DEVICE_PIC_PURPOSE_INSPECTION = "D";

    /**
     * 图片名字目的（销账工单）
     */
    public static final String DEVICE_PIC_PURPOSE_CLEAR = "C";

    /**
     * 图片名字目的（实景图）
     */
    public static final String DEVICE_PIC_PURPOSE_LIVE = "B";

    /**
     * 图片名字目的（其他）
     */
    public static final String DEVICE_PIC_PURPOSE_OTHER = "Z";

    /**
     * admin权限
     */
    public static final String PERMISSIONS_ADMIN = "1";

    /**
     * 无权限
     */
    public static final String PERMISSIONS_NOT = "2";

    /**
     * 权限异常
     */
    public static final String PERMISSIONS_ERROR = "3";

    /**
     * 权限正常
     */
    public static final String PERMISSIONS_NORMAL = "4";

}
