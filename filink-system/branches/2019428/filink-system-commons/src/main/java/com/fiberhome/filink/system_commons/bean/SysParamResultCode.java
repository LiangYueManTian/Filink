package com.fiberhome.filink.system_commons.bean;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 *  系统服务统一参数返回Code
 * </p>
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
public class SysParamResultCode extends ResultCode {

    /**请求参数错误*/
    public static final Integer SYSTEM_PARAM_ERROR = 210701;
    /**系统参数无有效数据*/
    public static final Integer SYSTEM_PARAM_DATA_ERROR = 210702;
}
