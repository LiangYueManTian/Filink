package com.fiberhome.filink.securitystrategy.constant;

/**
 * <p>
 *     安全策略数值枚举类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/2/20
 */
public enum SecurityLimitEnum {

    /**
     * 密码最小长度
     */
    PASSWORD_MIN_LENGTH(6, 18),
    /**
     * 用户代码最小长度
     */
    ACCOUNT_MIN_LENGTH(6, 18),
    /**
     * 非法登录允许次数
     */
    ILLEGAL_LOGIN_COUNT(5, 99),
    /**
     * 登录失败最大间隔时间(分钟)
     */
    INTERVAL_TIME(1, 99),
    /**
     * 账号锁定时间(分钟)
     */
    LOCKED_TIME(1, 1440),
    /**
     * 账号连续未登录时间(天)
     */
    NO_LOGIN_TIME(1, 1000),
    /**
     * 无操作登出时间(分钟)
     */
    NO_OPERATION_TIME(1, 99);
    /**
     * 最小值
     */
    private Integer minValue;
    /**
     * 最大值
     */
    private Integer maxValue;
    /**
     * 构造
     */
    SecurityLimitEnum(Integer minValue, Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    /**
     * 与最小值、最大值比较
     */
    public boolean checkValue(Integer value) {
        return value == null || value < minValue || value > maxValue;
    }

}
