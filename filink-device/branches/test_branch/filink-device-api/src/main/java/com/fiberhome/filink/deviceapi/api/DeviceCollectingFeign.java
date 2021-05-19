package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DeviceCollecting;
import com.fiberhome.filink.deviceapi.fallback.DeviceCollectingFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>
 * 我的关注 feign
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/18
 */
@FeignClient(name = "filink-device-server", path = "/deviceCollecting", fallback = DeviceCollectingFeignFallback.class)
public interface DeviceCollectingFeign {
    /**
     * 设施/用户删除时，根据deviceId/userId删除关联表的记录
     * DeviceCollecting同时只可指定一个属性deviceId or userId
     *
     * @param deviceCollecting
     * @return
     * @throws Exception
     */
    @PostMapping("/unFollowById")
    Result delDeviceCollectingByDeviceId(DeviceCollecting deviceCollecting) throws Exception;

}
