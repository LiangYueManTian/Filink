package com.fiberhome.filink.logapi.utils;

import com.fiberhome.filink.clientcommon.utils.ResultCode;

/**
 * @author hedongwei@wistronits.com
 * 日志返回编码
 * 2019/1/16 9:44
 */

public class LogResultCode extends ResultCode {

    /**
     * 新增操作日志异常编码
     */
    public static final Integer ADD_OPERATE_LOG_ERROR = 210131;

    /**
     * 新增安全日志异常编码
     */
    public static final Integer ADD_SECURITY_LOG_ERROR = 210132;

    /**
     * 新增系统日志异常编码
     */
    public static final Integer ADD_SYSTEM_LOG_ERROR = 210133;

    /**
     * pageCondition对象不能为空编码
     */
    public static final Integer PAGE_CONDITION_NULL = 210101;

    /**
     * 日志编码不能为空
     */
    public static final Integer LOG_ID_NULL = 210102;
}
