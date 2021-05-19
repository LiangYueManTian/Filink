package com.fiberhome.filink.logapi.bean;

import lombok.Data;

/**
 * 登录用户类
 * @author hedongwei@wistronits.com
 * @date 2019/2/14 18:43
 */
@Data
public class LoginUserBean {
    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;
}
