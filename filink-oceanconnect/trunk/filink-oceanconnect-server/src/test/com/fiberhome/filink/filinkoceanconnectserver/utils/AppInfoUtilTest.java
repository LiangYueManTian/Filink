package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.platformappapi.api.PlatformAppFeign;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * appInfoUtil测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class AppInfoUtilTest {

    @Tested
    private AppInfoUtil appInfoUtil;

    @Injectable
    private PlatformAppFeign appFeign;

    @Test
    public void getSecretByAppId() {
        String appId = "HEpXTGPZ4WaUY7sSvSXpqhH_oW4a";
        PlatformAppInfo appInfo = new PlatformAppInfo();
        new Expectations(appInfoUtil) {
            {
                appInfoUtil.getAppInfo(appId);
                result = appInfo;
            }
        };
        appInfoUtil.getSecretByAppId(appId);
    }

    @Test
    public void getAppInfo() {
        //appInfoObj为空,appInfoMap为空
        String appId = "HEpXTGPZ4WaUY7sSvSXpqhH_oW4a";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.SECRET, appId);
                result = null;
            }
        };
        new Expectations() {
            {
                appFeign.findPlatformAppInfoMapByType(0);
                result = null;
            }
        };
        try {
            appInfoUtil.getAppInfo(appId);
        } catch (Exception e) {
        }

        //appInfoObj为空,appInfoMap不为空
        PlatformAppInfo appInfo = new PlatformAppInfo();
        appInfo.setAppId(appId);
        appInfo.setDeviceType("DoorLock505");
        appInfo.setManufacturerId("fiberhome");
        appInfo.setManufacturerName("fiberhome505");
        appInfo.setModel("DoorLock");
        appInfo.setProtocolType("Coap");
        Map<String, PlatformAppInfo> platformAppInfoMap = new HashMap<>(64);
        platformAppInfoMap.put(appId, appInfo);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.SECRET, appId);
                result = null;
            }
            {
                RedisUtils.hSetMap(RedisKey.SECRET, (Map<String, Object>) any);
            }
        };
        new Expectations() {
            {
                appFeign.findPlatformAppInfoMapByType(0);
                result = platformAppInfoMap;
            }
        };
        appInfoUtil.getAppInfo(appId);


        //appInfoObj为空,appInfoMap不为空,appInfo为空
        platformAppInfoMap.put("11", appInfo);
        platformAppInfoMap.remove(appId);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.SECRET, appId);
                result = null;
            }
            {
                RedisUtils.hSetMap(RedisKey.SECRET, (Map<String, Object>) any);
            }
        };
        new Expectations() {
            {
                appFeign.findPlatformAppInfoMapByType(0);
                result = platformAppInfoMap;
            }
        };
        try {
            appInfoUtil.getAppInfo(appId);
        } catch (Exception e) {
        }
    }
}