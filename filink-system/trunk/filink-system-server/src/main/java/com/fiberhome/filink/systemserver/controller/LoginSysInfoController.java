package com.fiberhome.filink.systemserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.service.LicenseInfoService;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.service.MenuService;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
import com.fiberhome.filink.securitystrategy.service.SecurityStrategyService;
import com.fiberhome.filink.systemserver.bean.LoginSysInfo;
import com.fiberhome.filink.systemserver.bean.LoginSysParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登陆系统信息控制层
 *
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@RestController
@RequestMapping("/loginSysInfoController")
@Slf4j
public class LoginSysInfoController {
    /**
     * 注入licenseInfoService
     */
    @Autowired
    private LicenseInfoService licenseInfoService;
    /**
     * 注入安全策略服务层
     */
    @Autowired
    private SecurityStrategyService securityStrategyService;
    /**
     * 注入ipRangeService
     */
    @Autowired
    private IpRangeService ipRangeService;
    /**
     * 注入菜单服务层
     */
    @Autowired
    private MenuService menuService;

    /**
     * 查询登陆系统信息
     *
     * @param loginSysParam 传入参数
     * @return 查询结果
     */
    @PostMapping("/queryLoginSysInfo")
    public LoginSysInfo queryLoginSysInfo(@RequestBody LoginSysParam loginSysParam) {
        log.info("User login：Query menu, security policy, license");
        License currentLicense = licenseInfoService.getCurrentLicense();
        Result securityStrategyResult = securityStrategyService.queryAccountSecurity();
        Result ipResult = ipRangeService.hasIpAddress(loginSysParam.getIpAddress());
        MenuTemplateAndMenuInfoTree showMenuTemplate = menuService.getShowMenuTemplate();
        LoginSysInfo loginSysInfo = new LoginSysInfo();
        loginSysInfo.setCurrentLicense(currentLicense);
        loginSysInfo.setIpResult(ipResult);
        loginSysInfo.setSecurityStrategyResult(securityStrategyResult);
        loginSysInfo.setShowMenuTemplate(showMenuTemplate);
        return loginSysInfo;
    }
}
