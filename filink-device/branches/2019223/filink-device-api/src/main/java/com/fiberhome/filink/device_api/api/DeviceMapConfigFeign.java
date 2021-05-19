package com.fiberhome.filink.device_api.api;

import com.fiberhome.filink.device_api.fallback.DeviceMapConfigFeignFallback;
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
     * @return 插入条数
     */
    @PostMapping("/deviceMapConfig/insertConfig/{userId}")
    boolean insertConfigBatch(@PathVariable(value = "userId") String userId);

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 成功
     */
    @DeleteMapping("/deviceMapConfig/deletedConfig")
    boolean deletedConfigByUserIds(@RequestBody List<String> userIds);
}
