package com.fiberhome.filink.gatewaysecurity.license;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.gatewaysecurity.constant.UserConst;
import com.fiberhome.filink.userapi.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 *
 * 用户登录拦截过滤
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 6:08 PM
 */
@Slf4j
@Component
public class LoginFilter extends OncePerRequestFilter {

    @Resource(name = "fiLinkNotLoginFailureHandler")
    private AuthenticationFailureHandler fiLinkNotLoginFailureHandler;

    @Autowired
    private UserFeign userFeign;


    /**
     * 登录拦截过滤
     *
     * @param request   request请求
     * @param response  response返回
     * @param filterChain   过滤
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        List<String> urlList = Arrays.asList(UserConst.LOGIN_URL,UserConst.SYSTEM_PARAMTER,UserConst.APP_LOGIN, UserConst.LICENE_INFO_ADMIN,
                UserConst.LICENE_INFO_TIME,UserConst.SEND_APP_MESSAGE,UserConst.MONITOR_HEALTH,
                UserConst.MONITOR_METRICS,UserConst.MONITOR_HYSTRIX);

        //判断是否为不需要登录的url
        if (!urlList.contains(request.getRequestURI())){

            log.info(request.getRequestURI());
            String userId = RequestInfoUtils.getUserId();
            String token = RequestInfoUtils.getToken();
            if (userId != null) {

                boolean flag = userFeign.updateLoginTime(userId, token);
                if (flag) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            fiLinkNotLoginFailureHandler.onAuthenticationFailure(request, response, null);
        } else {
            // 校验成功则放行
            filterChain.doFilter(request, response);
        }
    }
}
