package com.fiberhome.filink.gateway_security.service;

import com.fiberhome.filink.user_api.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 实现security的userDetailservice自定义认证逻辑
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 2:43 PM
 */
@Slf4j
@Component
public class FilinkUserDetailServiceImpl implements UserDetailsService {



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFeign userFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询数据库
        // ... ...
        log.info("用户登录验证");

        //passwordEncoder 里面有两个方法 一个加密 一个解密
        // 用户注册需要调用encode方法，
        // 校验的时候需要调用match方法解密，security默认会调这个方法校验
        log.info("数据库密码是:"+passwordEncoder.encode("123456"));


        // 第三个参数是用户的权限 告诉用户有哪些权限
        // passwordEncoder.encode("123456") 加密操作  应该是注册的时候做这个操作
//        return new User(username, "123456",
//                true, true, true, true,

        log.info(username+">>>>>>>>>>>>>>>>>>>>>>>>>");
        String passWord = userFeign.queryUserPwd(username);

        return new User(username,passwordEncoder.encode(passWord),
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_DEMO,ROLE_USER"));
    }


}
