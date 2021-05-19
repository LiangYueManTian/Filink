package com.fiberhome.filink.loginSysInfo.bean;

import com.fiberhome.filink.security.bean.IpAddress;
import lombok.Data;

/**
 * 登陆查询系统信息传入参数
 *
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@Data
public class LoginSysParam {
    /**
     * 查询ip参数
     */
    private IpAddress ipAddress;
}
