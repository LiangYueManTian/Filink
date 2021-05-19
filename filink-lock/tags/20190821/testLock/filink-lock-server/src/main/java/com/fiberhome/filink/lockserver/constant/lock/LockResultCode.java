package com.fiberhome.filink.lockserver.constant.lock;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 电子锁 1402**
 *
 * @author CongcaiYu
 */
public class LockResultCode extends ResultCode {


    /**
     * 锁芯id已被使用
     */
    public static final int LOCK_CODE_REUSED = 140201;

    /**
     * 二维码已被使用
     */
    public static final int QR_CODE_REUSED = 140202;

    /**
     * 二维码信息错误
     */
    public static final int QR_CODE_ERROR = 140203;

    /**
     * 系统异常
     */
    public static final int SYSTEM_ERROR = 140250;

    /**
     * 没有开锁权限
     */
    public static final int OPEN_LOCK_REFUSED = 140204;
    /**
     * 没有数据权限
     */
    public static final int ACCESS_DENY = 140205;
    /**
     * 一个设施的门编号重复
     */
    public static final int DEVICE_DOOR_NUM_REUSED = 140206;
    /**
     * 一个设施的门编号不存在
     */
    public static final int DEVICE_DOOR_NUM_ERROR = 140207;
    /**
     * 门磁映射关系错误
     */
    public static final int SENSOR_LIST_ERROR = 140208;

    /**
     * 设施的映射关系数量已到最大值
     */
    public static final int DOOR_MAX_NUM = 140209;

    /**
     * 一个设施的门名称重复使用
     */
    public static final int DEVICE_DOOR_NAME_REUSED = 140210;


}
