package com.fiberhome.filink.gateway_security.license;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.gateway_security.Const.UserConst;
import com.fiberhome.filink.gateway_security.utils.IpUtil;
import com.fiberhome.filink.gateway_security.utils.LogUtils;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.user_api.api.UserFeign;
import com.fiberhome.filink.user_api.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Set;

/**
 *自定义License 校验过滤器   此处用作自定义验证器demo
 * 需要在配置中加入过滤器
 *
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

    private static final String LOGIN_URL = "/filink/login";

    private static final String TIMEOUT_MODEL = "1504102";

    private static int EXPIRE_TIME = 30;

    private static int TOKEN_LENGTH = 32;

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

        String ipAdrress = IpUtil.getIpAdrress(request);
        System.out.println(ipAdrress);
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
//        校验逻辑
        // 如果请求的是登录路径
        if (!StringUtils.equals(LOGIN_URL, request.getRequestURI())) {

            log.info(request.getRequestURI());
            String userId = RequestInfoUtils.getUserId();
            String token = RequestInfoUtils.getToken();

            if(userId != null) {
                boolean flag = userFeign.updateLoginTime(userId, token);
                System.out.println("userFeign.updateLoginTime = " + flag);
                if (flag) {
                    filterChain.doFilter(request, response);
                    return;
                }

                //如果用户信息已经过期,根据用户id获取用户信息，同时添加登出日志
                //添加token值，避免在用户过期退出的时候多次输入日志文件
                System.out.println("========================================");
                System.out.println("RedisUtils.hasKey(token) = " + RedisUtils.hasKey(token));
                System.out.println("token length = " + token.length());
                System.out.println("========================================");
                if(token.length() >= TOKEN_LENGTH && !RedisUtils.hasKey(token)) {
                    Result userResult = userFeign.queryUserInfoById(userId);
                    Object userObject = userResult.getData();
                    User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(userObject), User.class);
                    LogUtils.addLog(user, TIMEOUT_MODEL, LogConstants.LOG_TYPE_SECURITY);

                    //做已经输出日志的标识
                    RedisUtils.set(token,token,EXPIRE_TIME);
                }
            }
            log.info("");
            fiLinkNotLoginFailureHandler.onAuthenticationFailure(request,response,null);

        }else {
            // 校验成功则放行
            filterChain.doFilter(request, response);
        }
    }
}
