package com.fiberhome.filink.filinkoceanconnectserver.client;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.utils.JsonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.utils.AppInfoUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamClosedHttpResponse;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.http.*;
import org.apache.http.params.HttpParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * AuthClient测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class AuthClientTest {

    @Tested
    private AuthClient authClient;

    @Injectable
    private HttpsClient httpsClient;

    @Injectable
    private AppInfoUtil appInfoUtil;

    @Injectable
    private IpUtil ipUtil;

    @Test
    public void getToken() throws Exception {
        String appId = "HEpXTGPZ4WaUY7sSvSXpqhH_oW4a";
        String secret = "e8ddc826f1b4e245678c45e0abe6d4d";
        StreamClosedHttpResponse response = getResponse();
        Map<String, String> dataMap = new HashMap<>(64);
        dataMap.put(OceanParamsKey.ACCESS_TOKEN, "token");
        String key = RedisKey.APP_TOKEN_PREFIX + Constant.SEPARATOR + appId;
        //redis token为空
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(anyString);
                result = null;
            }
        };
        new Expectations() {
            {
                appInfoUtil.getSecretByAppId(appId);
                result = secret;
            }

            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.2.132:5683";
            }

            {
                httpsClient.doPostFormUrlEncodedGetStatusLine(anyString, (Map<String, String>) any);
                result = response;
            }
        };
        new Expectations(JsonUtil.class) {
            {
                JsonUtil.jsonString2SimpleObj(response.getContent(), dataMap.getClass());
                result = dataMap;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(key, "token", 1800);
            }

            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.2.132:5683";
            }
        };
        authClient.getToken(appId);
    }


    @Test
    public void refreshToken() throws Exception{
        String appId = "HEpXTGPZ4WaUY7sSvSXpqhH_oW4a";
        String secret = "e8ddc826f1b4e245678c45e0abe6d4d";
        StreamClosedHttpResponse response = getResponse();
        String jsonRequest = "";
        new Expectations(JsonUtil.class){
            {
                JsonUtil.jsonObj2Sting(any);
                result = jsonRequest;
            }
        };
        new Expectations() {
            {
                appInfoUtil.getSecretByAppId(appId);
                result = secret;
            }

            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.2.132:5683";
            }

            {
                httpsClient.doPostJsonGetStatusLine(anyString, jsonRequest);
                result = response;
            }
        };
        new MockUp<StreamClosedHttpResponse>(){
            @Mock
            public StatusLine getStatusLine() {
                return new StatusLine() {
                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return null;
                    }

                    @Override
                    public int getStatusCode() {
                        return 0;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return null;
                    }
                };
            }
        };
        authClient.refreshToken(appId,"rereshToken");
    }


    /**
     * 获取response
     *
     * @return StreamClosedHttpResponse
     */
    private StreamClosedHttpResponse getResponse() throws Exception{
        StreamClosedHttpResponse response = new StreamClosedHttpResponse(new HttpResponse() {
            @Override
            public StatusLine getStatusLine() {
                return null;
            }

            @Override
            public void setStatusLine(StatusLine statusLine) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {

            }

            @Override
            public void setStatusCode(int i) throws IllegalStateException {

            }

            @Override
            public void setReasonPhrase(String s) throws IllegalStateException {

            }

            @Override
            public HttpEntity getEntity() {
                return null;
            }

            @Override
            public void setEntity(HttpEntity httpEntity) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public Header[] getHeaders(String s) {
                return new Header[0];
            }

            @Override
            public Header getFirstHeader(String s) {
                return null;
            }

            @Override
            public Header getLastHeader(String s) {
                return null;
            }

            @Override
            public Header[] getAllHeaders() {
                return new Header[0];
            }

            @Override
            public void addHeader(Header header) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setHeader(Header header) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void setHeaders(Header[] headers) {

            }

            @Override
            public void removeHeader(Header header) {

            }

            @Override
            public void removeHeaders(String s) {

            }

            @Override
            public HeaderIterator headerIterator() {
                return null;
            }

            @Override
            public HeaderIterator headerIterator(String s) {
                return null;
            }

            @Override
            public HttpParams getParams() {
                return null;
            }

            @Override
            public void setParams(HttpParams httpParams) {

            }
        });
        return response;
    }
}