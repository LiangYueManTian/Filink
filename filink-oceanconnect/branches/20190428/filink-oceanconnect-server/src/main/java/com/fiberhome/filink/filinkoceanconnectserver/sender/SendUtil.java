package com.fiberhome.filink.filinkoceanconnectserver.sender;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.JsonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.client.AuthClient;
import com.fiberhome.filink.filinkoceanconnectserver.client.HttpsClient;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanConnectUrl;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.Profile;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令下发方法
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class SendUtil {


    @Autowired
    private HttpsClient httpsClient;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private IpUtil ipUtil;

    /**
     * 发送请求到平台
     *
     * @param data               消息
     * @param oceanConnectParams 请求参数
     */
    public void sendData(FiLinkReqOceanConnectParams oceanConnectParams, String data) {
        String oceanConnectId = oceanConnectParams.getOceanConnectId();
        String appId = oceanConnectParams.getAppId();
        log.info("ocean connect id : " + oceanConnectId);
        if (StringUtils.isEmpty(oceanConnectId)) {
            log.error("send data oceanConnect id is null>>>");
            return;
        }
        //获取token信息
        String accessToken = authClient.getToken(appId);
        //获取发送指令url
        String urlCreateDeviceCommand = ipUtil.getOceanConnectAddress()
                + OceanConnectUrl.CREATE_DEVICE_CMD;
        Integer maxRetransmit = 0;
        ObjectNode paras;
        try {
            Map<String, String> parasMap = new HashMap<>(64);
            byte[] bytes = HexUtil.hexStringToByte(data);
            //将数据进行base64加密
            String base64Str = new String(Base64.encodeBase64(bytes));
            log.info("send message to oceanConnect : " + base64Str);
            parasMap.put(Profile.COMMAND, base64Str);
            paras = JsonUtil.convertObject2ObjectNode(parasMap);
        } catch (Exception e) {
            throw new OceanException("object convert to objectNode failed>>>>>>>>>>>>>");
        }
        //构造CommandDTO参数
        Map<String, Object> paramCommand = new HashMap<>(64);
        paramCommand.put(OceanParamsKey.SERVICE_ID, Profile.SERVICE_ID);
        paramCommand.put(OceanParamsKey.METHOD, Profile.METHOD);
        paramCommand.put(OceanParamsKey.PARAS, paras);
        //构造请求参数
        Map<String, Object> paramCreateDeviceCommand = new HashMap<>(64);
        paramCreateDeviceCommand.put(OceanParamsKey.DEVICE_ID, oceanConnectId);
        paramCreateDeviceCommand.put(OceanParamsKey.COMMAND, paramCommand);
        paramCreateDeviceCommand.put(OceanParamsKey.CALLBACK_URL, "");
        paramCreateDeviceCommand.put(OceanParamsKey.MAX_RETRANSMIT, maxRetransmit);
        paramCreateDeviceCommand.put(OceanParamsKey.EXPIRE_TIME, 0);
        //将参数转成json
        String jsonRequest = JsonUtil.jsonObj2Sting(paramCreateDeviceCommand);
        //构造头部信息
        Map<String, String> header = new HashMap<>(64);
        header.put(OceanParamsKey.HEADER_APP_KEY, appId);
        header.put(OceanParamsKey.HEADER_APP_AUTH, OceanParamsKey.BEARER + accessToken);
        //发送请求
        HttpResponse responseCreateDeviceCommand = httpsClient.doPostJson(urlCreateDeviceCommand, header, jsonRequest);
        //解析响应
        String responseBody = httpsClient.getHttpResponseBody(responseCreateDeviceCommand);
        log.info("CreateDeviceCommand, response content: " + responseBody);
    }
}
