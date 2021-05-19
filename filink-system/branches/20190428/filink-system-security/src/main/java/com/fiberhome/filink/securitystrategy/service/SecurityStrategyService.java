package com.fiberhome.filink.securitystrategy.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.securitystrategy.bean.AccountParam;
import com.fiberhome.filink.securitystrategy.bean.PasswordParam;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
public interface SecurityStrategyService {

    /**
     * 查询账号安全策略
     * @return 账号安全策略
     */
    Result queryAccountSecurity();

    /**
     * 查询密码安全策略
     * @return 密码安全策略
     */
    Result queryPasswordSecurity();

    /**
     * 更新密码安全策略
     * @param passwordParam 密码安全策略
     * @return 更新条数
     */
    Result updatePasswordStrategy(PasswordParam passwordParam);

    /**
     * 更新账号安全策略
     * @param accountParam 账号安全策略
     * @return 更新条数
     */
    Result updateAccountStrategy(AccountParam accountParam);
}
