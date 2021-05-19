package com.fiberhome.filink.onenetserver.sender;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import com.fiberhome.filink.onenetserver.exception.FilinkOneNetException;
import com.fiberhome.filink.onenetserver.utils.HostUtil;
import com.fiberhome.filink.onenetserver.utils.ProductInfoUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class SendUtilTest {

    /**测试对象 SendUtil*/
    @Tested
    private SendUtil sendUtil;
    /**Mock ProductInfoUtil*/
    @Injectable
    private ProductInfoUtil productInfoUtil;
    /**Mock HostUtil*/
    @Injectable
    private HostUtil hostUtil;
    @Injectable
    private Integer objId;
    @Injectable
    private Integer objInstId;
    @Injectable
    private Integer resId;
    @Injectable
    private Integer mode;
    @Injectable
    private Integer timeout;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(10020);

    @Before
    public void setUp() {
        objId = 5;
        objInstId=2;
        resId=8;
        mode=9;
        timeout=8;
        wireMockRule.resetAll();
        OneNetResponse oneNetResponse = new OneNetResponse();
        oneNetResponse.setErrorno(0);
        oneNetResponse.setError("succ");
        String bodySuccess = JSONObject.toJSONString(oneNetResponse);
        StringBuilder url = new StringBuilder("/nbiot?imei=");
        url.append("52364456214554").append("&obj_id=");
        url.append(5).append("&obj_inst_id=");
        url.append(2).append("&mode=");
        url.append(9).append("&timeout=");
        url.append(8);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(url.toString()))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(bodySuccess)));
    }
    /**
     * sendData
     */
    @Test
    public void sendDataTest() {
        FiLinkOneNetConnectParams oneNetConnectParams = new FiLinkOneNetConnectParams();
        oneNetConnectParams.setImei("52364456214554");
        sendUtil.sendData(oneNetConnectParams, null);
        oneNetConnectParams.setAppId("66415");
        new Expectations() {
            {
                productInfoUtil.findAccessKeyByProductId(anyString);
                result = "skjhakhdkljawilkdjliawj";

                hostUtil.getOneNetHost();
                result = "http://localhost:10020";
            }
        };
        sendUtil.sendData(oneNetConnectParams, "FEFEFEFE");
        new Expectations() {
            {
                hostUtil.getOneNetHost();
                result = new Exception("error");
            }
        };
        try {
            sendUtil.sendData(oneNetConnectParams, "FEFEFEFE");
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkOneNetException.class);
        }
    }


}
