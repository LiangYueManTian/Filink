package com.fiberhome.filink.protocol.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * <p>
 * 通信协议结果返回码
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/20
 */
public class ProtocolResultCode extends ResultCode {
    /**
     * 协议系统错误
     */
    public static final Integer PROTOCOL_SYSTEM_ERROR = 210401;


    /**
     * 请求参数格式错误
     */
    public static final Integer PROTOCOL_PRAMS_ERROR = 210402;

}
