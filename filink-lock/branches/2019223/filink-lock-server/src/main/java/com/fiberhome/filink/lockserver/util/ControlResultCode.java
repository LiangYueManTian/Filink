package com.fiberhome.filink.lockserver.util;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 主控错误码 1401**
 * @author CongcaiYu
 */
public class ControlResultCode extends ResultCode {
    /**
     * 设施id为空code
     */
    public static final int DEVICE_ID_IS_NULL = 140101;
    /**
     * 配置策略为空code
     */
    public static final int CONFIG_VALUE_IS_NULL = 140102;
    /**
     * 配置策略下发成功code
     */
    public static final int SET_CONFIG_SUCCESS = 140103;
    /**
     * 设施无主控信息code
     */
    public static final int DEVICE_HAS_NO_CONTROL_INFO = 140105;
}
