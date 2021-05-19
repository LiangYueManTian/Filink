package com.fiberhome.filink.fdevice.async;

import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.service.area.impl.AreaInfoServiceImpl;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/7/9
 */
@RunWith(JMockit.class)
public class AreaAsyncTest {
    /**
     * 注入区域实现类
     */
    @Injectable
    private AreaInfoServiceImpl areaInfoService;
    /**
     * 自动注入设施service
     */
    @Injectable
    private DeviceInfoService deviceInfoService;
    @Tested
    private AreaAsync async;

    @Test
    public void afterUpdateAreaSuccess() {
        AreaInfo areaInfo = new AreaInfo();
        async.afterUpdateAreaSuccess(areaInfo);
    }

    @Test
    public void afterAddAreaSuccess() {
        AreaInfo areaInfo = new AreaInfo();
        Set<String> accountabilityUnit = new HashSet<>();
        accountabilityUnit.add("test");
        areaInfo.setAccountabilityUnit(accountabilityUnit);
        async.afterAddAreaSuccess(areaInfo);
    }

    @Test
    public void afterDeleteAreaSuccess() {
        async.afterDeleteAreaSuccess(new ArrayList<>());
    }
}
