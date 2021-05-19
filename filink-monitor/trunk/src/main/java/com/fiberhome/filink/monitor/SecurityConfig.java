//package com.fiberhome.filink.monitor;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
///**
// * Security配置类 集成springsecurity自带的适配器
// *
// * @author yuanyao@wistronits.com
// * create on 2018/12/30 1:47 PM
// */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//
//
//
//
//
//
////    /**
////     * 注入短信验证码配置  注入就失败 SmsCodeAuthenticationSecurityConfig
////     */
//
//
//    /**
//     * 覆盖父类的方法
//     *
//     * @param http
//     * @throws Exception
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//
//        // 访问出现Could not verify the provided CSRF token because your session was not found.
//        // 默认情况下 SpringSecurity提供了CSRF防护 跨站请求防护
//        http.csrf().disable();
//
//
//
//    }
//}
