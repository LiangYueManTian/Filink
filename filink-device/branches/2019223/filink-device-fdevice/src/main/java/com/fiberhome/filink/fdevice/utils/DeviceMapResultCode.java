package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.bean.ResultCode;
/**
 * <P>
 *     首页设施地图配置返回码
 * </P>
 * @author chaofang@wistronits.com
 * @since 2019/1/29
 */
public class DeviceMapResultCode extends ResultCode {

    /**配置参数错误*/
    public static final Integer CONFIG_PARAM_ERROR = 130301;
    /**无参数信息*/
    public static final Integer CONFIG_MESSAGE_EMPTY = 130302;
    /**配置失败*/
    public static final Integer CONFIG_UPDATE_ERROR = 130303;

}
