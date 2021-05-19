package com.fiberhome.filink.gatewaysecurity.jwt;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/5 12:34 PM
 */
public class FiLinkJwtTokenEncher implements TokenEnhancer {
    /**
     * Provides an opportunity for customization of an access token (e.g. through its additional information map) during
     * the process of creating a new token for use by a client.
     *
     * @param accessToken    the current access token with its expiration and refresh token
     * @param authentication the current authentication including client and user details
     * @return a new token enhanced with additional information
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> map = new HashMap<>();
        map.put("filink", "this is my jwt");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);

        return accessToken;
    }
}
