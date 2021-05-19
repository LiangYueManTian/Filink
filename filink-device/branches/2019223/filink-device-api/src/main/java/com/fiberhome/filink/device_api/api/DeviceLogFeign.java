package com.fiberhome.filink.device_api.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.device_api.bean.DeviceLog;
import com.fiberhome.filink.device_api.fallback.DeviceLogFeignFallBack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 设施日志feign
 * @author CongcaiYu
 */
@FeignClient(name = "filink-device-server", fallback = DeviceLogFeignFallBack.class)
public interface DeviceLogFeign {

    /**
     * 新增设施日志
     * @param deviceLog
     * @return 操作结果
     */
    @PostMapping("/deviceLog/save")
    Result saveDeviceLog(@RequestBody DeviceLog deviceLog);
}

