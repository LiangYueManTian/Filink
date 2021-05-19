package com.fiberhome.filink.onenetserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.onenetserver.bean.device.CreateDevice;
import com.fiberhome.filink.onenetserver.bean.device.DeleteDevice;
import com.fiberhome.filink.onenetserver.bean.device.HostBean;
import com.fiberhome.filink.onenetserver.constant.OneNetI18n;
import com.fiberhome.filink.onenetserver.constant.OneNetResultCode;
import com.fiberhome.filink.onenetserver.service.OneNetService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class OneNetControllerTest {
    /**测试对象 OneNetController*/
    @Tested
    private OneNetController oneNetController;
    /**Mock OneNetService*/
    @Injectable
    private OneNetService oneNetService;


    /**
     * updateOneNetHost
     */
    @Test
    public void updateOneNetHostTest() {
        HostBean hostBean = new HostBean();
        boolean result = oneNetController.updateOneNetHost(hostBean);
        Assert.assertFalse(result);
        hostBean.setHost("http://ww.api.com");
        new Expectations() {
            {
                oneNetService.updateOneNetHost(hostBean);
                result = true;
            }
        };
        result = oneNetController.updateOneNetHost(hostBean);
        Assert.assertTrue(result);
    }

    /**
     * createDevice
     */
    @Test
    public void createDeviceTest() {
        CreateDevice createDevice = new CreateDevice();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(OneNetI18n.PARAMETER_ERROR);
                result = "参数错误";
            }
        };
        Result result = oneNetController.createDevice(createDevice);
        Assert.assertEquals(result.getCode(), OneNetResultCode.PARAMETER_ERROR);
        createDevice.setTitle("光交001_主控1");
        createDevice.setImei("125414569875145");
        createDevice.setImsi("521454125896345");
        createDevice.setProductId("25365412");
        result = oneNetController.createDevice(createDevice);
        Assert.assertEquals(result.getCode(), (int) OneNetResultCode.SUCCESS);
    }

    /**
     * deleteDevice
     */
    @Test
    public void deleteDeviceTest() {
        DeleteDevice deleteDevice = new DeleteDevice();
        deleteDevice.setDeviceId("654535634");
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(OneNetI18n.PARAMETER_ERROR);
                result = "参数错误";
            }
        };
        Result result = oneNetController.deleteDevice(deleteDevice);
        Assert.assertEquals(result.getCode(), OneNetResultCode.PARAMETER_ERROR);
        deleteDevice.setProductId("25365412");
        result = oneNetController.deleteDevice(deleteDevice);
        Assert.assertEquals(result.getCode(), (int) OneNetResultCode.SUCCESS);
    }

    /**
     * addProtocol
     */
    @Test
    public void addProtocolTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        boolean result = oneNetController.addProtocol(protocolDto);
        Assert.assertFalse(result);
        protocolDto.setFileHexData("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        new Expectations() {
            {
                oneNetService.addProtocol(protocolDto);
                result = true;
            }
        };
        result = oneNetController.addProtocol(protocolDto);
        Assert.assertTrue(result);
    }

    /**
     * updateProtocol
     */
    @Test
    public void updateProtocolTest() {
        ProtocolDto protocolDto = new ProtocolDto();
        boolean result = oneNetController.updateProtocol(protocolDto);
        Assert.assertFalse(result);
        protocolDto.setFileHexData("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        protocolDto.setHardwareVersion("RP8004.004B.bin");
        protocolDto.setSoftwareVersion("RP8004.004B.bin");
        new Expectations() {
            {
                oneNetService.updateProtocol(protocolDto);
                result = true;
            }
        };
        result = oneNetController.updateProtocol(protocolDto);
        Assert.assertTrue(result);
    }

    /**
     * deleteProtocol
     */
    @Test
    public void deleteProtocolTest() {
        List<ProtocolDto> protocolDtoList = new ArrayList<>();
        boolean result = oneNetController.deleteProtocol(protocolDtoList);
        Assert.assertFalse(result);
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDtoList.add(protocolDto);
        result = oneNetController.deleteProtocol(protocolDtoList);
        Assert.assertFalse(result);
        protocolDto.setHardwareVersion("RP8004.004B.bin");
        protocolDto.setSoftwareVersion("RP8004.004B.bin");
        protocolDtoList.clear();
        protocolDtoList.add(protocolDto);
        new Expectations() {
            {
                oneNetService.deleteProtocol(protocolDtoList);
                result = true;
            }
        };
        result = oneNetController.deleteProtocol(protocolDtoList);
        Assert.assertTrue(result);
    }
}
