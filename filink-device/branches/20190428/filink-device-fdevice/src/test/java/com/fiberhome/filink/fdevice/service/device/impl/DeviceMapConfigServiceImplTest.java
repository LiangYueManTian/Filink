package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.fiberhome.filink.fdevice.dao.device.DeviceMapConfigDao;
import com.fiberhome.filink.fdevice.dto.DeviceMapConfigDto;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.constant.device.DeviceType;
import com.fiberhome.filink.server_common.exception.UserNotLoginException;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 *     首页设施地图配置服务实现类测试类
 * </P>
 * @author chaofang@wistronits.com
 * @since 2019/2/2
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceMapConfigServiceImplTest {
    /**
     * 测试对象 DeviceMapConfigServiceImpl
     */
    @Tested
    private DeviceMapConfigServiceImpl deviceMapConfigService;

    /**
     * 自动注入地图设施类型配置dao对象
     */
    @Injectable
    private DeviceMapConfigDao deviceMapConfigDao;
    /**
     * Mock HttpServletRequest
     */
    @Mocked
    private HttpServletRequest request;
    /**
     * Mock RequestContextHolder
     */
    @Mocked
    private RequestContextHolder requestContextHolder;
    /**
     * Mock ServletRequestAttributes
     */
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * queryDeviceMapConfig
     */
    @Test
     public void queryDeviceMapConfig() {
         try {
             deviceMapConfigService.queryDeviceMapConfig();
         } catch (Exception e) {
             Assert.assertTrue(e.getClass() == UserNotLoginException.class);
         }
        new Expectations() {
            {
                request.getHeader(anyString);
                result = "11";

                deviceMapConfigDao.queryDeviceMapConfig(anyString);
                result = null;
            }
        };
        try {
            deviceMapConfigService.queryDeviceMapConfig();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapMessageException.class);
        }
        new Expectations() {
            {
                deviceMapConfigDao.queryDeviceMapConfig(anyString);
                result = new DeviceMapConfigDto();
            }
        };
        Result result = deviceMapConfigService.queryDeviceMapConfig();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
     }

    /**
     * bathUpdateDeviceConfig
     */
    @Test
    public void bathUpdateDeviceConfig() {
        List<DeviceMapConfig> deviceMapConfigs = new ArrayList<>();
        try {
            deviceMapConfigService.bathUpdateDeviceConfig(deviceMapConfigs);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapParamException.class);
        }
        DeviceMapConfig deviceMapConfig = new DeviceMapConfig();
        deviceMapConfig.setConfigValue("ssss");
        deviceMapConfigs.add(deviceMapConfig);
        try {
            deviceMapConfigService.bathUpdateDeviceConfig(deviceMapConfigs);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapParamException.class);
        }
        for (DeviceMapConfig deviceMapConfig1 : deviceMapConfigs) {
            deviceMapConfig1.setConfigValue("1");
        }
        new Expectations() {
            {
                deviceMapConfigDao.bathUpdateDeviceConfig((List<DeviceMapConfig>) any, anyString);
                result = 0;
            }
        };
        try {
            deviceMapConfigService.bathUpdateDeviceConfig(deviceMapConfigs);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapUpdateException.class);
        }
        new Expectations() {
            {
                deviceMapConfigDao.bathUpdateDeviceConfig((List<DeviceMapConfig>) any, anyString);
                result = deviceMapConfigs.size();
            }
        };
        Result result = deviceMapConfigService.bathUpdateDeviceConfig(deviceMapConfigs);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * updateDeviceIconSize
     */
    @Test
    public void updateDeviceIconSize() {
        DeviceMapConfig deviceMapConfig = null;
        try {
            deviceMapConfigService.updateDeviceIconSize(deviceMapConfig);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapParamException.class);
        }
        deviceMapConfig = new DeviceMapConfig();
        deviceMapConfig.setConfigValue("11");
        new Expectations() {
            {
                deviceMapConfigDao.updateDeviceIconSize((DeviceMapConfig) any);
                result = 0;
            }
        };
        try {
            deviceMapConfigService.updateDeviceIconSize(deviceMapConfig);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapUpdateException.class);
        }
        new Expectations() {
            {
                deviceMapConfigDao.updateDeviceIconSize((DeviceMapConfig) any);
                result = 1;
            }
        };
        Result result = deviceMapConfigService.updateDeviceIconSize(deviceMapConfig);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * insertConfigBatch
     */
    @Test
    public void insertConfigBatch() {
        String userId = "";
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "111";
            }
        };
        try {
            deviceMapConfigService.insertConfigBatch(userId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceException.class);
        }
        userId = "1";
        new Expectations() {
            {
                deviceMapConfigDao.insertConfigBatch((List<DeviceMapConfig>) any);
                result = 0;
            }
        };
        try {
            deviceMapConfigService.insertConfigBatch(userId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceMapException.class);
        }
        new Expectations() {
            {
                deviceMapConfigDao.insertConfigBatch((List<DeviceMapConfig>) any);
                result = DeviceType.values().length + 1;
            }
        };
        boolean result = deviceMapConfigService.insertConfigBatch(userId);
        Assert.assertTrue(result == true);
    }

    /**
     * deletedConfigByUserIds
     */
    @Test
    public void deletedConfigAll() {
        List<String> userIds = new ArrayList<>();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "111";
            }
        };
        try {
            deviceMapConfigService.deletedConfigByUserIds(userIds);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDeviceException.class);
        }
        userIds.add("1");
        boolean result = deviceMapConfigService.deletedConfigByUserIds(userIds);
        Assert.assertTrue(result == true);
    }
}
