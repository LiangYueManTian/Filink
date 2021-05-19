package com.fiberhome.filink.fdevice.controller.config;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * DeviceConfigControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceConfigControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private DeviceConfigController deviceConfigController;
    @Mock
    private DeviceConfigService deviceConfigService;

    private Result result = new Result();

    @Test
    public void getDetailCode() {
        DeviceInfo deviceInfo = new DeviceInfo();
        when(deviceConfigService.getDetailCode(deviceInfo)).thenReturn(result);
        Assert.assertEquals(result, deviceConfigController.getDetailCode(deviceInfo));
    }

    @Test
    public void getParamsConfig() {
        String deviceType = new String();
        when(deviceConfigService.getParamsConfig(deviceType)).thenReturn(result);
        Assert.assertEquals(result, deviceConfigController.getParamsConfig(deviceType));
    }

    @Test
    public void getConfigPatterns() {
        Map<String, String> map = new HashMap<>();
        when(deviceConfigService.getConfigPatterns()).thenReturn(map);
        Assert.assertEquals(map, deviceConfigController.getConfigPatterns());
    }
}