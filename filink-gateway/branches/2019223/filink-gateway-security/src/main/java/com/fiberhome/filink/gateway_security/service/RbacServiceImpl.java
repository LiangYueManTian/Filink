package com.fiberhome.filink.gateway_security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限控制 返回false则请求失败
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:58
 */
@Slf4j
@Component("rbacService")
public class RbacServiceImpl implements RbacService {

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();

        boolean permission = false;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            log.info("当前用户" + username);

            // TODO: 2019-01-16 获取所有权限的url
            Set<String> urls = new HashSet<>();
            urls.add("/testAuth");
            urls.add("/testUser");
            for (String url : urls) {
                // 传进来的url是否匹配
                if (pathMatcher.match(url, request.getRequestURI())) {
                    permission = true;
                    break;
                }
            }
        }
        return permission;
    }

}
