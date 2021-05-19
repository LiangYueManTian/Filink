package com.fiberhome.filink.gatewaysecurity;


import com.fiberhome.filink.gatewaysecurity.jwt.FiLinkJwtTokenEncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 负责令牌存取
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/2 8:43 PM
 */
@Configuration
public class TokenStoreConfig {

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 生成处理 管理密钥
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //指定密签

        converter.setSigningKey("yaoyuan");
        return converter;
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return new FiLinkJwtTokenEncher();
    }



}
