package com.fiberhome.filink.platformappapi.fallback;


import com.fiberhome.filink.platformappapi.api.PlatformAppFeign;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 设备平台应用/产品信息远程调用
 * @author chaofang@wistronits.com
 * @since 2019-05-05
 */
@Slf4j
@Component
public class PlatformAppFeignFallback implements PlatformAppFeign {
    /**
     * 根据应用/产品 ID获取应用/产品信息
     *
     * @param appId 应用/产品 ID
     * @return 应用/产品信息
     */
    @Override
    public PlatformAppInfo findPlatformAppInfoByAppId(String appId) {
        log.info("find platform app info hystrix》》》》》》》》》》");
        return null;
    }

    /**
     * 根据平台类型获取应用/产品信息
     *
     * @param platformType 平台类型
     * @return 应用/产品信息Map
     */
    @Override
    public Map<String, PlatformAppInfo> findPlatformAppInfoMapByType(Integer platformType) {
        log.info("find platform app info map by platform type hystrix》》》》》》》》》》");
        return null;
    }
}
