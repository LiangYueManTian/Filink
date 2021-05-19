package com.fiberhome.filink.securitystrategy.bean;

import com.fiberhome.filink.securitystrategy.utils.SecurityLimitEnum;
import com.fiberhome.filink.securitystrategy.utils.SecurityStrategyConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *     密码
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
@Data
public class PasswordSecurityStrategy {
    /**
     * 密码最小长度
     */
    private Integer minLength;
    /**
     * 密码至少包含一个大写字母
     */
    private String containUpper;
    /**
     * 密码至少包含一个小写字母
     */
    private String containLower;
    /**
     * 密码至少包含一个数字
     */
    private String containNumber;
    /**
     * 密码至少包含一个特殊字符
     */
    private String containSpecialCharacter;

    /**
     * 校验参数
     * @return true false
     */
    public boolean check() {
        return SecurityLimitEnum.PASSWORD_MIN_LENGTH.checkValue(minLength)
                || StringUtils.isEmpty(containUpper) || !containUpper.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || StringUtils.isEmpty(containLower) || !containLower.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || StringUtils.isEmpty(containNumber) || !containNumber.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || StringUtils.isEmpty(containSpecialCharacter) || !containSpecialCharacter.matches(SecurityStrategyConstants.ONE_ZERO_REGEX);
    }
}
