package com.fiberhome.filink.gatewaysecurity.sms.authentication;

import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * 封装登录信息
 * 参照UsernamePasswordAuthenticationToken
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/1 2:41 PM
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {


    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 存放认证信息
     * 身份认证之前存放手机号
     * 身份认证之后存放用户信息
     */
    private final Object phone;


    // ~ Constructors
    // ===================================================================================================

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     */
    public SmsCodeAuthenticationToken(String phone) {
        super(null);
        this.phone = phone;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param phone
     * @param authorities
     */
    public SmsCodeAuthenticationToken(Object phone,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = phone;
        super.setAuthenticated(true); // must use super, as we override
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.phone;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    I18nUtils.getString(I18Const.INVALID_AUTHENTICATION_TOKEN));
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

    public Object getPhone() {
        return phone;
    }
}
