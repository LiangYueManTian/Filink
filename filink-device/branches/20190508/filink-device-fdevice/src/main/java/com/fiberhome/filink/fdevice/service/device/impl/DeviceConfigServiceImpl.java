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
 * ?????????????????????
 *
 * @author CongcaiYu
 */
@Slf4j
@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    @Autowired
    private ControlFeign controlFeign;
    /**
     * ????????????????????????????????????id
     */
    private List<String> detailParamListWithControl;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private TemplateServiceFeign templateServiceFeign;

    /**
     * ????????????id??????????????????code
     *
     * @param deviceInfo ??????id
     * @return ??????code
     */
    @Override
    public Result getDetailCode(DeviceInfo deviceInfo) {
        //????????????
        String deviceType = deviceInfo.getDeviceType();
        //??????????????????
        String language = RequestInfoUtils.getLanguage();
        //??????????????????
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType, language);
        List<DetailParam> detailParams = deviceConfig.getDetailParams();
        //????????????
        List<DetailParam> result = new ArrayList<>(detailParams);
        // ??????????????????
        if (deviceType.equals(DeviceType.Optical_Box.getCode())
                || deviceType.equals(DeviceType.Well.getCode())
                || deviceType.equals(DeviceType.Outdoor_Cabinet.getCode())) {
            // ?????????????????????????????????
            List<String> deviceTypes = getDeviceTypes(getCurrentUser());
            String code = DeviceLockAuthEnum.getCodeByParent(deviceType);
            if (!deviceTypes.contains(code)) {
                //??????????????????
                result = detailParams.stream().filter(
                        (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                        .collect(Collectors.toList());
            } else {
                //??????????????????????????????????????????
                result = queryControlInfo(deviceInfo, detailParams, result);
            }
        }
        // ????????????  ????????????  ????????? ????????????????????????

        if (deviceType.equals(DeviceType.Optical_Box.getCode()) ||
                deviceType.equals(DeviceType.Distribution_Frame.getCode()) ||
                deviceType.equals(DeviceType.Junction_Box.getCode())) {
            // ??????????????????????????????????????????
            List<String> deviceTypes = getDeviceTypes(getCurrentUser());
            String code = DeviceAuthEnum.getCodeByParent(deviceType);
            if (!deviceTypes.contains(code)) {
                //????????????????????? ????????????id ??? 8  ????????? deviceConfig.xml
                result = result.stream().filter(obj -> !obj.getId().equals("8")).collect(Collectors.toList());
            }
            if (!templateServiceFeign.isExistBus(deviceInfo.getDeviceId())) {
                //??????????????????????????? ???????????????????????? id ??? 9  ????????? deviceConfig.xml
                result = result.stream().filter(obj -> !obj.getId().equals("9")).collect(Collectors.toList());
            }
        }

        return ResultUtils.success(result);
    }

    /**
     * ??????????????????????????????
     *
     * @param deviceInfo   ????????????
     * @param detailParams ????????????
     * @param result       ????????????
     * @return ??????????????????
     */
    private List<DetailParam> queryControlInfo(DeviceInfo deviceInfo, List<DetailParam> detailParams, List<DetailParam> result) {
        //?????????????????????
        List<ControlParam> controlParams = controlFeign.getControlParams(deviceInfo.getDeviceId());
        //????????????????????????????????????????????????
        if (!ObjectUtils.isEmpty(controlParams)) {
            //???????????????
            String hostType = ConstantParam.PASSIVE_HOST;
            ControlParam controlParam = controlParams.get(0);
            if (controlParam.getHostType().equals(hostType)) {
                result = detailParams.stream().filter(
                        (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                        .collect(Collectors.toList());
                //??????????????????????????????
                DetailParam detailParam = new DetailParam();
                detailParam.setId(ConstantParam.PASSIVE_HOST);
                result.add(detailParam);
            }
        } else {
            //????????????
            result = detailParams.stream().filter(
                    (DetailParam d) -> !detailParamListWithControl.contains(d.getId()))
                    .collect(Collectors.toList());
        }
        return result;
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    private User getCurrentUser() {
        //??????????????????????????????
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        Object userObj = userFeign.queryCurrentUser(userId, token);
        //?????????User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * ??????????????????????????????
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
     * ????????????id???????????????????????????
     *
     * @param deviceType ????????????
     * @return ?????????
     */
    @Override
    public Result getParamsConfig(String deviceType) {
        String language = RequestInfoUtils.getLanguage();
        //????????????????????????
        FiLinkDeviceConfig deviceConfig = getDeviceConfig(deviceType, language);
        List<Configuration> configurations = deviceConfig.getConfigurations();
        return ResultUtils.success(configurations);
    }

    /**
     * ?????????????????????
     *
     * @param deviceType ????????????
     * @return???????????????
     */
    @Override
    public Map<String, String> getDefaultParams(String deviceType) {
        Map<String, String> defaultParams = new HashMap<>(64);
        //????????????????????????
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
     * ????????????????????????redis???
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
     * ????????????????????????????????????
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
     * ??????????????????
     *
     * @param deviceType ????????????
     * @return ??????????????????
     */
    private FiLinkDeviceConfig getDeviceConfig(String deviceType, String language) {
        Object deviceConfigObj = getDeviceConfigObj(deviceType, language);
        //????????????????????????
        if (deviceConfigObj == null) {
            //????????????????????????
            initDeviceConfigToRedis();
            deviceConfigObj = getDeviceConfigObj(deviceType, language);
        }
        //deviceConfig????????????????????????
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
     * ???????????????deviceConfig
     *
     * @param deviceType ????????????
     * @param language   ??????
     * @return ????????????
     */
    private Object getDeviceConfigObj(String deviceType, String language) {
        //??????key???
        String code = DeviceConstant.DEVICE_CONFIG_CODE;
        String key = code + deviceType;
        //??????????????????
        if (StringUtils.isEmpty(language)) {
            Map<String, Map<String, FiLinkDeviceConfig>> deviceConfigMap = (Map) RedisUtils.hGetMap(DeviceConstant.DEVICE_CONFIG_KEY);
            //redis??????,?????????
            if (deviceConfigMap == null || deviceConfigMap.size() == 0) {
                return null;
                //??????map???????????????
            } else {
                return deviceConfigMap.entrySet().iterator().next().getValue().get(key);
            }
            //?????????????????????
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
