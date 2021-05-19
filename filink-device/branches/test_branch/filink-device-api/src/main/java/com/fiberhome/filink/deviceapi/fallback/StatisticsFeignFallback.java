package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.StatisticsFeign;
import com.fiberhome.filink.deviceapi.bean.SensorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 11:37
 * @Description: com.fiberhome.filink.deviceapi.fallback
 * @version: 1.0
 */
@Slf4j
@Component
public class StatisticsFeignFallback implements StatisticsFeign {
    @Override
    public Result updateSensorLimit(SensorInfo sensorInfo) {
        log.warn("》》》》》》》》》》》》feign调用熔断");
        return null;
    }

}
