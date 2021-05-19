package com.fiberhome.filink.gateway_security.sms.codeAuthentication;

import com.fiberhome.filink.gateway_security.RedisRepository;
import com.fiberhome.filink.gateway_security.exception.ValidateCodeExceprion;
import com.fiberhome.filink.gateway_security.sms.codeGenerator.ValidateCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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


    /**url处理*/
    private AntPathMatcher matcher = new AntPathMatcher();

    /** 存放需要拦截的Url*/
    private Set<String> urls = new HashSet<>();


    /***
     * 初始化完毕后执行 组装url
     * @throws ServletException
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
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
            } catch (ValidateCodeExceprion e) {
                log.info("图形验证失败::"+ e.getMessage());
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                // 失败后return
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码
     * @param request
     */
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        // TODO: 2019/1/2 从缓存获取
        ValidateCode codeInSession = redisRepository.get(request, "SMS_CODE");
//        ValidateCode codeInSession = (ValidateCode) sessionStrategy.getAttribute(request,
//                "SMS_CODE");

        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "smsCode");

        if (org.apache.commons.lang.StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeExceprion("验证码的值不能为空");
        }

        if (null == codeInSession) {
            throw new ValidateCodeExceprion("验证码不存在");
        }

        if (codeInSession.isExpried()) {
            redisRepository.remove(request,"SMS_CODE");
            throw new ValidateCodeExceprion("验证码已经过期");
        }

        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeExceprion("验证码不匹配");
        }

        redisRepository.remove(request,"SMS_CODE");
        log.info("图形验证码校验通过");

    }

}
