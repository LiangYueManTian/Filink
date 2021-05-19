package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.SensorInfo;
import com.fiberhome.filink.deviceapi.fallback.StatisticsFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 11:36
 * @Description: com.fiberhome.filink.deviceapi.api
 * @version: 1.0
 */
@FeignClient(name = "filink-device-server", path = "/statistics", fallback = StatisticsFeignFallback.class)
public interface StatisticsFeign {

    /**
     * 更新设施传感器极限值
     * @param sensorInfo
     * @return
     */
    @PostMapping("/feign/updateSensorLimit")
    Result updateSensorLimit(SensorInfo sensorInfo);

}
