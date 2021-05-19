package com.fiberhome.filink.parameter.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *   阿里云访问密钥修改接收实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-12
 */
@Data
public class AliAccessKeyParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 阿里云访问密钥实体类
     */
    private AliAccessKey aliAccessKey;
    /**
     * 校验参数
     * @return true不符合 false符合
     */
    public boolean check() {
        return StringUtils.isEmpty(paramId) || ObjectUtils.isEmpty(aliAccessKey)
                || aliAccessKey.check();
    }

}
