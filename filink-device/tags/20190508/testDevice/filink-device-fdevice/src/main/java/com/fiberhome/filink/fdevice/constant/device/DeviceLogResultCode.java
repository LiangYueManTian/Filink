package com.fiberhome.filink.fdevice.constant.device;


import com.fiberhome.filink.bean.ResultCode;

/**
 * 设施日志返回编码
 * @author CongcaiYu@wistronits.com
 */
public class DeviceLogResultCode extends ResultCode {

    /**
     * pageCondition对象不能为空编码
     */
    public static final Integer PAGE_CONDITION_NULL = 130301;

    /**
     * 日志编码不能为空
     */
    public static final Integer LOG_ID_NULL = 130302;

    /**
     * 导出参数为空
     */
    public static final Integer EXPORT_PARAM_NULL = 130303;


    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 130304;

    /**
     * 导出超过最大限制
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 130305;

    /**
     * 没有数据导出
     */
    public static final Integer EXPORT_NO_DATA = 130306;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 130307;

    /**
     * 设施日志不存在
     */
    public static final Integer DEVICE_LOG_NOT_EXISTED = 130308;

}
