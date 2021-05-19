package com.fiberhome.filink.gateway_security.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * 权限提供结构
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:22
 */
public interface AuthorizeConfigProvider {

    /**
     * 配置权限
     * @param config
     */
    void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config);
}
