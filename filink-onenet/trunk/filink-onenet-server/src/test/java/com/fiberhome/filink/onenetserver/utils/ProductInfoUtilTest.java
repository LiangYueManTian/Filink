package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.platformappapi.api.PlatformAppFeign;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class ProductInfoUtilTest {

    /**测试对象 ProductInfoUtil*/
    @Tested
    private ProductInfoUtil productInfoUtil;
    /**Mock PlatformAppFeign*/
    @Injectable
    private PlatformAppFeign appFeign;


    /**
     * findAccessKeyByProductId
     */
    @Test
    public void findAccessKeyByProductIdTest() {
        String productId = "41556445";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(RedisKey.SECRET);
                result = true;
                RedisUtils.hGet(RedisKey.SECRET, productId);
                result = null;
            }
        };
        String result = productInfoUtil.findAccessKeyByProductId(productId);
        Assert.assertNull(result);
        Map<String, PlatformAppInfo> appInfoMap = new HashMap<>(64);
        PlatformAppInfo platformAppInfo = new PlatformAppInfo();
        platformAppInfo.setAppId("41556445");
        platformAppInfo.setSecretKey("akjhwdkihaiklhwdi");
        appInfoMap.put(platformAppInfo.getAppId(), platformAppInfo);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSetMap(RedisKey.SECRET, (Map) any);
                RedisUtils.hasKey(RedisKey.SECRET);
                result = false;
                appFeign.findPlatformAppInfoMapByType(1);
                result = appInfoMap;
            }
        };
        result = productInfoUtil.findAccessKeyByProductId(productId);
        Assert.assertEquals("akjhwdkihaiklhwdi", result);
    }

}
