package com.fiberhome.filink.security.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.IpAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 安全策略远程调用
 * @author chaofang@wistronits.com
 * @since 2019-04-10
 */
@Slf4j
@Component
public class SecurityFeignFallback implements SecurityFeign {
    /**
     * 查询账号安全策略
     *
     * @return 账号安全策略
     */
    @Override
    public Result queryAccountSecurity() {
        log.info("query account security strategy failed******************");
        return null;
    }

    /**
     * 查询密码安全策略
     *
     * @return 密码安全策略
     */
    @Override
    public Result queryPasswordSecurity() {
        log.info("query password security strategy failed******************");
        return null;
    }

    /**
     * 查询IP地址是否在访问范围内
     *
     * @param ipAddress IP地址
     * @return Result
     */
    @Override
    public Result hasIpAddress(IpAddress ipAddress) {
        log.info("query ip address  failed******************");
        return null;
    }
}
