package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo 集群测试
 */
@Slf4j
@Component
public class DeviceThread {

    @Autowired
    private DeviceFeign deviceFeign;

    private AtomicInteger success = new AtomicInteger(0);
    private AtomicInteger nullDevice = new AtomicInteger(0);
    private AtomicInteger rongduan = new AtomicInteger(0);

    @Async
    public void queryDevice(String deviceId) {
        DeviceInfoDto deviceById = deviceFeign.getDeviceById(deviceId);
        if (deviceById != null && !StringUtils.isEmpty(deviceById.getDeviceId())) {
            log.info("device success : " + success.incrementAndGet());
            return;
        }
        if (deviceById == null) {
            log.info("device rongduan : " + rongduan.incrementAndGet());
            return;
        }
        if (StringUtils.isEmpty(deviceById.getDeviceId())) {
            log.info("device null : " + nullDevice.incrementAndGet());
        }
    }

    public Map get() {
        Map<String, Integer> map = new HashMap<>();
        map.put("success", success.get());
        map.put("nullDevice", nullDevice.get());
        map.put("rongduan",rongduan.get());
        return map;
    }

    //
    public void reset() {
        success.set(0);
        nullDevice.set(0);
        rongduan.set(0);
    }
//
//    private synchronized void add(DeviceInfoDto deviceById ){
//        deviceInfoDtos.add(deviceById);
//    }
}
