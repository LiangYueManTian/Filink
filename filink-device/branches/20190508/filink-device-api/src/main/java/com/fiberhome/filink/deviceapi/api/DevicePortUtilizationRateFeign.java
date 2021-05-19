package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.deviceapi.fallback.DevicePortUtilizationRateFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 设施端口使用率信息
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
@FeignClient(name = "filink-device-server", path = "/portStatistics", fallback = DevicePortUtilizationRateFeignFallback.class)
public interface DevicePortUtilizationRateFeign {

    /**
     * 新增
     *
     * @param devicePortUtilizationRate 设施端口使用率信息
     * @return Integer
     */
    @PostMapping("/addPortStatistics")
    Integer addPortStatistics(@RequestBody List<DevicePortUtilizationRate> devicePortUtilizationRate);

    /**
     * 查询TopN
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return Result
     */
    @PostMapping("/queryPortTopN")
    Result queryPortTopNo(@RequestBody TopNoPortStatisticsReq topNoPortStatisticsReq);
}
