package com.fiberhome.filink.securitystrategy.bean;

import com.fiberhome.filink.securitystrategy.utils.SecurityLimitEnum;
import com.fiberhome.filink.securitystrategy.utils.SecurityStrategyConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

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

    /**
     * 校验参数
     * @return true false
     */
    public boolean check() {
        return SecurityLimitEnum.ACCOUNT_MIN_LENGTH.checkValue(minLength)
                || SecurityLimitEnum.ILLEGAL_LOGIN_COUNT.checkValue(illegalLoginCount)
                || SecurityLimitEnum.INTERVAL_TIME.checkValue(intervalTime)
                || StringUtils.isEmpty(lockStrategy) || !lockStrategy.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || StringUtils.isEmpty(forbidStrategy) || !forbidStrategy.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || SecurityLimitEnum.NO_OPERATION_TIME.checkValue(noOperationTime);
    }
    /**
     * 校验参数 lockedTime
     * @return true false
     */
    public boolean checkLockedTime() {
        return SecurityLimitEnum.LOCKED_TIME.checkValue(lockedTime);
    }
    /**
     * 校验参数 noLoginTime
     * @return true false
     */
    public boolean checkNoLoginTime() {
        return SecurityLimitEnum.NO_LOGIN_TIME.checkValue(noLoginTime);
    }
}
