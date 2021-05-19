package com.fiberhome.filink.loginSysInfo.fallback;

import com.fiberhome.filink.loginSysInfo.api.LoginSysInfoFeign;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/7/19
 */
@Slf4j
@Component
public class LoginSysInfoFeignFallback implements LoginSysInfoFeign {
    private static final String INFO = "system service feign call blown》》》》》》》》》》";

    @Override
    public LoginSysInfo queryLoginSysInfo(LoginSysParam loginSysParam) {
        log.info(INFO);
        return null;
    }
}
