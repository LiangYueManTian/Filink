package com.fiberhome.filink.onenetserver.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * oneNet服务返回值
 * @author chaofang@fiberhome.com
 * @since 2019-04-25
 */
public class OneNetResultCode extends ResultCode {
    /**
     * 参数错误
     */
    public static final int PARAMETER_ERROR = 260101;
    /**
     * 创建失败
     */
    public static final int CREATE_FAILED = 260102;
    /**
     * 删除失败
     */
    public static final int DELETE_FAILED = 260103;
}
