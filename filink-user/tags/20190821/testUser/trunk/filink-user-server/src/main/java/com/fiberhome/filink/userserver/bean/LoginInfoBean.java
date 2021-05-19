package com.fiberhome.filink.userserver.bean;

import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.Data;

/**
 * 登陆信息实体  用于网管登陆调用
 * @Author:qiqizhu@wistronits.com
 * Date:2019/7/19
 */
@Data
public class LoginInfoBean {
    /**
     * 登陆检验code
     */
    private Integer loginCode;
    /**
     * 用户详细信息
     */
    private User user;
    /**
     * 当前菜单
     */
    private MenuTemplateAndMenuInfoTree showMenuTemplate;
}
