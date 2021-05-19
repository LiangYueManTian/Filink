package com.fiberhome.filink.gatewaysecurity.constant;

public class UserConst {

    /**
     * 用户登录日志模板
     */
    public static String USER_LONG_MODEL = "1504101";

    public static int EXPIRE_TIME = 18;

    /**
     * 超级管理员的usercode
     */
    public static final String ADMIN = "admin";


    /**
     * 登陆接口，不需要登陆拦截
     */
    public static final String LOGIN_URL = "/filink/login";

    /**
     * 系统参数url，不需要登录
     */
    public static final String SYSTEM_PARAMTER =
            "/filink-system-server/systemParameter/selectDisplaySettingsParamForPageCollection";

    /**
     * 手机app登录url
     */
    public static final String APP_LOGIN = "/auth/phone";

    /**
     * 关于admin的licese
     */
    public static final String LICENE_INFO_ADMIN = "/zuul/filink-system-server/licenseInfo/uploadLicenseForAdmin";

    /**
     * 关于时间的license
     */
    public static final String LICENE_INFO_TIME = "/filink-system-server/licenseInfo/validateLicenseTime";

    /**
     * 手机登录
     */
    public static final String SEND_APP_MESSAGE = "/filink-user-server/user/sendMessage";

    public static final String BASIC = "Basic";

    /**
     * 监控路径
     */
    public static final String MONITOR_HEALTH = "/health";

    /**
     * 监控路径-metrics
     */
    public static final String MONITOR_METRICS = "/metrics";

    /**
     * 监控路径-hystrix
     */
    public static final String MONITOR_HYSTRIX = "/hystrix";



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
     *  没有app登录的权限
     */
    public static final int NO_APP_LOGIN_PERMISSION = 125100;

    /**
     * 操作用户总的最大用户数
     */
    public static final int MUILT_USER_THAN_MAXUSERNUMBER = 125160;

    /**
     * admin的用户名
     */
    public static final String USER_NAME = "username";

    /**
     * 登录源 1是Web网管，0是APP
     */
    public static final String LOGIN_SOURCE = "loginSource";

    /**
     * 登录源中的请求头
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * 用户被禁用状态码
     */
    public static final String USER_STOP_STATUS = "0";


    /**
     * 用户已被删除
     */
    public static final String THE_USER_IS_DELETED = "THE_USER_IS_DELETED";

    /**
     * 用户已被删除标志
     */
    public static final String THE_USER_IS_DELETED_FLAG = "125180";

    /**
     * 手机号参数
     */
    public static final String LOGIN_BY_PHONE = "phoneNumber";

    /**
     * 手机号参数
     */
    public static final String SMS_CODE = "smsCode";

    /**
     * 带有custom的参数
     */
    public static final String PARAMETER_CUSTOM = "custom";

    /**
     * reponse返回头的格式
     */
    public static final String RESPONSE_HEAD_TYPE = "application/json;charset=UTF-8";

    /**
     * 写日志中的id标识
     */
    public static final String LOG_ID_TIP = "id";

    /**
     * token分隔符
     */
    public static final String TOKEN_SPLIE = ":";

    /**
     * 编码格式
     */
    public static final String ENCODE_FORMAT = "UTF-8";
}
