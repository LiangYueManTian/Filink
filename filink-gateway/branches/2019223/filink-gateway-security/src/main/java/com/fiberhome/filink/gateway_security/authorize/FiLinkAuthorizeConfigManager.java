package com.fiberhome.filink.gateway_security.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限组装管理
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:27
 */
@Component
public class FiLinkAuthorizeConfigManager implements AuthorizeConfigManager {

    /**
     * 注入所有权限提供
     */
    @Autowired
    private List<AuthorizeConfigProvider> authorizeConfigProviders;


    /**
     * 组装权限
     *
     * @param config
     */
    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        for (AuthorizeConfigProvider authorizeConfigProvider : authorizeConfigProviders) {
            authorizeConfigProvider.config(config);
        }

    }
}
