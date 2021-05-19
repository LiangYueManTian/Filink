package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.platformappapi.api.PlatformAppFeign;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台信息工具类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class AppInfoUtil {

    @Autowired
    private PlatformAppFeign appFeign;

    /**
     * 电信平台类型
     */
    private final int oceanConnectType = 0;

    /**
     * 根据应用id获取密钥
     *
     * @param appId 应用id
     * @return 密钥
     */
    public String getSecretByAppId(String appId) {
        return getAppInfo(appId).getSecretKey();
    }

    /**
     * 根据appId获取密钥
     *
     * @param appId 产品id
     * @return 密钥
     */
    public PlatformAppInfo getAppInfo(String appId) {
        log.info("get app info>>>>>>>>>>");
        //从缓存获取密钥
        Object appInfoObj;
        try {
            appInfoObj = RedisUtils.hGet(RedisKey.SECRET, appId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("get app info from redis failed>>>>>");
            return querySecret(appId);
        }
        //如果找不到密钥则重新查询
        if (StringUtils.isEmpty(appInfoObj)) {
            appInfoObj = querySecret(appId);
        }
        //如果密钥仍为空，则该appId不存在
        if (StringUtils.isEmpty(appInfoObj)) {
            String msg = "the appId : " + appId + " info is not exist>>>";
            log.error(msg);
            throw new OceanException(msg);
        }
        return (PlatformAppInfo) appInfoObj;
    }

    /**
     * 查询密钥
     *
     * @param appId 应用id
     * @return 应用信息
     */
    private PlatformAppInfo querySecret(String appId) {
        Map<String, PlatformAppInfo> appInfoMap = appFeign.findPlatformAppInfoMapByType(oceanConnectType);
        if (appInfoMap == null) {
            String msg = "ocean connect app info map is null";
            log.error(msg);
            throw new OceanException(msg);
        }
        PlatformAppInfo appInfo = appInfoMap.get(appId);
        Map<String, Object> appInfoRedisMap = new HashMap<>(64);
        appInfoRedisMap.putAll(appInfoMap);
        RedisUtils.hSetMap(RedisKey.SECRET, appInfoRedisMap);
        return appInfo;
    }
}
