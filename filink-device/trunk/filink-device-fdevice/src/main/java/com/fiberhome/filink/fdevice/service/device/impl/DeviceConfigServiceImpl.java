package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.config.*;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.*;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceConfigException;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.FiLinkDeviceXmlResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.rfidapi.api.TemplateServiceFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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

    @Autowired
    private DiscoveryClient discoveryClient;


    /**
     * 获取设施集列表
     *
     * @return 设施集集合
     */
    @Override
    public Result getDeviceType() {
        final String lockServerName = "filink-lock-server";
        final String rfidServerName = "filink-rfid-server";
        //获取语言环境
        String language = RequestInfoUtils.getLanguage();
        //从redis获取deviceConfig
        Map<String, FiLinkDeviceConfig> deviceConfigMap = getDeviceConfigObj(language);
        if (deviceConfigMap == null) {
            //redis为空,重新加载
            initDeviceConfigToRedis();
            deviceConfigMap = getDeviceConfigObj(language);
        }
        //如果仍为空,则未配置该文件
        if (deviceConfigMap == null || deviceConfigMap.size() == 0) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_TYPE_NOT_CONFIGURED,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_TYPE_NOT_CONFIGURED));
        }
        //判断电子锁和rfid服务是否启动
        boolean lockIsRegistry = serverIsRegistry(lockServerName);
        boolean rfidIsRegistry = serverIsRegistry(rfidServerName);
        //遍历设施类型
        List<DeviceTypeNode> deviceTypeNodeList = new ArrayList<>();
        for (Map.Entry<String, FiLinkDeviceConfig> entry : deviceConfigMap.entrySet()) {
            DeviceTypeNode deviceTypeNode = generateDeviceType(entry.getValue(), lockIsRegistry, rfidIsRegistry);
            deviceTypeNodeList.add(deviceTypeNode);
        }
        return ResultUtils.success(deviceTypeNodeList);
    }

    /**
     * 判断服务是否可用
     *
     * @param serverName 服务名称
     * @return true:可用 false:不可用
     */
    private boolean serverIsRegistry(String serverName) {
        if (StringUtils.isEmpty(serverName)) {
            return false;
        }
        //获取注册中心上注册的服务
        List<String> instances = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(instances)) {
            return false;
        }
        return instances.contains(serverName);
    }


    /**
     * 生成deviceType对象
     *
     * @param deviceConfig deviceConfig配置类
     * @return deviceType对象
     */
    private DeviceTypeNode generateDeviceType(FiLinkDeviceConfig deviceConfig, boolean lockIsRegistry, boolean rfidIsRegistry) {
        final String rfidCode = "9";
        final String lockCode = "3";
        final String rfidRoleCode = "2";
        final String lockRoleCode = "1";
        //设施类型code
        String deviceTypeCode = deviceConfig.getDeviceType();
        List<DetailParam> detailParams = deviceConfig.getDetailParams();
        DeviceTypeNode deviceTypeNode = new DeviceTypeNode();
        deviceTypeNode.setDeviceTypeId(deviceTypeCode);
        //遍历详情模块id,判断该设施类型是否有该业务
        List<BusinessNode> businessNodeList = new ArrayList<>();
        for (DetailParam detailParam : detailParams) {
            String code = detailParam.getId();
            if (lockIsRegistry && lockCode.equals(code)) {
                //电子锁业务
                businessNodeList.add(new BusinessNode(deviceTypeCode, lockRoleCode));
            } else if (rfidIsRegistry && rfidCode.equals(code)) {
                //rfid业务
                businessNodeList.add(new BusinessNode(deviceTypeCode, rfidRoleCode));
            }
        }
        deviceTypeNode.setChildDeviceTypeList(businessNodeList);
        return deviceTypeNode;
    }


    /**
     * 从缓存获取deviceConfig
     *
     * @param language 语言
     * @return 对象实体
     */
    private Map<String, FiLinkDeviceConfig> getDeviceConfigObj(String language) {
        //语言环境为空
        if (StringUtils.isEmpty(language)) {
            Map<String, Map<String, FiLinkDeviceConfig>> deviceConfigMap = (Map) RedisUtils.hGetMap(DeviceConstant.DEVICE_CONFIG_KEY);
            //redis为空,返回空
            if (deviceConfigMap == null || deviceConfigMap.size() == 0) {
                return null;
                //返回map中第一个值
            } else {
                return deviceConfigMap.entrySet().iterator().next().getValue();
            }
            //语言环境不为空
        } else {
            return (Map<String, FiLinkDeviceConfig>) RedisUtils.hGet(DeviceConstant.DEVICE_CONFIG_KEY, language);
        }
    }


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
            Boolean existBus = templateServiceFeign.isExistBus(deviceInfo.getDeviceId());
            if (existBus == null || !existBus) {
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
