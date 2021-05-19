package com.fiberhome.filink.gatewaysecurity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.userapi.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class FilinkUserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFeign userFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("用户名 = " + username);

        String passWord = null;
        if(StringUtils.isNotEmpty(username)) {
            passWord = userFeign.queryUserPwd(username);
        }
        //不加密使用的方法
        return new User(username,passwordEncoder.encode(passWord),
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_DEMO,ROLE_USER"));

    }


    /**
     * 短信登录
     * @param phoneNumber 手机号
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByPhone(String phoneNumber) throws UsernameNotFoundException {

        Object userObj = userFeign.queryUserByPhone(phoneNumber);
        com.fiberhome.filink.userapi.bean.User loginUser = JSONArray.
                toJavaObject((JSON) JSONArray.toJSON(userObj), com.fiberhome.filink.userapi.bean.User.class);

        return new User(loginUser.getUserCode(),passwordEncoder.encode(loginUser.getPassword()),
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_DEMO,ROLE_USER"));

    }
}
