package com.fiberhome.filink.gateway_security.Const;

/**
 * 常量配置
 *
 * @author gongxuan
 */
public class UserConst {

    /**
     * token在数组中的位置
     */
    public static final int TOKEN_SITE = 1;

    /**
     * redis存储用户的前缀
     */
    public static String REDIS_USER_PREFIX = "USER_";

    /**
     * redis存储用户key中的分隔符
     */
    public static String REDIS_USER_SPLIT = "_";

    /**
     * 登录过去时间
     */
    public static final Long EXPIRE_TIME = 1800L;


}
