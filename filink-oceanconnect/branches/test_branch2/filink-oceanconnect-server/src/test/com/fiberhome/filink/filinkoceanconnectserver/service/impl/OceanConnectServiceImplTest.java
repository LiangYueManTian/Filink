package com.fiberhome.filink.filinkoceanconnectserver.service.impl;

import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.filinkoceanconnectserver.client.DeviceClient;
import com.fiberhome.filink.filinkoceanconnectserver.entity.https.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.DeviceServiceData;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.ReceiveBean;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.Platform;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.ServiceBean;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.AppInfoUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProfileUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class OceanConnectServiceImplTest {

    @Tested
    private OceanConnectServiceImpl oceanConnectService;

    @Injectable
    private Map<String, AbstractInstructSender> instructSenderMap;

    @Injectable
    private ProtocolUtil protocolUtil;

    @Injectable
    private AppInfoUtil appInfoUtil;

    @Injectable
    private DeviceClient deviceClient;

    @Injectable
    private FiLinkKafkaSender kafkaSender;

    @Injectable
    private ProfileUtil profileUtil;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: sendCmd(FiLinkReqParamsDto fiLinkReqParamsDto)
     */
    @Test
    public void testSendCmd() throws Exception {
        FiLinkReqParamsDto fiLinkReqParamsDto = new FiLinkReqParamsDto();
        oceanConnectService.sendCmd(fiLinkReqParamsDto);
        new Expectations() {
            {
                instructSenderMap.get(anyString);
                result = null;
            }
        };
        try {
            oceanConnectService.sendCmd(fiLinkReqParamsDto);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == RequestException.class);
        }
    }

    /**
     * Method: addProtocol(ProtocolDto protocolDto)
     */
    @Test
    public void testAddProtocol() throws Exception {
        oceanConnectService.addProtocol(new ProtocolDto());
    }

    /**
     * Method: updateProtocol(ProtocolDto protocolDto)
     */
    @Test
    public void testUpdateProtocol() throws Exception {
        oceanConnectService.updateProtocol(new ProtocolDto());
    }

    /**
     * Method: deleteProtocol(List<ProtocolDto> protocolDtoList)
     */
    @Test
    public void testDeleteProtocol() throws Exception {
        List<ProtocolDto> protocolDtoList = new ArrayList<>();
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDtoList.add(protocolDto);
        oceanConnectService.deleteProtocol(protocolDtoList);
    }

    /**
     * Method: registry(OceanDevice oceanDevice)
     */
    @Test
    public void testRegistry() throws Exception {
        OceanDevice oceanDevice = new OceanDevice();
        oceanConnectService.registry(oceanDevice);
        new Expectations() {
            {
                appInfoUtil.getAppInfo(anyString);
                result = null;
            }
        };
        oceanConnectService.registry(oceanDevice);
    }

    /**
     * Method: receiveMsg(ReceiveBean receiveBean)
     */
    @Test
    public void testReceiveMsg() throws Exception {
        ReceiveBean receiveBean = new ReceiveBean();
        DeviceServiceData service = new DeviceServiceData();
        service.setServiceId("testId");
        Map dataMap = new HashMap();
        dataMap.put("testKey", "testValue");
        service.setData(dataMap);
        receiveBean.setService(service);
        Platform platform = new Platform();
        ServiceBean serviceBean = new ServiceBean();
        serviceBean.setData("testKey");
        serviceBean.setBase64(true);
        Map<String, ServiceBean> uploadMap = new HashMap<>();
        uploadMap.put("testId", serviceBean);
        platform.setUploadMap(uploadMap);
        new Expectations() {
            {
                OceanConnectServiceImplTest.this.profileUtil.getProfileConfig();
                result = platform;
            }
        };
        oceanConnectService.receiveMsg(receiveBean);
        dataMap.put("testKey", "");
        try {
            oceanConnectService.receiveMsg(receiveBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == OceanException.class);
        }
    }

    /**
     * Method: deleteDevice(OceanDevice oceanDevice)
     */
    @Test
    public void testDeleteDevice() throws Exception {
        oceanConnectService.deleteDevice(new OceanDevice());
    }

    /**
     * Method: updateHttpsConfig(HttpsConfig httpsConfig)
     */
    @Test
    public void testUpdateHttpsConfig() throws Exception {
        HttpsConfig httpsConfig = new HttpsConfig();
        httpsConfig.setAddress("10.5.24.224");
        oceanConnectService.updateHttpsConfig(httpsConfig);
    }

} 
