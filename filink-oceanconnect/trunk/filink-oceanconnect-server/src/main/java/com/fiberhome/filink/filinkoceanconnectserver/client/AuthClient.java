package com.fiberhome.filink.filinkoceanconnectserver.client;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.utils.JsonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanConnectUrl;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.filinkoceanconnectserver.utils.AppInfoUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamClosedHttpResponse;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证方法
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class AuthClient {

    @Autowired
    private HttpsClient httpsClient;

    @Autowired
    private AppInfoUtil appInfoUtil;

    @Autowired
    private IpUtil ipUtil;


    /**
     * 过期时间
     */
    private final int expireTime = 1800;


    /**
     * 获取token
     *
     * @param appId 产品id
     * @return token
     */
    public String getToken(String appId) {
        log.info("get token>>>>>>>");
        //从redis中获取token
        String key = RedisKey.APP_TOKEN_PREFIX + Constant.SEPARATOR + appId;
        String token = (String) RedisUtils.get(key);
        if (StringUtils.isEmpty(token)) {
            log.info("redis token is null , get token from ocean connect");
            Map<String, String> tokenMap = getAccessToken(appId);
            //获取token
            token = tokenMap.get(OceanParamsKey.ACCESS_TOKEN);
            //将token存入redis并设置过期时间
            RedisUtils.set(key, token, expireTime);
        }
        return token;
    }


    /**
     * 鉴权
     *
     * @return token
     */
    private Map<String, String> getAccessToken(String appId) {
        try {
            log.info("get access token>>>>>>");
            //根据appId获取密钥
            String secret = appInfoUtil.getSecretByAppId(appId);
            //发起鉴权请求
            String urlLogin = ipUtil.getOceanConnectAddress() + OceanConnectUrl.APP_AUTH;
            Map<String, String> param = new HashMap<>(64);
            param.put(OceanParamsKey.APP_ID, appId);
            param.put(OceanParamsKey.SECRET, secret);
            StreamClosedHttpResponse responseLogin = httpsClient.doPostFormUrlEncodedGetStatusLine(urlLogin, param);
            //获取鉴权返回参数
            Map<String, String> data = new HashMap<>(64);
            data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
            String accessToken = data.get(OceanParamsKey.ACCESS_TOKEN);
            if (StringUtils.isEmpty(accessToken)) {
                throw new OceanException("the token of app " + appId + "is null");
            }
            log.info("accessToken:" + accessToken);
            return data;
        } catch (Exception e) {
            throw new OceanException("ocean platform authentication failed");
        }
    }


    /**
     * 刷新token
     *
     * @param appId        产品id
     * @param refreshToken 刷新token
     */
    public void refreshToken(String appId, String refreshToken) {
        //根据appId获取密钥
        String secret = appInfoUtil.getSecretByAppId(appId);
        //刷新token
        String urlRefreshToken = ipUtil.getOceanConnectAddress() + OceanConnectUrl.REFRESH_TOKEN;
        Map<String, Object> paramReg = new HashMap<>(64);
        paramReg.put(OceanParamsKey.APP_ID, appId);
        paramReg.put(OceanParamsKey.SECRET, secret);
        paramReg.put(OceanParamsKey.REFRESH_TOKEN, refreshToken);

        String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);
        StreamClosedHttpResponse bodyRefreshToken = httpsClient.doPostJsonGetStatusLine(urlRefreshToken, jsonRequest);

        log.info("RefreshToken, response content:");
        log.info(bodyRefreshToken.getStatusLine().toString());
        log.info(bodyRefreshToken.getContent());
    }

}
