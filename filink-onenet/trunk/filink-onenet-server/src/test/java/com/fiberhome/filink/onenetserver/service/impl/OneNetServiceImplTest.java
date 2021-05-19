package com.fiberhome.filink.onenetserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.onenetserver.bean.device.*;
import com.fiberhome.filink.onenetserver.constant.OneNetI18n;
import com.fiberhome.filink.onenetserver.constant.OneNetResultCode;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.onenetserver.utils.HostUtil;
import com.fiberhome.filink.onenetserver.utils.ProductInfoUtil;
import com.fiberhome.filink.onenetserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JMockit.class)
public class OneNetServiceImplTest {
    /**测试对象 OneNetServiceImpl*/
    @Tested
    private OneNetServiceImpl oneNetService;
    /**Mock AbstractInstructSender*/
    @Injectable
    private AbstractInstructSender sender;
    /**Mock ProtocolUtil*/
    @Injectable
    private ProtocolUtil protocolUtil;
    /**Mock ProductInfoUtil*/
    @Injectable
    private ProductInfoUtil productInfoUtil;
    /**Mock HostUtil*/
    @Injectable
    private HostUtil hostUtil;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private CreateDevice createDeviceSuccess;
    private CreateDevice createDeviceFAil;
    private DeleteDevice deleteDeviceSuccess;
    private DeleteDevice deleteDeviceFail;

    @Before
    public void setUp() {
        createDeviceSuccess = new CreateDevice();
        createDeviceFAil = new CreateDevice();
        deleteDeviceSuccess = new DeleteDevice();
        deleteDeviceFail = new DeleteDevice();
        createDeviceSuccess.setTitle("光交001_主控1");
        createDeviceFAil.setTitle(createDeviceSuccess.getTitle());
        createDeviceSuccess.setImsi("152544444468475");
        createDeviceFAil.setImsi(createDeviceSuccess.getImsi());
        createDeviceSuccess.setImei("542415632678542");
        createDeviceFAil.setImei(createDeviceSuccess.getImei() + "1");
        createDeviceSuccess.setProductId("20463");
        createDeviceFAil.setProductId(createDeviceSuccess.getProductId() + "0");
        wireMockRule.resetAll();
        OneNetResponse oneNetResponse = new OneNetResponse();
        oneNetResponse.setErrorno(0);
        oneNetResponse.setError("fail");
        String bodyFail = JSONObject.toJSONString(oneNetResponse);
        oneNetResponse.setError("succ");
        CreateResult createResult = new CreateResult();
        createResult.setDeviceId("412415455");
        createResult.setPsk("skdjkjasd");
        oneNetResponse.setData(createResult);
        String bodySuccess = JSONObject.toJSONString(oneNetResponse);
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/devices")).withRequestBody(WireMock.equalToJson(createDeviceFAil.toJsonObject().toString()))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(bodyFail)));
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/devices")).withRequestBody(WireMock.equalToJson(createDeviceSuccess.toJsonObject().toString()))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(bodySuccess)));
        String deviceId = "4544545";
        deleteDeviceSuccess.setDeviceId(deviceId);
        deleteDeviceFail.setDeviceId(deleteDeviceSuccess.getDeviceId() + "0");
        deleteDeviceSuccess.setProductId("20145");
        deleteDeviceFail.setProductId(deleteDeviceSuccess.getProductId());
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo("/devices/" + deviceId + "0"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(bodyFail)));
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo("/devices/" + deviceId))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(bodySuccess)));
    }
    /**
     * updateOneNetHost
     */
    @Test
    public void updateOneNetHostTest() {
        HostBean hostBean = new HostBean();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(RedisKey.ADDRESS_KEY, any);
            }
        };
        boolean result = oneNetService.updateOneNetHost(hostBean);
        Assert.assertTrue(result);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(RedisKey.ADDRESS_KEY, any);
                result = new Exception();
            }
        };
        result = oneNetService.updateOneNetHost(hostBean);
        Assert.assertFalse(result);
    }

    /**
     * createDevice
     */
    @Test
    public void createDeviceTest() throws IOException {
        new Expectations(I18nUtils.class) {
            {
                productInfoUtil.findAccessKeyByProductId(anyString);
                result = "hj+iPcYrhZHeuDNp85/rhhkwG5OFHVamV2JS7mb7mzk=";

                hostUtil.getOneNetHost();
                result = "http://localhost:8089";

                I18nUtils.getSystemString(OneNetI18n.CREATE_SUCCESS);
                result = "成功";

                I18nUtils.getSystemString(OneNetI18n.CREATE_FAILED);
                result = "失败";
            }
        };
        Result result = oneNetService.createDevice(createDeviceFAil);
        Assert.assertEquals(result.getCode(), OneNetResultCode.CREATE_FAILED);
        result = oneNetService.createDevice(createDeviceSuccess);
        Assert.assertEquals(result.getCode(), (int) OneNetResultCode.SUCCESS);
    }

    /**
     * deleteDevice
     */
    @Test
    public void deleteDeviceTest() {
        new Expectations(I18nUtils.class) {
            {
                hostUtil.getOneNetHost();
                result = "http://localhost:8089";

                productInfoUtil.findAccessKeyByProductId(anyString);
                result = "hj+iPcYrhZHeuDNp85/rhhkwG5OFHVamV2JS7mb7mzk=";

                I18nUtils.getSystemString(OneNetI18n.DELETE_SUCCESS);
                result = "成功";

                I18nUtils.getSystemString(OneNetI18n.DELETE_FAILED);
                result = "失败";
            }
        };
        Result result = oneNetService.deleteDevice(deleteDeviceFail);
        Assert.assertEquals(result.getCode(), OneNetResultCode.DELETE_FAILED);
        result = oneNetService.deleteDevice(deleteDeviceSuccess);
        Assert.assertEquals(result.getCode(), (int) OneNetResultCode.SUCCESS);
    }

    /**
     * write
     */
    @Test
    public void  writeTest() {
        FiLinkReqParamsDto fiLinkReqParamsDto = new FiLinkReqParamsDto();
        fiLinkReqParamsDto.setCmdId("2201");
        Map<String, Object> params = new HashMap<>(16);
        fiLinkReqParamsDto.setParams(params);
        fiLinkReqParamsDto.setEquipmentId("22124154");
        fiLinkReqParamsDto.setSoftwareVersion("RP9003.001A.bin");
        fiLinkReqParamsDto.setHardwareVersion("RP9003.001A.bin");
        fiLinkReqParamsDto.setAppId("41545");
        fiLinkReqParamsDto.setToken("ajkhwdukhwukghakdihiklawhd");
        fiLinkReqParamsDto.setImei("5455445454545");
        fiLinkReqParamsDto.setAppKey(6464656L);
        fiLinkReqParamsDto.setPhoneId("54544545454");
        oneNetService.write(fiLinkReqParamsDto);
    }

    /**
     * addProtocol
     */
    @Test
    public void addProtocolTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setFileHexData("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        boolean result = oneNetService.addProtocol(protocolDto);
        Assert.assertTrue(result);
    }

    /**
     * updateProtocol
     */
    @Test
    public void updateProtocolTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setFileHexData("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        boolean result = oneNetService.updateProtocol(protocolDto);
        Assert.assertTrue(result);
    }

    /**
     * deleteProtocol
     */
    @Test
    public void deleteProtocolTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        List<ProtocolDto> protocolDtoList = new ArrayList<>();
        protocolDtoList.add(protocolDto);
        boolean result = oneNetService.deleteProtocol(protocolDtoList);
        Assert.assertTrue(result);
    }
}
