package com.fiberhome.filink.fdevice.controller.config;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.clientcommon.utils.ResultUtils;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.utils.DeviceType;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class DeviceConfigControllerTest {

    /**
     * 测试对象
     */
    @Tested
    private DeviceConfigController deviceConfigController;

    /**
     * 模拟deviceConfigService
     */
    @Injectable
    private DeviceConfigService deviceConfigService;

    /**
     * 根据设施id获取详情模块code测试方法
     */
    @Test
    public void getDetailCode() {
        String deviceType = DeviceType.Optical_Box.getCode();
        new Expectations(){
            {
                deviceConfigService.getDetailCode(deviceType);
            }
        };
        deviceConfigController.getDetailCode(deviceType);
    }

    /**
     * 根据设施id获取参数下发配置项测试方法
     */
    @Test
    public void getParamsConfig() {
        String deviceType = DeviceType.Optical_Box.getCode();
        new Expectations(){
            {
                deviceConfigService.getParamsConfig(deviceType);
            }
        };
        deviceConfigController.getParamsConfig(deviceType);
    }
}
