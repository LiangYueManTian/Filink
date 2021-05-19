package com.fiberhome.filink.gatewaysecurity.bean;


import com.fiberhome.filink.userapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.Data;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.Serializable;

/**
 * @author 朱奇
 * 登陆详情实体
 */

@Data
public class LoginDetailInfoDto implements Serializable {
    /**
     * accessToken
     */
    private DefaultOAuth2AccessToken accessToken;
    /**
     * 菜单树
     */
    private MenuTemplateAndMenuInfoTree showMenuTemplate;
    /**
     * 登陆信息
     */
    private Object LoginInfo;

    public LoginDetailInfoDto(DefaultOAuth2AccessToken accessToken, MenuTemplateAndMenuInfoTree showMenuTemplate, Object loginInfo) {
        this.accessToken = accessToken;
        this.showMenuTemplate = showMenuTemplate;
        LoginInfo = loginInfo;
    }
}
