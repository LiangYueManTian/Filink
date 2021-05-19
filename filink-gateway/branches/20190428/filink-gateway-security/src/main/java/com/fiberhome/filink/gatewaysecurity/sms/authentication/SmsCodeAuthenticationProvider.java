package com.fiberhome.filink.gatewaysecurity.sms.authentication;

import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.service.FilinkUserDetailService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 自定义SmsAuthencationProvider提供校验逻辑
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/1 2:51 PM
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {


    private FilinkUserDetailService userDetailsService;

    private UserFeign userFeign;

    /**
     * 身份认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 用userDetailService获取用户信息
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        // 根据手机号拿到用户信息

        UserDetails user = userDetailsService.loadUserByPhone((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException(I18nUtils.getString(I18Const.UNABLE_GET_USER_INFO));
        }

        // 如果得到了用户信息 重新构造token
        //       * 1:用户认证信息
        //		 * 2：用户权限
        SmsCodeAuthenticationToken token = new SmsCodeAuthenticationToken(user, user.getAuthorities());
        // 把认证信息放进新的token 将之前未认证的请求放进认证后的Token中
        token.setDetails(authenticationToken.getDetails());

        return token;
    }

    /**
     * AuthenticationManager带着Token调用Provider
     * 判断传进来的Token最终调用的是哪个Provider
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(FilinkUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
