package com.fiberhome.filink.gatewaysecurity;

import com.fiberhome.filink.gatewaysecurity.authorize.AuthorizeConfigManager;
import com.fiberhome.filink.gatewaysecurity.license.LoginFilter;
import com.fiberhome.filink.gatewaysecurity.license.ValidateLicenseFilter;
//import com.fiberhome.filink.gateway_security.service.FilinkUserDetailService;
import com.fiberhome.filink.gatewaysecurity.sms.authentication.SmsCodeAuthenticationSecurityConfig;
import com.fiberhome.filink.gatewaysecurity.sms.authentication.SmsCodeFilter;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security配置类 集成springsecurity自带的适配器
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 1:47 PM
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public PasswordEncoder passwordEncoder() {
        // 如果有自定义加密 需要自己实现PasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationFailureHandler fiLinkAuthenticationFailureHandler;


//    /**
//     * 注入短信验证码配置  注入就失败
//     */
//
    /**
     * license拦截器
     */
    @Autowired
    private ValidateLicenseFilter validateLicenseFilter;

    @Autowired
    private LoginFilter demoFilter;

    @Autowired
    private SmsCodeFilter smsCodeFilter;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;


    /**
     * 覆盖父类的方法
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {


        // 访问出现Could not verify the provided CSRF token because your session was not found.
        // 默认情况下 SpringSecurity提供了CSRF防护 跨站请求防护
        http.csrf().disable();

        // 登录相关
        http.formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl("/filink/login")
                // 指定登录成功失败处理
                .successHandler((AuthenticationSuccessHandler) SpringUtils.getBean("fiLinkAuthenticationSuccessHandler"))
                .failureHandler(fiLinkAuthenticationFailureHandler);



        http
                .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(demoFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(validateLicenseFilter, UsernamePasswordAuthenticationFilter.class)
                // 短信登录
                .apply((SmsCodeAuthenticationSecurityConfig) SpringUtils.getBean("smsCodeAuthenticationSecurityConfig"));



        authorizeConfigManager.config(http.authorizeRequests());

    }
}