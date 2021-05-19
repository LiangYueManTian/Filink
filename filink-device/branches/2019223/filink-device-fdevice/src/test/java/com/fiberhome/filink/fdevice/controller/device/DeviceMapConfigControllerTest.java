package com.fiberhome.filink.fdevice.controller.device;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.fiberhome.filink.fdevice.service.device.DeviceMapConfigService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 *     首页设施地图配置控制器测试类
 * </P>
 * @author chaofang@wistronits.com
 * @since 2019/2/2
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceMapConfigControllerTest {

    /**
     * 测试对象
     */
    @Tested
    private DeviceMapConfigController deviceMapConfigController;
    /**
     * 自动注入service对象
     */
    @Injectable
    private DeviceMapConfigService deviceMapConfigService;
    /**
     * 地图设施类型配置信息list
     */
    private List<DeviceMapConfig> deviceMapConfigs;
    /**
     * 地图设施类型配置信息
     */
    private DeviceMapConfig deviceMapConfig;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户Id List
     */
    private List<String> userIds;
    /**
     * 初始化
     */
    @Before
    public void setUp() {
        deviceMapConfig = new DeviceMapConfig();
        deviceMapConfigs = new ArrayList<>();
    }
    /**
     *queryMapDeviceConfigAll
     */
    @Test
    public void queryMapDeviceConfigAll() {
        Result result = deviceMapConfigController.queryMapDeviceConfigAll();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     *updateDeviceTypeStatusAll
     */
    @Test
    public void updateDeviceTypeStatusAll(){
        Result result = deviceMapConfigController.updateDeviceTypeStatusAll(deviceMapConfigs);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     *updateDeviceIconSize
     */
    @Test
    public void updateDeviceIconSize(){
        Result result = deviceMapConfigController.updateDeviceIconSize(deviceMapConfig);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * insertConfigBatch
     */
    @Test
    public void insertConfig(){
        new Expectations() {
            {
                deviceMapConfigService.insertConfigBatch(userId);
                result = true;
            }
        };
        boolean result = deviceMapConfigController.insertConfigBatch(userId);
        Assert.assertTrue(result == true);
    }

    /**
     * deletedConfigByUserIds
     */
    @Test
    public void deletedConfig(){
        new Expectations() {
            {
                deviceMapConfigService.deletedConfigByUserIds(userIds);
                result = true;
            }
        };
        boolean result = deviceMapConfigController.deletedConfigByUserIds(userIds);
        Assert.assertTrue(result == true);
    }
}
