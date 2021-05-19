package com.fiberhome.filink.gatewaysecurity.sms.authentication;

import com.fiberhome.filink.gatewaysecurity.login.FiLinkAuthenticationFailureHandler;
import com.fiberhome.filink.gatewaysecurity.login.FiLinkAuthenticationSuccessHandler;
import com.fiberhome.filink.gatewaysecurity.service.FilinkUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信验证码配置类
 * 浏览器和app都要使用
 *
 * @author yuanyao@wistronits.com
 * create on 2018/11/21 10:26 PM
 */
@Slf4j
@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    @Autowired
    private FiLinkAuthenticationFailureHandler failureHandler;

    @Autowired
    private FiLinkAuthenticationSuccessHandler successHandler;

    @Autowired
    FilinkUserDetailService userDetailsService;


    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        // 先配置过滤器
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();

        // 先设置manager
        smsCodeAuthenticationFilter.setAuthenticationManager(httpSecurity.getSharedObject(AuthenticationManager.class));
        // 再设置成功/失败处理类
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);

        // 配置Provider
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

        // 加入安全框架
        //smsCodeAuthenticationProvider设置在AuthenticationManager管理的集合上面
        // 把认证过滤器加入username。。。后面
        httpSecurity.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}