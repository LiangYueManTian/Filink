package com.fiberhome.filink.filinkoceanconnectserver.client;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.utils.JsonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.constant.NotifyType;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanConnectUrl;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.Platform;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.ServiceBean;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProfileUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamClosedHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * oceanConnect设施操作类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class DeviceClient {

    @Autowired
    private HttpsClient httpsClient;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private IpUtil ipUtil;

    @Value("${constant.callBackUrl}")
    private String callBackUrl;

    @Autowired
    private ProfileUtil profileUtil;

    /**
     * 注册设施
     *
     * @param oceanDevice OceanDevice
     */
    public StreamClosedHttpResponse registryDevice(OceanDevice oceanDevice) {
        log.info("[addControl] : registry control to ocean connect : " + JSONObject.toJSONString(oceanDevice));
        try {
            String appId = oceanDevice.getAppId();
            //获取token
            String accessToken = authClient.getToken(appId);
            //注册地址
            String registerDeviceUrl = ipUtil.getOceanConnectAddress() +
                    OceanConnectUrl.REGISTER_DIRECT_CONNECTED_DEVICE;

            String jsonRequest = JsonUtil.jsonObj2Sting(oceanDevice);
            Map<String, String> header = new HashMap<>(64);
            header.put(OceanParamsKey.HEADER_APP_KEY, oceanDevice.getAppId());
            header.put(OceanParamsKey.HEADER_APP_AUTH, OceanParamsKey.BEARER + accessToken);
            //发送注册请求
            StreamClosedHttpResponse response = httpsClient.doPostJsonGetStatusLine(registerDeviceUrl, header, jsonRequest);
            log.info("[addControl] : control registry response {} ", response.getContent());
            return response;
        } catch (Exception e) {
            log.error("[addControl] : registry control failed {}", e);
        }
        return null;
    }


    /**
     * 根据id删除设施
     *
     * @param oceanDevice 设施实体
     */
    public StreamClosedHttpResponse deleteDeviceById(OceanDevice oceanDevice) {
        //平台id
        String deviceId = oceanDevice.getDeviceId();
        String appId = oceanDevice.getAppId();
        try {
            //获取token
            String accessToken = authClient.getToken(appId);
            String urlDeleteDirectConnectedDevice = ipUtil.getOceanConnectAddress() +
                    OceanConnectUrl.DELETE_DIRECT_CONNECTED_DEVICE + "/" + deviceId;
            Map<String, String> header = new HashMap<>(64);
            header.put(OceanParamsKey.HEADER_APP_KEY, appId);
            header.put(OceanParamsKey.HEADER_APP_AUTH, OceanParamsKey.BEARER + accessToken);
            //发送请求
            StreamClosedHttpResponse response = httpsClient.doDeleteWithParasGetStatusLine(urlDeleteDirectConnectedDevice,
                    null, header);
            log.info("[deleteControl] : delete control response", response.getContent());
            return response;
        } catch (Exception e) {
            log.error("[deleteControl] : delete device failed", e);
        }
        return null;
    }


    /**
     * 根据id修改设施
     *
     * @param oceanDevice OceanDevice
     */
    public StreamClosedHttpResponse modifyDeviceById(OceanDevice oceanDevice) {
        String appId = oceanDevice.getAppId();
        String deviceId = oceanDevice.getDeviceId();
        try {
            String accessToken = authClient.getToken(appId);
            String urlModifyDevice = ipUtil.getOceanConnectAddress()
                    + OceanConnectUrl.MODIFY_DEVICE_INFO + deviceId;

            String jsonRequest = JsonUtil.jsonObj2Sting(oceanDevice);

            Map<String, String> header = new HashMap<>(64);
            header.put(OceanParamsKey.HEADER_APP_KEY, appId);
            header.put(OceanParamsKey.HEADER_APP_AUTH, OceanParamsKey.BEARER + accessToken);

            StreamClosedHttpResponse response = httpsClient.doPutJsonGetStatusLine(urlModifyDevice,
                    header, jsonRequest);

            log.info("[updateControl] : update control response", response.getContent());
            return response;
        } catch (Exception e) {
            log.error("[updateControl] : update control failed", e);
        }
        return null;
    }

    /**
     * 订阅所有产品
     */
    public void subscribeAll() {
        Platform profileConfig = profileUtil.getProfileConfig();
        Map<String, ServiceBean> downMap = profileConfig.getDownMap();
        for (Map.Entry<String, ServiceBean> entry : downMap.entrySet()) {
            String appId = entry.getKey();
            subscribe(appId);
        }
    }

    /**
     * 订阅oceanConnect平台消息
     */
    public void subscribe(String appId) {
        //订阅请求地址
        String urlSubscribe = ipUtil.getOceanConnectAddress() + OceanConnectUrl.SUBSCRIBE_SERVICE_NOTIFYCATION;
        //获取token
        String accessToken = authClient.getToken(appId);
        //获取订阅类型
        List<String> serviceNotifyTypes = NotifyType.getServiceNotifyTypes();
        //循环构造订阅类型
        for (String serviceNotifyType : serviceNotifyTypes) {
            //构造请求参数
            Map<String, Object> paramServiceSubscribe = new HashMap<>(64);
            paramServiceSubscribe.put(OceanParamsKey.NOTIFY_TYPE, serviceNotifyType);
            paramServiceSubscribe.put(OceanParamsKey.CALLBACK_URL, callBackUrl);
            String jsonRequest = JsonUtil.jsonObj2Sting(paramServiceSubscribe);
            Map<String, String> header = new HashMap<>(64);
            header.put(OceanParamsKey.HEADER_APP_KEY, appId);
            header.put(OceanParamsKey.HEADER_APP_AUTH, OceanParamsKey.BEARER + accessToken);
            //获取响应信息
            HttpResponse httpResponse = httpsClient.doPostJson(urlSubscribe, header, jsonRequest);
            String bodySubscribe = httpsClient.getHttpResponseBody(httpResponse);
            log.info("SubscribeServiceNotification, notifyType : {} , callbackUrl : {} , response content:",
                    serviceNotifyType, callBackUrl, bodySubscribe);
        }
    }
}
