package com.fiberhome.filink.gateway_security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/2 12:45 AM
 */
@Configuration
@EnableResourceServer
public class FiLinkResoureServerConfig /*extends ResourceServerConfigurerAdapter*/ {

   /* @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }*/
}
