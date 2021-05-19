package com.fiberhome.filink.securitystrategy.utils;

/**
 * <p>
 *     安全策略静态常量
 * </p>
 * @author chaofang@wistronits.com
 * @since  2019/3/14
 */
public class SecurityStrategyConstants {
    /**"1"*/
    public static final String ONE_TYPE = "1";
    /**记录日志 修改密码安全策略XMLFunctionCode*/
    public static final String PASSWORD_FUNCTION_CODE = "2103101";
    /**记录日志 修改账号安全策略XMLFunctionCode*/
    public static final String ACCOUNT_FUNCTION_CODE = "2103102";
    /**记录日志 新增访问控制列表XMLFunctionCode*/
    public static final String ADD_IP_RANGE_CODE = "2103103";
    /**记录日志 新增访问控制列表XMLFunctionCode*/
    public static final String UPDATE_IP_RANGE_CODE = "2103104";
    /**记录日志 删除访问控制列表XMLFunctionCode*/
    public static final String DELETE_IP_RANGE_CODE = "2103105";
    /**记录日志 启用访问控制列表XMLFunctionCode*/
    public static final String ENABLE_IP_RANGE_CODE = "2103106";
    /**记录日志 禁用访问控制列表XMLFunctionCode*/
    public static final String DISABLE_IP_RANGE_CODE = "2103107";
    /**记录日志 全部启用访问控制列表XMLFunctionCode*/
    public static final String ENABLE_IP_RANGES_ALL_CODE = "2103108";
    /**记录日志 全部禁用访问控制列表XMLFunctionCode*/
    public static final String DISABLE_IP_RANGES_AL_CODE = "2103109";
    /**记录日志 修改安全策略 XML NAME*/
    public static final String SECURITY_STRATEGY_NAME= "securityStrategyName";
    /**记录日志 访问控制列表 XML NAME*/
    public static final String IP_RANGE_NAME= "ipRangeName";
    /**记录日志 安全策略对象ID名称*/
    public static final String PARAM_ID= "paramId";
    /**记录日志 访问控制列表对象ID名称*/
    public static final String RANGE_ID= "rangeId";

    /**ipv4*/
    public static final String IPV4 = "ipv4";
    /**ipv4正则*/
    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    /**ipv4全零正则*/
    public static final String IPV4_ZERO_REGEX = "^((0{1,3}\\.){3}(0{1,3}))$";
    /**ipv6*/
    public static final String IPV6 = "ipv6";
    /**ipv6正则*/
    public static final String IPV6_REGEX = "^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$";
    /**ipv6正则*/
    public static final String IPV6_REGEX_FULL = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
    /**ipv6全零正则*/
    public static final String IPV6_ZERO_REGEX = "^(0{1,4}:){7}(0{1,4})$";
    /**ipv6掩码正则*/
    public static final String IPV6_MASK_REGEX = "^(\\d|[1-9]\\d|1[0-1]\\d|12[0-8])$";
    /**ipv6每个位数*/
    public static final int IPV6_NUM = 4;
    /**国际化信息开始IP*/
    public static final String START_IP = "${startIp}";
    /**国际化信息结束IP*/
    public static final String END_IP = "${endIp}";
    /**"0""1"正则*/
    public static final String ONE_ZERO_REGEX = "^(0|1)$";
    /**IP正则*/
    public static final String IP_TYPE_REGEX = "^(ipv(4|6))$";
    /**访问控制列表Redis的key值*/
    public static final String IP_RANGE_REDIS = "IP_RANGE";
    /**IPV6 双冒号*/
    public static final String IP_DOUBLE_COLON = "::";
    /**IPV6 冒号*/
    public static final String IP_COLON = ":";
    /**完整IPV6段数*/
    public static final Integer IPV6_PART = 8;
    /**IPV4每段转16进制IPV6长度*/
    public static final Integer IPV4_TO_IPV6_NUM = 2;
    /**IPV6去除IPV4部分段数*/
    public static final Integer IPV6_IPV4_PORT = 6;
}
