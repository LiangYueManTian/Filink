package com.fiberhome.filink.gatewaysecurity;


import com.fiberhome.filink.gatewaysecurity.service.FilinkUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;


/**
 * 认证服务器
 * 不继承的时候会自己去找，继承了就需要自己配
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/2 12:45 AM
 */
@Configuration
@EnableAuthorizationServer
public class FiLinkAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private FilinkUserDetailService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;
//
    @Autowired
    private TokenStore jwtTokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenEnhancer jwtTokenEnhancer;

//
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    /**
     * 针对客户端，有哪些第三方应用发放令牌
     * 配置过后配置文件中的clientid和secret就不起作用
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("filink")
                .secret("filink")
                // 有效时间 秒
                .accessTokenValiditySeconds(3600)
                // 支持授权模式
                .authorizedGrantTypes("password", "refresh_token")
                // 发出去的权限有哪些, 如果不配置可以不带，配置了就只能指定在里面选
                // TODO: 2019/1/2 具体权限方式
                .scopes("all", "admin", "read")

                // 多个应用
                .and()
                .withClient("xxx")
                .secret("xxx");

        // TODO: 2019/1/2 修改为可配置
    }
}
