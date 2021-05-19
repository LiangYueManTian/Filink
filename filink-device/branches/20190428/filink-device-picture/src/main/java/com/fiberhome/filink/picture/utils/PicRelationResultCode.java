package com.fiberhome.filink.picture.utils;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 图片返回类
 * @author chaofanrong@wistronits.com
 * @date 2019/3/19 10:56
 */

public class PicRelationResultCode extends ResultCode {

    /**
     * 设施图片code
     */
    public static final Integer DEVICE_PIC_CODE = 130300;

    /**
     * 设施图片参数错误
     */
    public static final Integer PIC_PARAM_ERROR = 1301302;

    /**
     * 用户服务异常
     */
    public static final Integer USER_SERVER_ERROR  = 130210;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 210503;

    /**
     * 任务不存在
     */
    public static final Integer TASK_DOES_NOT_EXIST = 210502;

    /**
     * 实景图
     */
    public static final String DEVICE_PIC_RESOURCE_NAME_LIVE = "实景图";

    /**
     * 图片上传失败
     */
    public static final Integer PIC_UPLOAD_FAIL = 1301303;

    /**
     * 图片上传超过最大张数
     */
    public static final Integer UPLOAD_FILE_OVER_THE_MAX_NUM = 1301304;

    /**
     * 图片上传超过最大size
     */
    public static final Integer UPLOAD_FILE_OVER_THE_MAX_SIZE = 1301305;

    /**
     * 图片上传类型错误
     */
    public static final Integer UPLOAD_PIC_TYPE_FAIL = 1301306;

    /**
     * 图片上传失败
     */
    public static final Integer UPLOAD_PIC_FAIL = 1301307;

    /**
     * 图片上传类型错误
     */
    public static final Integer SAVE_PIC_INFO_FAIL = 1301308;

}
