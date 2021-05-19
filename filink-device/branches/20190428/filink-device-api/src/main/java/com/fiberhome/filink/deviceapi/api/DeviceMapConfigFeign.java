package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.deviceapi.fallback.DeviceMapConfigFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 *  首页地图和设施类型配置
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-12
 */
@FeignClient(name = "filink-device-server", fallback = DeviceMapConfigFeignFallback.class)
public interface DeviceMapConfigFeign {
    /**
     * 批量插入用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return boolean
     */
    @PostMapping("/deviceMapConfig/insertConfig/{userId}")
    boolean insertConfigBatch(@PathVariable(value = "userId") String userId);

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return boolean
     */
    @DeleteMapping("/deviceMapConfig/deletedConfig")
    boolean deletedConfigByUserIds(@RequestBody List<String> userIds);

    /**
     *  批量插入多个用户首页地图设施配置信息
     *
     * @param userIds 多个用户
     * @return boolean
     */
    @PostMapping("/deviceMapConfig/insertConfigBatch")
    boolean insertConfigBatchUsers(@RequestBody List<String> userIds);
}
