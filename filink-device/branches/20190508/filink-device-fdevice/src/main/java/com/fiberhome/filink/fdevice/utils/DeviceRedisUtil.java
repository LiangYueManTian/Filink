package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.dto.HomeDeviceInfoDto;
import com.fiberhome.filink.redis.RedisUtils;

import java.util.HashMap;
import java.util.Map;


public class DeviceRedisUtil {

    /**
     * 新增设施缓存
     *
     * @param homeDeviceInfoDto 设施
     */
    public static void addDeviceRedis(HomeDeviceInfoDto homeDeviceInfoDto) {
        // Redis有缓存信息，从Redis获取
        Map<String, HomeDeviceInfoDto> deviceMap;
        String areaId = homeDeviceInfoDto.getAreaId();
        //缓存是否有这个区域
        if (RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, areaId)) {
            deviceMap = (Map) RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, areaId);
        } else {
            deviceMap = new HashMap<>(128);
        }
        deviceMap.put(homeDeviceInfoDto.getDeviceId(), homeDeviceInfoDto);
        //存入
        RedisUtils.hSet(DeviceConstant.DEVICE_GIS_MAP, areaId, deviceMap);
    }
}
