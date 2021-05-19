package com.fiberhome.filink.security.api;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.security.bean.IpAddress;
import com.fiberhome.filink.security.fallback.SecurityFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 安全策略远程调用
 * @author chaofang@wistronits.com
 * @since 2019-04-10
 */
@FeignClient(name = "filink-system-server", fallback = SecurityFeignFallback.class)
public interface SecurityFeign {
    /**
     * 查询账号安全策略
     * @return 账号安全策略
     */
    @GetMapping("/securityStrategy/queryAccountSecurity")
    Result queryAccountSecurity();

    /**
     * 查询密码安全策略
     * @return 密码安全策略
     */
    @GetMapping("/securityStrategy/queryPasswordSecurity")
    Result queryPasswordSecurity();

    /**
     * 查询IP地址是否在访问范围内
     * @param ipAddress IP地址
     * @return Result
     */
    @PostMapping("/ipRange/hasIpAddress")
    Result hasIpAddress(@RequestBody IpAddress ipAddress);
}
