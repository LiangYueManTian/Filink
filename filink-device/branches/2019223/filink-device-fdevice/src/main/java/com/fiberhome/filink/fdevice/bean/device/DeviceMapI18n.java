package com.fiberhome.filink.fdevice.bean.device;

import lombok.Data;

/**
 * <p>
 *     首页设施类型和地图配置国际化实体类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/24
 */

@Data
public class DeviceMapI18n {
    /**配置请求参数错误*/
    public static final String CONFIG_PARAM_ERROR = "configParamError";
    /**数据库配置信息为空*/
    public static final String CONFIG_MESSAGE_EMPTY = "configMessageEmpty";
    /**修改数据库配置信息异常*/
    public static final String CONFIG_UPDATE_ERROR = "configUpdateError";
    /**传入用户参数为空*/
    public static final String CONFIG_USER_PARAM_ERROR = "configUserParamError";
}
