package com.fiberhome.filink.fdevice.utils;


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
    public static final Integer DEVICE_PARAM_ERROT = 130201;

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

}

