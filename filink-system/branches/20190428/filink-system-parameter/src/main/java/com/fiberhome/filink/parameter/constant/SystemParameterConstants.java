package com.fiberhome.filink.parameter.constant;

/**
 * <p>
 *     系统参数静态常量
 * </p>
 * @author chaofang@wistronits.com
 * @since  2019/3/19
 */
public class SystemParameterConstants {
    /**邮箱 XML 日志functionCode*/
    public static final String MAIL_FUNCTION_CODE = "2106101";
    /**短信 XML 日志functionCode*/
    public static final String MESSAGE_FUNCTION_CODE = "2106102";
    /**消息通知 XML 日志functionCode*/
    public static final String MESSAGE_NOTIFICATION_FUNCTION_CODE = "2106103";
    /**移动推送 XML 日志functionCode*/
    public static final String MOBILE_PUSH_FUNCTION_CODE = "2106104";
    /**显示设置 XML 日志functionCode*/
    public static final String DISPLAY_FUNCTION_CODE = "2106105";
    /**FTP服务 XML 日志functionCode*/
    public static final String FTP_FUNCTION_CODE = "2106106";
    /**记录日志 修改系统参数 XML NAME*/
    public static final String SYSTEM_PARAMETER_NAME= "systemParameterName";
    /**记录日志 系统参数对象ID名称*/
    public static final String PARAM_ID= "paramId";
    /**"1"*/
    public static final String ONE_TYPE = "1";
    /**"0"*/
    public static final String ZERO_TYPE = "0";
    /**"1"和"0"正则*/
    public static final String ONE_ZERO_REGEX = "^(0|1)$";
    /**消息提醒音名称正则*/
    public static final String SOUND_REGEX = "^([A-Za-z0-9.]{1,16})$";
    /**阿里云key和secret正则*/
    public static final String ALI_KEY_REGEX = "^([A-Za-z0-9]{1,32})$";
    /**ipv4正则*/
    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    /**ipv6正则*/
    public static final String IPV6_REGEX = "^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$";
    /**邮箱正则*/
    public static final String MAIL_REGEX = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$$";
    /**手机号码长度*/
    public static final Integer PHONE_LENGTH = 32;
    /**邮箱长度*/
    public static final Integer MAIL_LENGTH = 255;
    /**阿里云短信发送成功返回码*/
    public static final String SMS_RESPONSE_OK = "ok";
    /**系统图标默认值*/
    public static final String DEFAULT_SYSTEM_LOGO = "local";
}
