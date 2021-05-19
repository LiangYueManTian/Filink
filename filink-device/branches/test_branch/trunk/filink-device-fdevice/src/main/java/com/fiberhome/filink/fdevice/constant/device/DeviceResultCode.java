package com.fiberhome.filink.fdevice.constant.device;


import com.fiberhome.filink.bean.ResultCode;

/**
 * 设施返回码
 *
 * @Author zepenggao@wistronits.com
 * @Date 2019/1/7
 */
public class DeviceResultCode extends ResultCode {

    /**
     * 设施参数错误
     */
    public static final Integer DEVICE_PARAM_ERROR = 130201;

    /**
     * 设施名称重复
     */
    public static final Integer DEVICE_NAME_SAME = 130202;

    /**
     * 缺少设施id
     */
    public static final Integer DEVICE_ID_LOSE = 130203;

    /**
     * 设施不存在
     */
    public static final Integer DEVICE_NOT_EXIST = 130204;

	/**
	 * 设施存在工单或告警
	 */
	public static final Integer DEVICE_CANNOT_UPDATE = 130205;


    /**
     * 输入参数为空
     */
    public static final Integer EXPORT_PARAM_NULL = 130206;


    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 130207;

    /**
     * 导出超过最大限制
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 130208;

    /**
     * 没有数据导出
     */
    public static final Integer EXPORT_NO_DATA = 130209;

    /**
     * 用户服务异常
     */
    public static final Integer USER_SERVER_ERROR  = 130210;

    /**
     * 当前用户超过最大任务数量
     */
    public static final int EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 130211;

    /**
     * 保存模板出现异常
     */
    public static final int EXCEPTION_WHILE_SAVING_DEVICE_TEMPLATE = 130212;

    /**
     * 设施关联智能标签时不能删除
     */
    public static final int DEVICE_NOT_DELETE_WITH_LABEL = 130213;

}

