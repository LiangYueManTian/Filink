package com.fiberhome.filink.parameter.bean;

import lombok.Data;

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
}
