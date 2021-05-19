package com.fiberhome.filink.filinkoceanconnectserver.client;

import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.DeviceInfoDto;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.utils.IpUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProfileUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamClosedHttpResponse;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.*;
import org.apache.http.params.HttpParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

/**
 * DeviceClient测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class DeviceClientTest {

    @Tested
    private DeviceClient deviceClient;

    @Injectable
    private HttpsClient httpsClient;

    @Injectable
    private AuthClient authClient;

    @Injectable
    private IpUtil ipUtil;

    @Injectable
    private ProfileUtil profileUtil;

    @Test
    public void registryDevice() throws Exception{
        OceanDevice oceanDevice = new OceanDevice();
        oceanDevice.setNodeId("12345123");
        oceanDevice.setVerifyCode("12345123");
        oceanDevice.setAppId("HEpXTGPZ4WaUY7sSvSXpqhH_oW4a");
        oceanDevice.setProductId("21432213543");
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setManufacturerName("fiberhome");
        deviceInfoDto.setProtocolType("CoAP");
        deviceInfoDto.setModel("DoorLock");
        deviceInfoDto.setDeviceType("DoorLock505");
        deviceInfoDto.setManufacturerId("fiberhome505");
        oceanDevice.setDeviceInfo(deviceInfoDto);
        new Expectations(){
            {
                authClient.getToken(oceanDevice.getAppId());
                result = "3242134125";
            }
            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.24.11:5683";
            }
        };
        deviceClient.registryDevice(oceanDevice);
    }

    @Test
    public void deleteDeviceById() {
        OceanDevice oceanDevice = new OceanDevice();
        oceanDevice.setNodeId("12345123");
        oceanDevice.setVerifyCode("12345123");
        oceanDevice.setAppId("HEpXTGPZ4WaUY7sSvSXpqhH_oW4a");
        oceanDevice.setProductId("21432213543");
        new Expectations(){
            {
                authClient.getToken(oceanDevice.getAppId());
                result = "3242134125";
            }
            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.24.11:5683";
            }
        };
        deviceClient.deleteDeviceById(oceanDevice);
    }

    @Test
    public void modifyDeviceById() {
        OceanDevice oceanDevice = new OceanDevice();
        oceanDevice.setNodeId("12345123");
        oceanDevice.setVerifyCode("12345123");
        oceanDevice.setAppId("HEpXTGPZ4WaUY7sSvSXpqhH_oW4a");
        oceanDevice.setProductId("21432213543");
        new Expectations(){
            {
                authClient.getToken(oceanDevice.getAppId());
                result = "3242134125";
            }
            {
                ipUtil.getOceanConnectAddress();
                result = "https://180.101.24.11:5683";
            }
        };
        deviceClient.modifyDeviceById(oceanDevice);
    }

    @Test
    public void subscribe() {
        String appId = "HEpXTGPZ4WaUY7sSvSXpqhH_oW4a";
        deviceClient.subscribe(appId);
    }

    @Test
    public void subscribeAll() {
        deviceClient.subscribeAll();
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