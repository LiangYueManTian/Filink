package com.fiberhome.filink.server_common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 语言环境  默认为CN 每个微服务可以自己指定环境覆盖
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 10:17
 */
@Data
@Component
@ConfigurationProperties(prefix = "language")
public class LanguageConfig {

    private String environment = "CN";

}
