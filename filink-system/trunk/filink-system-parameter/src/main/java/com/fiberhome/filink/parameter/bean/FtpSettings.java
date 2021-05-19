package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.FtpNumEnum;
import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   FTP服务设置实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-01
 */
@Data
public class FtpSettings {
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 内网IP地址
     */
    private String innerIpAddress;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 断开（超时）时间
     */
    private Integer disconnectTime;

    /**
     * 校验参数是否正确
     * @return true符合 false不符合
     */
    public boolean checkValue() {
        if (StringUtils.isEmpty(ipAddress) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(innerIpAddress) || StringUtils.isEmpty(userName)) {
            return false;
        }
        if (checkIntegerValue()) {
            return false;
        }
        return checkIp();
    }

    /**
     * 校验数值是否符合规范
     * @return true符合 false不符合
     */
    private  boolean checkIp() {
        if (!(ipAddress.matches(SystemParameterConstants.IPV4_REGEX)
                || ipAddress.matches(SystemParameterConstants.IPV6_REGEX))) {
            return false;
        }
        return innerIpAddress.matches(SystemParameterConstants.IPV4_REGEX)
                || innerIpAddress.matches(SystemParameterConstants.IPV6_REGEX);
    }

    /**
     * 校验数值是否符合规范
     * @return true不符合 false符合
     */
    private boolean checkIntegerValue() {
        if (FtpNumEnum.LENGTH.checkValue(userName.length()) || FtpNumEnum.LENGTH.checkValue(password.length())) {
            return true;
        }
        return FtpNumEnum.PORT.checkValue(port) || FtpNumEnum.DISCONNECT_TIME.checkValue(disconnectTime);
    }
}
