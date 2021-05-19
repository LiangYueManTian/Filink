package com.fiberhome.filink.securitystrategy.bean;

import com.fiberhome.filink.securitystrategy.constant.SecurityLimitEnum;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
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
     * 校验参数是否符合规范
     * @return true不符合 false符合
     */
    public boolean checkValue() {
        if (checkEmpty() || SecurityLimitEnum.PASSWORD_MIN_LENGTH.checkValue(minLength)) {
            return true;
        }
        if (!containUpper.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || !containLower.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)) {
            return true;
        }
        return !containNumber.matches(SecurityStrategyConstants.ONE_ZERO_REGEX)
                || !containSpecialCharacter.matches(SecurityStrategyConstants.ONE_ZERO_REGEX);
    }

    /**
     * 检验参数是否为空
     * @return true是 false不是
     */
    private boolean checkEmpty() {
        if (StringUtils.isEmpty(containUpper) || StringUtils.isEmpty(containLower)) {
            return true;
        }
        return StringUtils.isEmpty(containNumber) || StringUtils.isEmpty(containSpecialCharacter);
    }
}
