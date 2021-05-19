package com.fiberhome.filink.platformappapi.api;


import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.platformappapi.fallback.PlatformAppFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;


/**
 * 设备平台应用/产品信息远程调用
 * @author chaofang@wistronits.com
 * @since 2019-04-16
 */
@FeignClient(name = "filink-system-server", path = "/platformApp", fallback = PlatformAppFeignFallback.class)
public interface PlatformAppFeign {
    /**
     * 根据应用/产品 ID获取应用/产品信息
     * @param appId 应用/产品 ID
     * @return 应用/产品信息
     */
    @GetMapping("/findPlatformAppInfoByAppId/{appId}")
    PlatformAppInfo findPlatformAppInfoByAppId(@PathVariable(value = "appId") String appId);

    /**
     *根据平台类型获取应用/产品信息
     * @param platformType 平台类型
     * @return 应用/产品信息Map
     */
    @GetMapping("/findPlatformAppInfoMapByType/{platformType}")
    Map<String, PlatformAppInfo> findPlatformAppInfoMapByType(@PathVariable(value = "platformType") Integer platformType);

}