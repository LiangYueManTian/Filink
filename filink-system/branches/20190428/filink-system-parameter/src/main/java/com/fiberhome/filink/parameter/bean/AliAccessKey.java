package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   阿里云访问密钥实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class AliAccessKey {
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
        return StringUtils.isEmpty(accessKeyId) || !accessKeyId.matches(SystemParameterConstants.ALI_KEY_REGEX)
                || StringUtils.isEmpty(accessKeySecret) || !accessKeySecret.matches(SystemParameterConstants.ALI_KEY_REGEX);
    }
}
