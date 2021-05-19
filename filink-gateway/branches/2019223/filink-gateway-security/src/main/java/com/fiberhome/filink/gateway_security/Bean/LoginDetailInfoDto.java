package com.fiberhome.filink.gateway_security.Bean;

import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.Data;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.Serializable;

/**
 * 登录详情对象
 * @author gongxuan
 */
@Data
public class LoginDetailInfoDto implements Serializable{

    private DefaultOAuth2AccessToken accessToken;

    private MenuTemplateAndMenuInfoTree showMenuTemplate;

    private Object loginInfo;

    public LoginDetailInfoDto(DefaultOAuth2AccessToken accessToken, MenuTemplateAndMenuInfoTree showMenuTemplate, Object info) {
        this.accessToken = accessToken;
        this.showMenuTemplate = showMenuTemplate;
        loginInfo = info;
    }
}
