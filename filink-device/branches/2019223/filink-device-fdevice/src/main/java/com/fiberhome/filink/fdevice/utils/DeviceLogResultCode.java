package com.fiberhome.filink.fdevice.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * 设施日志返回编码
 * @author CongcaiYu@wistronits.com
 */
public class DeviceLogResultCode extends ResultCode {

    /**
     * pageCondition对象不能为空编码
     */
    public static final Integer PAGE_CONDITION_NULL = 130401;

    /**
     * 日志编码不能为空
     */
    public static final Integer LOG_ID_NULL = 130402;
}
