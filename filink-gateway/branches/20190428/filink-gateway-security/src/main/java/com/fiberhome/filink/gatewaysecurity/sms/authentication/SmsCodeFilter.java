package com.fiberhome.filink.gatewaysecurity.sms.authentication;

import com.fiberhome.filink.gatewaysecurity.RedisRepository;
import com.fiberhome.filink.gatewaysecurity.constant.UserConst;
import com.fiberhome.filink.gatewaysecurity.exception.ValidateCodeException;
import com.fiberhome.filink.userapi.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 短信验证码校验拦截器
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/1 2:56 PM
 */
@Slf4j
@Component
public class SmsCodeFilter extends OncePerRequestFilter implements InitializingBean {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private UserFeign userFeign;

    /**
     * url处理
     */
    private AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 存放需要拦截的Url
     */
    private Set<String> urls = new HashSet<>();


    /***
     * 初始化完毕后执行 组装url
     * @throws ServletException
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        // 登录的请求是一定要做验证码的
        urls.add("/auth/phone");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 如果url能够匹配上 , 因为路径中可以带* 所以用AntPathMatcher , 可以匹配/user/*这种
        boolean action = false;
        for (String url : urls) {
            if (matcher.match(url, request.getRequestURI())) {
                action = true;
            }
        }
        // 如果请求的是认证路径
        if (action) {
            try {
                validate(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                // 失败后return
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码
     *
     * @param request
     */
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {

        String smsCode = ServletRequestUtils.getStringParameter(request.getRequest(), UserConst.SMS_CODE);
        String phoneNumber = ServletRequestUtils.getStringParameter(request.getRequest(), UserConst.LOGIN_BY_PHONE);

        if (org.apache.commons.lang3.StringUtils.isBlank(smsCode)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        //查询验证码
        String smsMessage = userFeign.getSmsMessage(phoneNumber);
        if (org.apache.commons.lang3.StringUtils.isBlank(smsMessage)) {
            throw new ValidateCodeException("验证码已过期");
        }

        if (!smsCode.equals(smsMessage)) {
            throw new ValidateCodeException("验证码不正确");
        }

        log.info("图形验证码校验通过");
    }

}
