package com.fiberhome.filink.securitystrategy.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *     密码安全策略修改接收实体
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class PasswordParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 密码安全策略
     */
    private PasswordSecurityStrategy passwordSecurityStrategy;

    /**
     * 校验参数是否符合规范
     * @return true不符合 false符合
     */
    public boolean check() {
        if (StringUtils.isEmpty(paramId) || ObjectUtils.isEmpty(passwordSecurityStrategy)) {
            return true;
        }
        return passwordSecurityStrategy.checkValue();
    }
}
