package com.fiberhome.filink.gatewaysecurity.sms.authentication;

import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截短信登录
 * @author yuanyao@wistronits.com
 * create on 2018/11/21 8:49 PM
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String YY_FROM_PHONE_KEY = "phoneNumber";

    /**
     * 请求中参数名称为 phone
     */
    private String phoneParameter = YY_FROM_PHONE_KEY;
    /**
     * 只处理post请求
     */
    private boolean postOnly = true;

    public SmsCodeAuthenticationFilter() {
        // 指定顶球路径
        super(new AntPathRequestMatcher("/auth/phone", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    I18nUtils.getString(I18Const.AUTHENTICATION_METHOD_NOT_SUPPORTED));
        }

        String phone = obtainPhone(request);

        if (phone == null) {
            phone = "";
        }


        phone = phone.trim();
        // 实例化自己的token
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(phone);

        // 修改参数为自己创建的token
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(phoneParameter);
    }

    protected void setDetails(HttpServletRequest request,
                              SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public String getPhoneParameter() {
        return phoneParameter;
    }

    public void setPhoneParameter(String phoneParameter) {
        this.phoneParameter = phoneParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


}
