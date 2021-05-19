package com.fiberhome.filink.filinkoceanconnectserver.controller;

import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.filinkoceanconnectserver.entity.https.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.DeviceServiceData;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.ReceiveBean;
import com.fiberhome.filink.filinkoceanconnectserver.service.OceanConnectService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class OceanConnectControllerTest {
    @Injectable
    private OceanConnectService oceanConnectService;
    @Tested
    private OceanConnectController oceanConnectController;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: registryDevice(@RequestBody OceanDevice oceanDevice)
     */
    @Test
    public void testRegistryDevice() throws Exception {
        OceanDevice oceanDevice = new OceanDevice();
        oceanConnectController.registryDevice(oceanDevice);
        oceanDevice.setAppId("testId");
        oceanConnectController.registryDevice(oceanDevice);
    }

    /**
     * Method: deleteDevice(@RequestBody OceanDevice oceanDevice)
     */
    @Test
    public void testDeleteDevice() throws Exception {
        OceanDevice oceanDevice = new OceanDevice();
        oceanConnectController.deleteDevice(oceanDevice);
        oceanDevice.setDeviceId("testId");
        oceanDevice.setAppId("testId");
        oceanConnectController.deleteDevice(oceanDevice);
    }

    /**
     * Method: receiveOceanMsg(@RequestBody ReceiveBean receiveBean)
     */
    @Test
    public void testReceiveOceanMsg() throws Exception {
        ReceiveBean receiveBean = new ReceiveBean();
        oceanConnectController.receiveOceanMsg(receiveBean);
        receiveBean.setDeviceId("testId");
        receiveBean.setNotifyType("testType");
        receiveBean.setGatewayId("testId");
        DeviceServiceData service = new DeviceServiceData();
        receiveBean.setService(service);
        oceanConnectController.receiveOceanMsg(receiveBean);
        service.setServiceId("testId");
        service.setServiceType("testType");
        service.setData("testDate");
        service.setEventTime("testTime");
        oceanConnectController.receiveOceanMsg(receiveBean);
    }

    /**
     * Method: addProtocol(@RequestBody ProtocolDto protocolDto)
     */
    @Test
    public void testAddProtocol() throws Exception {
        ProtocolDto protocolDto = new ProtocolDto();
        oceanConnectController.addProtocol(protocolDto);
        protocolDto.setFileHexData("FEX");
        oceanConnectController.addProtocol(protocolDto);
    }

    /**
     * Method: updateProtocol(@RequestBody ProtocolDto protocolDto)
     */
    @Test
    public void testUpdateProtocol() throws Exception {
        ProtocolDto protocolDto = new ProtocolDto();
        oceanConnectController.updateProtocol(protocolDto);
        protocolDto.setFileHexData("FEX");
        protocolDto.setHardwareVersion("test");
        protocolDto.setSoftwareVersion("test");
        oceanConnectController.updateProtocol(protocolDto);
    }

    /**
     * Method: deleteProtocol(@RequestBody List<ProtocolDto> protocolDtoList)
     */
    @Test
    public void testDeleteProtocol() throws Exception {
        List<ProtocolDto> protocolDtoList = new ArrayList<>();
        oceanConnectController.deleteProtocol(protocolDtoList);
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDtoList.add(protocolDto);
        oceanConnectController.deleteProtocol(protocolDtoList);
        protocolDto.setFileHexData("FEX");
        protocolDto.setHardwareVersion("test");
        protocolDto.setSoftwareVersion("test");
        oceanConnectController.deleteProtocol(protocolDtoList);
    }

    /**
     * Method: updateHttpsConfig(@RequestBody HttpsConfig httpsConfig)
     */
    @Test
    public void testUpdateHttpsConfig() throws Exception {
        HttpsConfig httpsConfig = new HttpsConfig();
        oceanConnectController.updateHttpsConfig(httpsConfig);
        httpsConfig.setAddress("192.168.0.1");
        oceanConnectController.updateHttpsConfig(httpsConfig);
    }


} 
