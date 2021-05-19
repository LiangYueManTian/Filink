package com.fiberhome.filink.systemcommons.controller;

import com.fiberhome.filink.systemcommons.bean.PlatformAppInfo;
import com.fiberhome.filink.systemcommons.service.PlatformAppInfoService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * <p>
 *  设备平台APP/产品信息前端控制器 测试类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
@RunWith(JMockit.class)
public class PlatformAppInfoControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private PlatformAppInfoController appInfoController;
    /**
     * 自动注入Service
     */
    @Injectable
    private PlatformAppInfoService appInfoService;


    /**
     * findPlatformAppInfoByAppId
     */
    @Test
    public void findPlatformAppInfoByAppIdTest() {
        PlatformAppInfo appInfo = appInfoController.findPlatformAppInfoByAppId(null);
        Assert.assertNull(appInfo);
        String appId = "215555";
        appInfo = appInfoController.findPlatformAppInfoByAppId(appId);
        Assert.assertNotNull(appInfo);
    }

    /**
     * findPlatformAppInfoMapByType
     */
    @Test
    public void findPlatformAppInfoMapByTypeTest(){
        Map<String, PlatformAppInfo> appInfoMap = appInfoController.findPlatformAppInfoMapByType(null);
        Assert.assertNull(appInfoMap);
        Integer platformType = 1;
        appInfoMap = appInfoController.findPlatformAppInfoMapByType(platformType);
        Assert.assertNotNull(appInfoMap);
    }

    /**
     * findPlatformAppInfoAll
     */
    @Test
    public void findPlatformAppInfoAllTest(){
        appInfoController.findPlatformAppInfoAll();
    }
}
