package com.fiberhome.filink.gatewaysecurity.license;

import com.fiberhome.filink.gatewaysecurity.exception.AuthenticationUserException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *自定义License 校验过滤器   此处用作自定义验证器demo
 * 需要在配置中加入过滤器
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 6:08 PM
 */
@Slf4j
@Component
public class ValidateLicenseFilter extends OncePerRequestFilter {

    @Resource(name = "fiLinkAuthenticationFailureHandler")
    private AuthenticationFailureHandler fiLinkAuthenticationFailureHandler;

    /**
     *校验license
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 如果请求的是登录路径
        if (StringUtils.equals("/filink/login", request.getRequestURI())
                && StringUtils.equalsIgnoreCase(request.getMethod(),"post")) {
            try {
                // 校验
            } catch (AuthenticationUserException e) {
                log.error("Bad things:",e);
                fiLinkAuthenticationFailureHandler.onAuthenticationFailure(request,response,e);
                // 失败后return
                return;
            }
        }
        // 校验成功则放行
        filterChain.doFilter(request, response);
    }
}
