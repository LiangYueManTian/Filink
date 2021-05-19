package com.fiberhome.filink.gateway_security.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 权限控制
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:24
 */
@Component
public class FiLinkAuthorizeConfigProvider implements AuthorizeConfigProvider {
    /**
     * 添加需要验证的权限
     * 包括系统配置权限以及数据库权限
     *
     * @param config
     */
    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {

        config.antMatchers(
                "/sign.html",
                "/filink/login",
                "/authentication/require",
                "/code/sms",
                "/auth/phone",
                "/session/invalid", "/oauth/**", "/testE","/auth/user",
//                 todo 先放掉所有请求方便测试
                "/**"
        ).permitAll();

        // 系统配置权限
        config.antMatchers("testAuth").hasRole("DEMO");

        // 剩下的所有请求都需要身份认证
        config.anyRequest().authenticated();

        // 校验数据库权限
        config.anyRequest().access("@rbacService.hasPermission(request,authentication)");

    }
}
