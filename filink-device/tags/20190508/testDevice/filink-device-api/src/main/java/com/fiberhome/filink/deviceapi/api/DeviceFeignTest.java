package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.deviceapi.bean.Device;
import com.fiberhome.filink.deviceapi.fallback.DeviceFeignTestFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:20 PM
 */
@FeignClient(name = "filink-device-server", fallback = DeviceFeignTestFallback.class)
public interface DeviceFeignTest {

    /**
     * feign测试
     * @return
     */
    @GetMapping("/device/feignTest")
    Device feignTest();
}
