package com.fiberhome.filink.systemserver.bean;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import lombok.Data;

/**
 * 登陆系统服务信息
 *
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@Data
public class LoginSysInfo {
    /**
     * 当前证书实体
     */
    private License currentLicense;
    /**
     * 安全策略返回
     */
    private Result securityStrategyResult;
    /**
     * 查询ip结果
     */
    private Result ipResult;
    /**
     * 查询展示菜单结果
     */
    private MenuTemplateAndMenuInfoTree showMenuTemplate;
}
