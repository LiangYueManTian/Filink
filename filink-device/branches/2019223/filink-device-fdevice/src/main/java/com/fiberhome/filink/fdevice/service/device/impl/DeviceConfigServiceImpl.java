package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.config.Configuration;
import com.fiberhome.filink.fdevice.bean.config.FiLinkDeviceConfig;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceConfigException;
import com.fiberhome.filink.fdevice.exception.FilinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.utils.Constant;
import com.fiberhome.filink.fdevice.utils.FiLinkDeviceXmlResolver;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * 设施配置实现类
 *
 * @author CongcaiYu
 */
@Slf4j
@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    /**
     * 根据设施id查询详情模块code
     *
     * @param deviceType 设施类型
     * @return 模块code
     */
    @Override
    public Result getDetailCode(String deviceType) {
        //获取设施配置
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType);
        return ResultUtils.success(deviceConfig.getDetailParams());
    }


    /**
     * 根据设施id获取参数下发配置项
     *
     * @param deviceType 设施类型
     * @return 配置项
     */
    @Override
    public Result getParamsConfig(String deviceType) {
        //获取设施配置对象
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType);
        List<Configuration> configurations = deviceConfig.getConfigurations();
        return ResultUtils.success(configurations);
    }

    /**
     * 初始化设施配置到redis中
     */
    @Override
    public void initDeviceConfigToRedis() {
        InputStream inputStream;
        List<FiLinkDeviceConfig> deviceConfigs;
        try {
            ClassPathResource classPathResource = new ClassPathResource("/config/deviceConfig.xml");
            inputStream = classPathResource.getInputStream();
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("deviceConfig xml is not exist>>>>>>>>");
        }
        try {
            deviceConfigs = FiLinkDeviceXmlResolver.resolveDeviceXml(inputStream);
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("resolve deviceConfig xml failed>>>>>");
        }
        //循环deviceConfig存到redis
        for (FiLinkDeviceConfig deviceConfig : deviceConfigs) {
            String code = deviceConfig.getCode();
            String deviceType = deviceConfig.getDeviceType();
            //redis key
            String key = code + deviceType;
            RedisUtils.hSet(Constant.DEVICE_CONFIG_KEY,key,deviceConfig);
        }
    }


    /**
     * 获取设施配置
     *
     * @param deviceType 设施类型
     * @return 设施配置对象
     */
    private FiLinkDeviceConfig getDeviceConfig(String deviceType) {
        Object deviceConfigObj = getDeviceConfigObj(deviceType);
        //缓存没有重新加载
        if (deviceConfigObj == null) {
            //重新加载配置文件
            initDeviceConfigToRedis();
            deviceConfigObj = getDeviceConfigObj(deviceType);
        }
        //deviceConfig为空，没有该配置
        if (deviceConfigObj == null) {
            throw new FilinkDeviceException("the config of device: " + deviceType + " is not exist>>>>>");
        }
        if (deviceConfigObj instanceof FiLinkDeviceConfig) {
            return (FiLinkDeviceConfig) deviceConfigObj;
        } else {
            throw new FilinkDeviceException("the config of device: " + deviceType + " is error>>>>>");
        }
    }

    /**
     * 从缓存获取deviceConfig
     *
     * @param deviceType 设施类型
     * @return 对象实体
     */
    private Object getDeviceConfigObj(String deviceType) {
        //从redis中获取设施配置
        // todo 设施配置key
        String code = "filink";
        String key = code + deviceType;
        return RedisUtils.hGet(Constant.DEVICE_CONFIG_KEY, key);
    }
}
