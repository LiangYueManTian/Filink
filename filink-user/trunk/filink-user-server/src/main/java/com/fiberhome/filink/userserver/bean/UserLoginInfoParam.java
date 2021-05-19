package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * 网关服务查询登陆信息传入实体
 *
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@Data
public class UserLoginInfoParam {
    /**
     * 检验用户是否可以登录传入信息实体
     */
    private UserParameter loginUserParameter;
    /**
     * 查询用户详情传入实体
     */
    private UserParameter userParameter;
}
