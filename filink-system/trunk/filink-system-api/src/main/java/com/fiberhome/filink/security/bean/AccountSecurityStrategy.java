package com.fiberhome.filink.security.bean;

import lombok.Data;

/**
 * <p>
 *     账号安全策略
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
@Data
public class AccountSecurityStrategy {
    /**
     * 用户代码最小长度
     */
    private Integer minLength;
    /**
     * 非法登录允许次数
     */
    private Integer illegalLoginCount;
    /**
     * 登录失败最大间隔时间
     */
    private Integer intervalTime;
    /**
     * 启用账号锁定策略
     */
    private String lockStrategy;
    /**
     * 账号锁定时间
     */
    private Integer lockedTime;
    /**
     * 启用账号禁用策略
     */
    private String forbidStrategy;
    /**
     * 账号连续未登录时间
     */
    private Integer noLoginTime;
    /**
     * 无操作登出时间
     */
    private Integer noOperationTime;
}
