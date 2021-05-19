package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.userserver.bean.UserLoginInfoParam;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.service.UserExtraService;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.impl.UserExtraServiceImpl;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class UserLoginControllerTest {

    @Tested
    private UserLoginController userLoginController;

    /**
     * 注入用户service
     */
    @Injectable
    private UserService userService;
    /**
     * 部门和用户之间的服务类
     */
    @Injectable
    private UserExtraService userExtraService;

    @Test
    public void queryUserLoginInfoTest() {
        UserLoginInfoParam userLoginInfoParam = new UserLoginInfoParam();
        userLoginInfoParam.setLoginUserParameter(new UserParameter());
        new Expectations() {
            {
                userExtraService.validateUserLogin((UserParameter) any);
                result = 1;
            }
        };
        userLoginController.queryUserLoginInfo(userLoginInfoParam);
        LoginSysInfo loginSysInfo = new LoginSysInfo();
        loginSysInfo.setShowMenuTemplate(new MenuTemplateAndMenuInfoTree());
        UserExtraServiceImpl.LOGIN_SYS_INFO_THREAD_LOCAL.set(loginSysInfo);
        new Expectations() {
            {
                userExtraService.validateUserLogin((UserParameter) any);
                result = 0;
            }
        };
        userLoginController.queryUserLoginInfo(userLoginInfoParam);
    }
}