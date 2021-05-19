package com.fiberhome.filink.gateway_security.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * 权限组装管理接口
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:26
 */
public interface AuthorizeConfigManager {

    /**
     * 权限组装
     * @param config
     */
    void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config);

}
