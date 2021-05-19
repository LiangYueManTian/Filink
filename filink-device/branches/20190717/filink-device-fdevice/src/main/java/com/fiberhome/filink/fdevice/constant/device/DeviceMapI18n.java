package com.fiberhome.filink.fdevice.constant.device;

import lombok.Data;

/**
 * <p>
 * 首页设施类型和地图配置国际化实体类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/24
 */

@Data
public class DeviceMapI18n {
    /**
     * 配置请求参数错误
     */
    public static final String CONFIG_PARAM_ERROR = "CONFIG_PARAM_ERROR";
    /**
     * 数据库配置信息为空
     */
    public static final String CONFIG_MESSAGE_EMPTY = "CONFIG_MESSAGE_EMPTY";
    /**
     * 修改数据库配置信息异常
     */
    public static final String CONFIG_UPDATE_ERROR = "CONFIG_UPDATE_ERROR";
    /**
     * 传入用户参数为空
     */
    public static final String CONFIG_USER_PARAM_ERROR = "CONFIG_USER_PARAM_ERROR";
}
