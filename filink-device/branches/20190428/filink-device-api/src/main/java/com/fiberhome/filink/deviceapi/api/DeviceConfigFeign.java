package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.deviceapi.fallback.DeviceConfigFeifnFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * <p>
 * 设施配置 feign
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/28
 */
@FeignClient(name = "filink-device-server", path = "/deviceConfig", fallback = DeviceConfigFeifnFallback.class)
public interface DeviceConfigFeign {
    /**
     * 获取设施配置字段的校验规则
     *
     * @return <字段 字段检验规则>集合
     * @throws Exception
     */
    @GetMapping("/getConfigPatterns")
    Map<String, String> getConfigPatterns() throws Exception;
}
