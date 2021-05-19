package com.fiberhome.filink.gatewaysecurity.constant;

/**
 * @@Author:qiqizhu@wistronits.com
 * 用户登陆返回码
 */
public class LoginResultCode {
    /**
     * 系统服务异常
     */
    public static final int SYSTEM_SERVICE_ERROR = 125000;
    /**
     * ip没有访问权限
     */
    public static final int IP_NOT_HAVE_PERMISSION = 125010;
    /**
     * 用户服务异常
     */
    public static final int USER_SERVICE_ERROR = 125020;
    /**
     * 超过最大登录用户数
     */
    public static final int MORE_THAN_MAXUSERNUMBER = 125030;
    /**
     * 请求头无CLIENT信息
     */
    public static final int NO_CLIENT_INFO = 125040;

    /**
     * 用户已被禁用
     */
    public static final int USER_HAS_FORBIDDEN = 125050;

    /**
     * licene已经过期
     */
    public static final int LICENE_HAS_EXPIRATION = 125060;

    /**
     * 用户已被锁定
     */
    public static final int USER_HAS_LOCKED = 125070;

    /**
     * license为空
     */
    public static final int LICENSE_IS_NULL = 125080;

    /**
     * 用户超过有效期限
     */
    public static final int USER_EXCEED_VALID_TIME = 125090;

    /**
     * 没有app登录的权限
     */
    public static final int NO_APP_LOGIN_PERMISSION = 125100;

    /**
     * 操作用户总的最大用户数
     */
    public static final int MUILT_USER_THAN_MAXUSERNUMBER = 125160;
    /**
     * license未开始
     */
    public static final int LICENSE_NOT_STARTED = 125170;
    /**
     * 没有client detail info
     */
    public static final int NO_CLIENT_DETAIL_INFO = 125180;
    /**
     * client的秘钥不匹配
     */
    public static final int CLIENT_INFO_SECRET_WRONG = 125190;

}
