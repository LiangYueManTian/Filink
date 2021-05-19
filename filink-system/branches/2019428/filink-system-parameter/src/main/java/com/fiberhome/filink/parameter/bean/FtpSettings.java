package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.utils.FtpNumEnum;
import com.fiberhome.filink.parameter.utils.SystemParameterConstants;
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

    private String ipAddress;

    private Integer port;

    private String userName;

    private String password;

    private Integer disconnectTime;

    /**
     * 校验参数是否正确
     * @return true符合 false不符合
     */
    public boolean checkValue() {
        if (StringUtils.isEmpty(ipAddress) || StringUtils.isEmpty(password)
                || port == null || disconnectTime == null
                || StringUtils.isEmpty(userName)) {
            return false;
        }
        if (FtpNumEnum.LENGTH.checkValue(userName.length())
                || FtpNumEnum.LENGTH.checkValue(password.length())
                || FtpNumEnum.PORT.checkValue(port)
                || FtpNumEnum.DISCONNECT_TIME.checkValue(disconnectTime)) {
            return false;
        }
        return ipAddress.matches(SystemParameterConstants.IPV4_REGEX)
                || ipAddress.matches(SystemParameterConstants.IPV6_REGEX);
    }
}
