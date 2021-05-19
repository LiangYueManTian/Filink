package com.fiberhome.filink.securitystrategy.bean;

import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *     IP地址实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-10
 */
@Data
public class IpAddress {
    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 校验IP是否正确
     * @return true正确 false不正确
     */
    public boolean check() {
        if (StringUtils.isEmpty(ipAddress)) {
            return false;
        }
        return ipAddress.matches(SecurityStrategyConstants.IPV4_REGEX)
                || ipAddress.matches(SecurityStrategyConstants.IPV6_REGEX_FULL);
    }

}
