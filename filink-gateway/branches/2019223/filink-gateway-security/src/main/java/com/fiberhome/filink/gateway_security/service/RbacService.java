package com.fiberhome.filink.gateway_security.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 判断权限
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:57
 */
public interface RbacService {

    /**
     * 判断权限
     * @param request
     * @param authentication
     * @return
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
