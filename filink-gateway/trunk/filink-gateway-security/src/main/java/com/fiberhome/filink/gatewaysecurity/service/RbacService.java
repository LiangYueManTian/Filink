package com.fiberhome.filink.gatewaysecurity.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-16 15:57
 */
public interface RbacService {

    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
