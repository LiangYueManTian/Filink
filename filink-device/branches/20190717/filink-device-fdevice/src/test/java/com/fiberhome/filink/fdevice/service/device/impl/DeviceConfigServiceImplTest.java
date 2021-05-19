package com.fiberhome.filink.fdevice.service.device.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.fdevice.bean.config.ConfigParam;
import com.fiberhome.filink.fdevice.bean.config.Configuration;
import com.fiberhome.filink.fdevice.bean.config.DetailParam;
import com.fiberhome.filink.fdevice.bean.config.FiLinkDeviceConfig;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceConfigException;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * DeviceConfigServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/6
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceConfigServiceImplTest {
    @InjectMocks
    private DeviceConfigServiceImpl deviceConfigService;
    @Mock
    private ControlFeign controlFeign;

    private FiLinkDeviceConfig fiLinkDeviceConfig = new FiLinkDeviceConfig();
    @Mock
    private UserFeign userFeign;
    @Mock
    private List<String> detailParamListWithControl;

    @Before
    public void setUp() {
        List<Configuration> configurations = new ArrayList<>();
        Configuration configuration = new Configuration();
        configuration.setId("zz");
        configuration.setName("cc");
        List<ConfigParam> configParams = new ArrayList<>();
        ConfigParam configParam = new ConfigParam();
        configParam.setId("Aa");
        configParams.add(configParam);
        configurations.add(configuration);
        configuration.setConfigParams(configParams);
        fiLinkDeviceConfig.setConfigurations(configurations);
        DetailParam detailParam = new DetailParam();
        detailParam.setId("CNM");
        List<DetailParam> detailParams = new ArrayList<>();
        detailParams.add(detailParam);
        fiLinkDeviceConfig.setDetailParams(detailParams);
        detailParamListWithControl = new ArrayList<>();
        detailParamListWithControl.add("3");
        detailParamListWithControl.add("4");
        detailParamListWithControl.add("5");
    }

    @Test
    public void getDetailCode() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceType("210");
        new Expectations(RedisUtils.class) {{
            RedisUtils.hGet(anyString, anyString);
            result = fiLinkDeviceConfig;
        }};

        List<ControlParam> controlParams = new ArrayList<>();
        ControlParam controlParam = new ControlParam();
        controlParam.setControlId("c");
        controlParam.setHostType("0");
        controlParams.add(controlParam);
        when(controlFeign.getControlParams(any())).thenReturn(controlParams);
        User user = new User();
        user.setId(DeviceConstant.ADMIN);
        when(userFeign.queryCurrentUser(any(), any())).thenReturn(JSONObject.toJSON(user));
        Assert.assertEquals(0, deviceConfigService.getDetailCode(deviceInfo).getCode());

        deviceInfo.setDeviceId("vv");
        deviceInfo.setDeviceType("090");
        when(controlFeign.getControlParams(deviceInfo.getDeviceId())).thenReturn(null);
        Assert.assertEquals(0, deviceConfigService.getDetailCode(deviceInfo).getCode());
    }

    @Test
    public void getParamsConfig() {

        new Expectations(RedisUtils.class) {{
            RedisUtils.hGet(anyString, anyString);
            result = fiLinkDeviceConfig;
        }};
        Assert.assertEquals(0, deviceConfigService.getParamsConfig("XX").getCode());
    }

    @Test
    public void getDefaultParams() {
        FiLinkDeviceConfig fiLinkDeviceConfig = new FiLinkDeviceConfig();
        List<Configuration> configurations = new ArrayList<>();
        Configuration configuration = new Configuration();
        configuration.setId("zz");
        configuration.setName("cc");
        List<ConfigParam> configParams = new ArrayList<>();
        ConfigParam configParam = new ConfigParam();
        configParam.setId("Aa");
        configParams.add(configParam);
        configurations.add(configuration);
        configuration.setConfigParams(configParams);
        fiLinkDeviceConfig.setConfigurations(configurations);
        new Expectations(RedisUtils.class) {{
            RedisUtils.hGet(anyString, anyString);
            result = fiLinkDeviceConfig;
        }};
        String deviceType = "xx";
        deviceConfigService.getDefaultParams(deviceType);
    }

    @Test
    public void initDeviceConfigToRedis() {
        try {
            deviceConfigService.initDeviceConfigToRedis();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceConfigException);
        }

    }

    @Test
    public void getConfigPatterns() {
        try {
            deviceConfigService.getConfigPatterns();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceConfigException);
        }
    }

    @Test
    public void init() {
        deviceConfigService.init();
    }
}