package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.config.ConfigParam;
import com.fiberhome.filink.fdevice.bean.config.Configuration;
import com.fiberhome.filink.fdevice.bean.config.DetailParam;
import com.fiberhome.filink.fdevice.bean.config.FiLinkDeviceConfig;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.ConstantParam;
import com.fiberhome.filink.fdevice.constant.device.DeviceAuthEnum;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.constant.device.DeviceLockAuthEnum;
import com.fiberhome.filink.fdevice.constant.device.DeviceType;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceConfigException;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.FiLinkDeviceXmlResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.rfidapi.api.TemplateServiceFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private TemplateServiceFeign templateServiceFeign;

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
        //获取语言环境
        String language = RequestInfoUtils.getLanguage();
        //获取设施配置
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType, language);
        List<DetailParam> detailParams = deviceConfig.getDetailParams();
        //返回结果
        List<DetailParam> result = new ArrayList<>(detailParams);
        // 电子锁的判断
        if (deviceType.equals(DeviceType.Optical_Box.getCode())
                || deviceType.equals(DeviceType.Well.getCode())
                || deviceType.equals(DeviceType.Outdoor_Cabinet.getCode())) {
            // 看用户有无电子锁的权限
            List<String> deviceTypes = getDeviceTypes(getCurrentUser());
            String code = DeviceLockAuthEnum.getCodeByParent(deviceType);
            if (!deviceTypes.contains(code)) {
                //不存在电子锁
                result = detailParams.stream().filter(
                        (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                        .collect(Collectors.toList());
            } else {
                //根据主控类型判断是否需要显示
                result = queryControlInfo(deviceInfo, detailParams, result);
            }
        }
        // 智能标签  菜单移除  光交箱 、配线架、接头盒

        if (deviceType.equals(DeviceType.Optical_Box.getCode()) ||
                deviceType.equals(DeviceType.Distribution_Frame.getCode()) ||
                deviceType.equals(DeviceType.Junction_Box.getCode())) {
            // 看用户有无配置智能标签的权限
            List<String> deviceTypes = getDeviceTypes(getCurrentUser());
            String code = DeviceAuthEnum.getCodeByParent(deviceType);
            if (!deviceTypes.contains(code)) {
                //不存在智能标签 智能标签id 为 8  详情见 deviceConfig.xml
                result = result.stream().filter(obj -> !obj.getId().equals("8")).collect(Collectors.toList());
            }
            if (!templateServiceFeign.isExistBus(deviceInfo.getDeviceId())) {
                //判断是否配置了信息 没有配置也不让看 id 为 9  详情见 deviceConfig.xml
                result = result.stream().filter(obj -> !obj.getId().equals("9")).collect(Collectors.toList());
            }
        }

        return ResultUtils.success(result);
    }

    /**
     * 判断是否需要显示主控
     *
     * @param deviceInfo   设施信息
     * @param detailParams 模块信息
     * @param result       显示结果
     * @return 显示模块集合
     */
    private List<DetailParam> queryControlInfo(DeviceInfo deviceInfo, List<DetailParam> detailParams, List<DetailParam> result) {
        //获取设施的主控
        List<ControlParam> controlParams = controlFeign.getControlParams(deviceInfo.getDeviceId());
        //根据有无主控决定设施详情展示面板
        if (!ObjectUtils.isEmpty(controlParams)) {
            //若是无源锁
            String hostType = ConstantParam.PASSIVE_HOST;
            ControlParam controlParam = controlParams.get(0);
            if (controlParam.getHostType().equals(hostType)) {
                result = detailParams.stream().filter(
                        (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                        .collect(Collectors.toList());
                //加上无源主控门锁模块
                DetailParam detailParam = new DetailParam();
                detailParam.setId(ConstantParam.PASSIVE_HOST);
                result.add(detailParam);
            }
        } else {
            //没有主控
            result = detailParams.stream().filter(
                    (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                    .collect(Collectors.toList());
        }
        return result;
    }


    /**
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户权限信息
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        Object userObj = userFeign.queryCurrentUser(userId, token);
        //转换为User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * 获取用户设施类型信息
     *
     * @param user
     * @return
     */
    private static List<String> getDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * 根据设施id获取参数下发配置项
     *
     * @param deviceType 设施类型
     * @return 配置项
     */
    @Override
    public Result getParamsConfig(String deviceType) {
        String language = RequestInfoUtils.getLanguage();
        //获取设施配置对象
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType, language);
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
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType, null);
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
        Map deviceConfigMap;
        try {
            ClassPathResource classPathResource = new ClassPathResource("/config/deviceConfig.xml");
            inputStream = classPathResource.getInputStream();
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("deviceConfig xml is not exist>>>>>>>>");
        }
        try {
            deviceConfigMap = FiLinkDeviceXmlResolver.resolveDeviceXml(inputStream);
        } catch (Exception e) {
            throw new FiLinkDeviceConfigException("resolve deviceConfig xml failed>>>>>");
        }
        RedisUtils.remove(DeviceConstant.DEVICE_CONFIG_KEY);
        RedisUtils.hSetMap(DeviceConstant.DEVICE_CONFIG_KEY, deviceConfigMap);
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
    private FiLinkDeviceConfig getDeviceConfig(String deviceType, String language) {
        Object deviceConfigObj = getDeviceConfigObj(deviceType, language);
        //缓存没有重新加载
        if (deviceConfigObj == null) {
            //重新加载配置文件
            initDeviceConfigToRedis();
            deviceConfigObj = getDeviceConfigObj(deviceType, language);
        }
        //deviceConfig为空，没有该配置
        if (deviceConfigObj == null) {
            throw new FiLinkDeviceException("the config of device: " + deviceType + " is not exist>>>>>");
        }
        if (deviceConfigObj instanceof FiLinkDeviceConfig) {
            return (FiLinkDeviceConfig) deviceConfigObj;
        } else {
            throw new FiLinkDeviceException("the config of device: " + deviceType + " is error>>>>>");
        }
    }

    /**
     * 从缓存获取deviceConfig
     *
     * @param deviceType 设施类型
     * @param language   语言
     * @return 对象实体
     */
    private Object getDeviceConfigObj(String deviceType, String language) {
        //获取key值
        String code = DeviceConstant.DEVICE_CONFIG_CODE;
        String key = code + deviceType;
        //语言环境为空
        if (StringUtils.isEmpty(language)) {
            Map<String, Map<String, FiLinkDeviceConfig>> deviceConfigMap = (Map) RedisUtils.hGetMap(DeviceConstant.DEVICE_CONFIG_KEY);
            //redis为空,返回空
            if (deviceConfigMap == null || deviceConfigMap.size() == 0) {
                return null;
                //返回map中第一个值
            } else {
                return deviceConfigMap.entrySet().iterator().next().getValue().get(key);
            }
            //语言环境不为空
        } else {
            Map<String, FiLinkDeviceConfig> deviceConfig = (Map<String, FiLinkDeviceConfig>) RedisUtils.hGet(DeviceConstant.DEVICE_CONFIG_KEY, language);
            if (deviceConfig == null) {
                return null;
            } else {
                return deviceConfig.get(key);
            }
        }
    }


    @PostConstruct
    public void init() {
        detailParamListWithControl = new ArrayList<>();
        detailParamListWithControl.add("3");
        detailParamListWithControl.add("4");
        detailParamListWithControl.add("5");
    }
}
