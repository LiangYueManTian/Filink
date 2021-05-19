package com.fiberhome.filink.lockserver.constant.control;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 主控错误码 1401**
 *
 * @author CongcaiYu
 */
public class ControlResultCode extends ResultCode {

    /**
     * 设施id为空
     */
    public static final int DEVICE_ID_IS_NULL = 140101;

    /**
     * 配置策略的值为空
     */
    public static final int CONFIG_VALUE_IS_NULL = 140102;

    /**
     * 配置策略指令下发成功
     */
    public static final int SET_CONFIG_SUCCESS = 140103;

    /**
     * 设施没有主控
     */
    public static final int DEVICE_HAS_NO_CONTROL_INFO = 140105;

    /**
     * 主控信息为空
     */
    public static final int CONTROL_IS_NULL = 140106;

    /**
     * 主控id已被使用
     */
    public static final int CONTROL_ID_REUSED = 140107;

    /**
     * 主控名已被同一个设施使用
     */
    public static final int CONTROL_NAME_REUSED = 140108;

    /**
     * 设施的主控数量已到最大值
     */
    public static final int CONTROL_MAX_NUM = 140109;

    /**
     * 参数错误
     */
    public static final int PARAMETER_ERROR = 140110;

    /**
     * 心跳周期必须大于异常心跳周期
     */
    public static final int SET_CONFIG_PARAMS_ERROR = 140111;
}
