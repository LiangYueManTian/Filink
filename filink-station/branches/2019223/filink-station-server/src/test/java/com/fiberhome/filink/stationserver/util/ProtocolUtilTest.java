package com.fiberhome.filink.stationserver.util;

import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.stationserver.exception.ProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProtocolUtilTest {

    private DeviceInfoDto deviceInfoDto;

    private Control control;

    /**
     * 测试对象
     */
    @InjectMocks
    private ProtocolUtil protocolUtil;

    /**
     * 模拟deviceFeign
     */
    @Mock
    private DeviceFeign deviceFeign;

    /**
     * 模拟controlFeign
     */
    @Mock
    private ControlFeign controlFeign;

    @Before
    public void setUp() {
        control = new Control();
        control.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        control.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        control.setSyncStatus("1");
        control.setHardwareVersion("stm32L4-v001");
        control.setSoftwareVersion("RP9003.002G.bin");
        deviceInfoDto = new DeviceInfoDto();
    }

    /**
     * 根据序列id获取协议实体
     */
    @Test
    public void getProtocolBeanBySerialNum() {
        try {
            when(deviceFeign.findDeviceBySeriaNumber(anyString())).thenReturn(deviceInfoDto);
        } catch (Exception e) {

        }
        when(controlFeign.getControlParams(anyString())).thenReturn(control);
        protocolUtil.getProtocolBeanBySerialNum("13172750");
        //异常情况
        try {
            when(deviceFeign.findDeviceBySeriaNumber(anyString())).thenThrow(new ProtocolException(""));
            protocolUtil.getProtocolBeanBySerialNum("13172750");
        } catch (Exception e) {

        }

        //device为null场景
        try {
            when(deviceFeign.findDeviceBySeriaNumber(anyString())).thenReturn(null);
            protocolUtil.getProtocolBeanBySerialNum("13172750");
        } catch (Exception e) {

        }

        //control为null场景
        try {
            when(deviceFeign.findDeviceBySeriaNumber(anyString())).thenReturn(null);
            when(controlFeign.getControlParams(anyString())).thenReturn(null);
            protocolUtil.getProtocolBeanBySerialNum("13172750");
        } catch (Exception e) {

        }
    }
}