package com.fiberhome.filink.gatewaysecurity.constant;

import java.io.Serializable;

/**
 * @Author:qiqizhu@wistronits.com
 * @Date:2019/8/8
 * 返回值i8n常量
 */
public class I18Const implements Serializable{

    /**
     * 登录密码错误
     */
    public static final String LOGIN_PASSWORD_WRONG = "LOGIN_PASSWORD_WRONG";

    /**
     * 用户没有登录
     */
    public static final String USER_NOT_LOGIN = "USER_NOT_LOGIN";

    /**
     * 系统服务异常
     */
    public static final String SYSTEM_SERVICE_ERROR = "SYSTEM_SERVICE_ERROR";

    /**
     * ip没有访问权限
     */
    public static final String IP_NOT_HAVE_PERMISSION = "IP_NOT_HAVE_PERMISSION";

    /**
     * 用戶服务异常
     */
    public static final String USER_SERVICE_ERROR = "USER_SERVICE_ERROR";

    /**
     * 超过最大在线数
     */
    public static final String MORE_THAN_MAXUSERNUMBER = "MORE_THAN_MAXUSERNUMBER";

    /**
     * 用户已被禁用
     */
    public static final String USER_HAS_FORBIDDEN = "USER_HAS_FORBIDDEN";

    /**
     * licene以过期
     */
    public static final String LICENE_HAS_EXPIRATION = "LICENE_HAS_EXPIRATION";
    /**
     * license未开始
     */
    public static final String LICENSE_NOT_STARTED = "LICENSE_NOT_STARTED";

    /**
     * 用户已被锁定
     */
    public static final String USER_HAS_LOCKED = "USER_HAS_LOCKED";

    /**
     * license为空
     */
    public static final String LICENSE_IS_NULL = "LICENSE_IS_NULL";

    /**
     * 操作用户总的最大用户数
     */
    public static final String MUILT_USER_THAN_MAXUSERNUMBER = "MUILT_USER_THAN_MAXUSERNUMBER";

    /**
     * 账号超过有效期
     */
    public static final String USER_EXCEED_VALID_TIME = "USER_EXCEED_VALID_TIME";

    /**
     *  没有app登录的权限
     */
    public static final String NO_APP_LOGIN_PERMISSION = "NO_APP_LOGIN_PERMISSION";

    /**
     * 验证码错误
     */
    public static final String VERIFICATION_CODE_ERROR = "VERIFICATION_CODE_ERROR";

    /**
     * 在其他地方登陆
     */
    public static final String LOGIN_ANOTHER_LOCATION = "LOGIN_ANOTHER_LOCATION";

    /**
     * 没有client信息
     */
    public static final String NO_CLIENT_INFO = "NO_CLIENT_INFO";

    /**
     * 没有client的实体信息
     */
    public static final String NO_CLIENT_DETAIL_INFO = "NO_CLIENT_DETAIL_INFO";

    /**
     * client的秘钥不匹配
     */
    public static final String CLIENT_INFO_SECRET_WRONG = "CLIENT_INFO_SECRET_WRONG";

    /**
     * 登陆成功
     */
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";

    /**
     * 无效的令牌信息
     */
    public static final String INVALID_AUTHENTICATION_TOKEN = "INVALID_AUTHENTICATION_TOKEN";

    /**
     * 解码token失败
     */
    public static final String FAILED_DECODE_TOKEN = "FAILED_DECODE_TOKEN";

    /**
     * 需要登录
     */
    public static final String NEED_LOGIN = "NEED_LOGIN";

    /**
     * 退出登录
     */
    public static final String LOG_OUT = "LOG_OUT";

    /**
     * 不能获取用户信息
     */
    public static final String UNABLE_GET_USER_INFO = "UNABLE_GET_USER_INFO";

    /**
     * 不支持身份验证方法
     */
    public static final String AUTHENTICATION_METHOD_NOT_SUPPORTED = "AUTHENTICATION_METHOD_NOT_SUPPORTED";
}
