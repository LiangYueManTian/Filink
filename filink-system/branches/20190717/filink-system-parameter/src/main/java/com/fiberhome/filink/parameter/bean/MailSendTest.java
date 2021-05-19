package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   阿里云邮箱配置测试信息实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-16
 */
@Data
public class MailSendTest {
    /**
     * 目标地址
     */
    private String toAddress;
    /**
     * 访问用户
     */
    private String accessKeyId;
    /**
     * 访问密钥
     */
    private String accessKeySecret;

    /**
     * 校验参数
     * @return true不符合 false符合
     */
    public boolean check() {
        if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(accessKeySecret)
                || StringUtils.isEmpty(toAddress)) {
            return true;
        }
        toAddress = toAddress.toLowerCase();
        if (!accessKeyId.matches(SystemParameterConstants.ALI_KEY_REGEX)
                || !accessKeySecret.matches(SystemParameterConstants.ALI_KEY_REGEX)) {
            return true;
        }
        return  !toAddress.matches(SystemParameterConstants.MAIL_REGEX)
                || toAddress.length() > SystemParameterConstants.MAIL_LENGTH;
    }
}
