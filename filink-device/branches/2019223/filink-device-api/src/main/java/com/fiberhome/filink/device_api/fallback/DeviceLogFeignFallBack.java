package com.fiberhome.filink.device_api.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.device_api.api.DeviceLogFeign;
import com.fiberhome.filink.device_api.bean.DeviceLog;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 * 设施日志feign熔断类
 * @author CongcaiYu
 */
@Log4j
@Component
public class DeviceLogFeignFallBack implements DeviceLogFeign {

    /**
     * 新增设施日志
     * @param deviceLog 设施日志
     * @return 操作结果
     */
    @Override
    public Result saveDeviceLog(DeviceLog deviceLog) {
        log.info("save device log failed>>>>>>");
        return null;
    }
}
