package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   阿里云短信配置测试信息实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-16
 */
@Data
public class MessageSendTest {
    /**
     * 电话号码
     */
    private String phone;
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
        return StringUtils.isEmpty(phone) || phone.length() > SystemParameterConstants.PHONE_LENGTH
                || StringUtils.isEmpty(accessKeyId) || !accessKeyId.matches(SystemParameterConstants.ALI_KEY_REGEX)
                || StringUtils.isEmpty(accessKeySecret) || !accessKeySecret.matches(SystemParameterConstants.ALI_KEY_REGEX);
    }
}
