package com.fiberhome.filink.systemcommons.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.systemcommons.bean.PlatformAppInfo;
import com.fiberhome.filink.systemcommons.dao.PlatformAppInfoDao;
import com.fiberhome.filink.systemcommons.dto.PlatformAppInfoDto;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  设备平台APP/产品信息服务实现类 测试类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
@RunWith(JMockit.class)
public class PlatformAppInfoServiceImplTest {
    /**
     * 测试对象
     */
    @Tested
    private PlatformAppInfoServiceImpl appInfoService;
    /**
     * 自动注入DAO
     */
    @Injectable
    private PlatformAppInfoDao appInfoDao;

    private PlatformAppInfo appInfo;

    private PlatformAppInfoDto appInfoDto;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        appInfo = new PlatformAppInfo();
        appInfoDto = new PlatformAppInfoDto();
        appInfo.setAppId("454545");
        appInfo.setAppName(appInfo.getAppId());
        appInfo.setDeviceType(appInfo.getAppName());
        appInfo.setManufacturerId(appInfo.getDeviceType());
        appInfo.setManufacturerName(appInfo.getManufacturerId());
        appInfo.setModel(appInfo.getManufacturerName());
        appInfo.setPlatformAppId(appInfo.getModel());
        appInfo.setProtocolType(appInfo.getPlatformAppId());
        appInfo.setSecretKey(appInfo.getProtocolType());
        appInfo.setPlatformType(1);
        appInfoDto.setAppId(appInfo.getSecretKey());
        appInfoDto.setAppName(appInfoDto.getAppId());
        appInfo.setAppId(appInfoDto.getAppName());
        appInfoDto.setPlatformType(appInfo.getPlatformType());
        appInfo.setPlatformType(appInfoDto.getPlatformType());
    }

    /**
     * findPlatformAppInfoByAppId
     */
    @Test
    public void findPlatformAppInfoByAppIdTest() {
        String appId = "5463444";
        appInfoService.findPlatformAppInfoByAppId(appId);
    }

    /**
     * findPlatformAppInfoMapByType
     */
    @Test
    public void findPlatformAppInfoMapByTypeTest() {
        List<PlatformAppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(appInfo);
        Integer platformType = 1;
        new Expectations() {
            {
                appInfoDao.findPlatformAppInfoMapByType(platformType);
                result = appInfoList;
            }
        };
        Map<String, PlatformAppInfo> result = appInfoService.findPlatformAppInfoMapByType(platformType);
        Assert.assertTrue(result.size() > 0);
    }
    /**
     * findPlatformAppInfoAll
     */
    @Test
    public void findPlatformAppInfoAllTest() {
        List<PlatformAppInfoDto> appInfoDtos = new ArrayList<>();
        new Expectations() {
            {
                appInfoDao.findPlatformAppInfoAll();
                result = appInfoDtos;
            }
        };
        Result result = appInfoService.findPlatformAppInfoAll();
        List<PlatformAppInfoDto> resultList = (List)result.getData();
        Assert.assertEquals(0, resultList.size());
        appInfoDtos.add(appInfoDto);
        result = appInfoService.findPlatformAppInfoAll();
        resultList = (List)result.getData();
        Assert.assertTrue(resultList.size() > 0);
    }
}
