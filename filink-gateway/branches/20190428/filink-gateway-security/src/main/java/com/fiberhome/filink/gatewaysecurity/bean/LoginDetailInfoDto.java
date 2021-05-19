package com.fiberhome.filink.gatewaysecurity.bean;

import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.Data;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.Serializable;

@Data
public class LoginDetailInfoDto implements Serializable {

    private DefaultOAuth2AccessToken accessToken;

    private MenuTemplateAndMenuInfoTree showMenuTemplate;

    private Object LoginInfo;

    public LoginDetailInfoDto(DefaultOAuth2AccessToken accessToken, MenuTemplateAndMenuInfoTree showMenuTemplate, Object loginInfo) {
        this.accessToken = accessToken;
        this.showMenuTemplate = showMenuTemplate;
        LoginInfo = loginInfo;
    }
}
