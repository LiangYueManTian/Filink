package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.config.ConfigParam;
import com.fiberhome.filink.fdevice.bean.config.Configuration;
import com.fiberhome.filink.fdevice.bean.config.DetailParam;
import com.fiberhome.filink.fdevice.bean.config.FiLinkDeviceConfig;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.constant.device.DeviceType;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceConfigException;
import com.fiberhome.filink.fdevice.exception.FilinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.utils.FiLinkDeviceXmlResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设施配置实现类
 *
 * @author CongcaiYu
 */
@Slf4j
@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    @Autowired
    private ControlFeign controlFeign;
    /**
     * 有源主控设施详情显示模块id
     */
    private List<String> detailParamListWithControl;

    /**
     * 根据设施id查询详情模块code
     *
     * @param deviceInfo 设施id
     * @return 模块code
     */
    @Override
    public Result getDetailCode(DeviceInfo deviceInfo) {
        //设施类型
        String deviceType = deviceInfo.getDeviceType();
        //获取设施配置
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType);
        List<DetailParam> detailParams = deviceConfig.getDetailParams();
        //返回结果
        List<DetailParam> result = new ArrayList<>(detailParams);
        if(deviceType.equals(DeviceType.Optical_Box.getCode()) || deviceType.equals(DeviceType.Well.getCode())){
            //获取设施的主控
            List<ControlParam> controlParams = controlFeign.getControlParams(deviceInfo.getDeviceId());
            //根据有无主控决定设施详情展示面板
            if (!ObjectUtils.isEmpty(controlParams)) {
                //若是无源锁
                String hostType = "0";
                ControlParam controlParam = controlParams.get(0);
                if (controlParam.getHostType().equals(hostType)) {
                    result = detailParams.stream().filter(
                            (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                            .collect(Collectors.toList());
                }
            } else {
                //没有主控
                result = detailParams.stream().filter(
                        (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                        .collect(Collectors.toList());
            }
        }

        return ResultUtils.success(result);
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
     * 获取配置默认值
     *
     * @param deviceType 设施类型
     * @return配置默认值
     */
    @Override
    public Map<String, String> getDefaultParams(String deviceType) {
        Map<String, String> defaultParams = new HashMap<>(64);
        //获取设施配置对象
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType);
        List<Configuration> configurations = deviceConfig.getConfigurations();
        for (Configuration configuration : configurations) {
            List<ConfigParam> configParams = configuration.getConfigParams();
            for (ConfigParam configParam : configParams) {
                //key:id  value:defaultValue
                defaultParams.put(configParam.getId(), configParam.getDefaultValue());
            }
        }

        return defaultParams;
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
            RedisUtils.hSet(DeviceConstant.DEVICE_CONFIG_KEY, key, deviceConfig);
        }
    }

    /**
     * 得到配置的校验正则表达式
     *
     * @return
     */
    @Override
    public Map<String, String> getConfigPatterns() {
        InputStream inputStream;
        Map<String, String> regexMap = new HashMap<>(64);
        try {
            ClassPathResource classPathResource = new ClassPathResource("/config/deviceConfig.xml");
            inputStream = classPathResource.getInputStream();
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("deviceConfig xml is not exist>>>>>>>>");
        }
        try {
            regexMap = FiLinkDeviceXmlResolver.resolveConfigPatternXmlToMap(inputStream);
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("resolve deviceConfig xml failed>>>>>");
        }
        return regexMap;
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
        String code = DeviceConstant.DEVICE_CONFIG_CODE;
        String key = code + deviceType;
        return RedisUtils.hGet(DeviceConstant.DEVICE_CONFIG_KEY, key);
    }


    @PostConstruct
    public void init(){
        detailParamListWithControl = new ArrayList<>();
        detailParamListWithControl.add("2");
        detailParamListWithControl.add("3");
        detailParamListWithControl.add("4");
        detailParamListWithControl.add("5");
    }
}
