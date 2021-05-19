package com.fiberhome.filink.log_server.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * @author hedongwei@wistronits.com
 * description 日志返回编码
 * date 2019/1/16 9:44
 */

public class LogResultCode extends ResultCode {

    /**
     * pageCondition对象不能为空编码
     */
    public static final Integer PAGE_CONDITION_NULL = 220101;

    /**
     * 日志编码不能为空
     */
    public static final Integer LOG_ID_NULL = 220102;


    /**
     * 参数为空
     */
    public static final Integer PARAM_NULL = 220103;
}
