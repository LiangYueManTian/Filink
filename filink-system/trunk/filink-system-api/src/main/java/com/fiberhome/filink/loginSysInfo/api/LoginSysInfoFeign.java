package com.fiberhome.filink.loginSysInfo.api;


import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysParam;
import com.fiberhome.filink.loginSysInfo.fallback.LoginSysInfoFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @author qiqizhu@wistronits.com
 * @Date: 2019/1/25 16:44
 */
@FeignClient(name = "filink-system-server", path = "/loginSysInfoController",fallback = LoginSysInfoFeignFallback.class)
public interface LoginSysInfoFeign {
    /**
     * 查询登陆系统信息
     * @param  loginSysParam 传入参数
     * @return 查询结果
     */
    @PostMapping("/queryLoginSysInfo")
    LoginSysInfo queryLoginSysInfo(@RequestBody LoginSysParam loginSysParam);
}
