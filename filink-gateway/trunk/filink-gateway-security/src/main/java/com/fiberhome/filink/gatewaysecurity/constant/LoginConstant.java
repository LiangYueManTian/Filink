package com.fiberhome.filink.gatewaysecurity.constant;

/**
 * 用户登陆
 *
 * @Author:qiqizhu@wistronits.com
 * @Date:2019/8/8
 */
public class LoginConstant {

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
    /**
     *告警灯
     */
    public static final String QUERY_EVERY_ALARM_COUNT ="/filink-alarmcurrent-server/alarmCurrent/queryEveryAlarmCount";


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
