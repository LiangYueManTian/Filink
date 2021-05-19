package com.fiberhome.filink.device.runner;

import com.fiberhome.filink.fdevice.service.area.impl.AreaInfoServiceImpl;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 设施项目系统启动加载
 * @author chaofang@wistronits.com
 * @since 2019/2/21
 */
@Component
public class DeviceApplicationRunner implements ApplicationRunner {
    /**
     * 设施服务
     */
    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private DeviceConfigService deviceConfigService;
    @Autowired
    private AreaInfoServiceImpl areaInfoService;
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化设施信息到Redis
        if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
            RedisUtils.remove(DeviceConstant.DEVICE_GIS_MAP);
        }
        deviceInfoService.queryDeviceAreaAll();
        //初始化设施配置到redis
        deviceConfigService.initDeviceConfigToRedis();
        RedisUtils.remove(DeviceConstant.AREAINFO_FOREIGN);
        areaInfoService.getAreaInfoFromRedis();
    }
}
