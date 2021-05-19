package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.bean.ResultCode;

/**
 * oceanConnect错误码
 *
 * @author CongcaiYu
 */
public class OceanConnectResultCode extends ResultCode {

    /**
     * 参数错误
     */
    public static final int PARAMETER_ERROR = 250001;

    /**
     * 注册失败
     */
    public static final int REGISTRY_FAILED = 250002;

    /**
     * 删除失败
     */
    public static final int DELETE_FAILED = 250003;

    /**
     * 人井开锁成功 用于消息推送
     */
    public static final int WELL_UNLOCKED_SUCCESSFULLY = 2500004;
    /**
     * 开锁失败
     */
    public static final int UNLOCKING_FAILED = 250005;


}
