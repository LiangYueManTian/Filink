package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.userserver.bean.LoginInfoBean;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserLoginInfoParam;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.service.UserExtraService;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.impl.UserExtraServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登陆调用控制层
 *
 * @Author: qiqizhu@wistronits.com
 * Date:2019/7/19
 */
@RestController
@RequestMapping("/userLoginController")
@Slf4j
public class UserLoginController {
    /**
     * 注入用户service
     */
    @Autowired
    private UserService userService;
    /**
     * 部门和用户之间的服务类
     */
    @Autowired
    private UserExtraService userExtraService;

    @PostMapping("/queryUserLoginInfo")
    public LoginInfoBean queryUserLoginInfo(@RequestBody UserLoginInfoParam userLoginInfoParam) {
        LoginInfoBean loginInfoBean = new LoginInfoBean();
        UserParameter loginUserParameter = userLoginInfoParam.getLoginUserParameter();
        String userName = loginUserParameter.getUserName();
        log.info("User {} login：Check if the user can login", userName);
        Integer loginCode = userExtraService.validateUserLogin(loginUserParameter);
        loginInfoBean.setLoginCode(loginCode);
        if (!ResultCode.SUCCESS.equals(loginCode)) {
            log.info("User {} login：Check if the user can login without passing", userName);
            return loginInfoBean;
        }
        ThreadLocal<LoginSysInfo> loginSysInfoThreadLocal = UserExtraServiceImpl.LOGIN_SYS_INFO_THREAD_LOCAL;
        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        LoginSysInfo loginSysInfo = loginSysInfoThreadLocal.get();
        MenuTemplateAndMenuInfoTree showMenuTemplate = loginSysInfo.getShowMenuTemplate();
        BeanUtils.copyProperties(showMenuTemplate, menuTemplateAndMenuInfoTree);
        loginInfoBean.setShowMenuTemplate(menuTemplateAndMenuInfoTree);
        log.info("User {} login：Query user related information", userName);
        User user = userService.queryUserByName(userLoginInfoParam.getUserParameter());
        loginInfoBean.setUser(user);
        return loginInfoBean;
    }
}
