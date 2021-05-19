package com.fiberhome.filink.securitystrategy.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *     账号安全策略修改接收实体
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class AccountParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 账号安全策略
     */
    private AccountSecurityStrategy accountSecurityStrategy;

    /**
     * 校验参数是否符合规范
     * @return true不符合 false符合
     */
    public boolean check() {
        if (StringUtils.isEmpty(paramId) || ObjectUtils.isEmpty(accountSecurityStrategy)) {
            return true;
        }
        return accountSecurityStrategy.checkValue();
    }

}
