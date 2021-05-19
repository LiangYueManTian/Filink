package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.platformappapi.api.PlatformAppFeign;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *   oneNet平台产品信息获取工具类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-06
 */
@Slf4j
@Component
public class ProductInfoUtil {
    /**
     * 电信平台类型
     */
    private static final int ONE_NET_TYPE = 1;

    @Autowired
    private PlatformAppFeign appFeign;
    /**
     * 根据产品ID获取秘钥
     * @param productId 产品ID
     * @return 秘钥
     */
    public String findAccessKeyByProductId(String productId) {
        String accessKey;
        if (RedisUtils.hasKey(RedisKey.SECRET)) {
            accessKey = (String) RedisUtils.hGet(RedisKey.SECRET, productId);
        } else {
            Map<String, String> productSecretMap = findProductInfo();
            accessKey = productSecretMap.get(productId);
        }
        if (StringUtils.isEmpty(accessKey)) {
            return null;
        }
        return accessKey;
    }

    /**
     * 查找所有产品信息
     * @return 所有产品信息
     */
    public Map<String, String> findProductInfo() {
        Map<String, PlatformAppInfo> appInfoMap = appFeign.findPlatformAppInfoMapByType(ONE_NET_TYPE);
        Map<String, Object> productSecretMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(appInfoMap)) {
            for (PlatformAppInfo appInfo : appInfoMap.values()) {
                productSecretMap.put(appInfo.getAppId(), appInfo.getSecretKey());
            }
            RedisUtils.hSetMap(RedisKey.SECRET, productSecretMap);
        }
        return (Map) productSecretMap;

    }
}
