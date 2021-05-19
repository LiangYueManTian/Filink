package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DevicePortUtilizationRateFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 设施端口使用率信息feign熔断类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
@Log4j
@Component
public class DevicePortUtilizationRateFeignFallback implements DevicePortUtilizationRateFeign {
    /**
     * 新增
     *
     * @param devicePortUtilizationRate 设施端口使用率信息
     * @return Integer
     */
    @Override
    public Integer addPortStatistics(List<DevicePortUtilizationRate> devicePortUtilizationRate) {
        log.info("save device port utilization rate failed>>>>>>");
        return null;
    }

    /**
     * 查询
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return Result
     */
    @Override
    public Result queryPortTopNo(TopNoPortStatisticsReq topNoPortStatisticsReq) {
        log.info("query device port utilization rate failed>>>>>>");
        return null;
    }
}
