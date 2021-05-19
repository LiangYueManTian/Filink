package com.fiberhome.filink.systemserver.runner;

import com.esotericsoftware.minlog.Log;
import com.fiberhome.filink.license.service.LicenseInfoService;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统项目系统启动加载
 * @author chaofang@wistronits.com
 * @since 2019/4/8
 */
@Component
public class SystemApplicationRunner implements ApplicationRunner {
    /**
     * 访问控制列表
     */
    @Autowired
    private IpRangeService ipRangeService;

    @Autowired
    private LicenseInfoService licenseInfoService;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) {
        //初始化设施信息到Redis
        if (RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS)) {
            RedisUtils.remove(SecurityStrategyConstants.IP_RANGE_REDIS);
        }
        //初始化配置到redis
        ipRangeService.setIpRangeRedis();

        //初始化License数据到redis
        try {
            licenseInfoService.updateLicenseToRedis();
            licenseInfoService.refreshLicenseThreshold();
        } catch (Throwable e) {
            Log.warn(e.getMessage());
        }

    }
}
