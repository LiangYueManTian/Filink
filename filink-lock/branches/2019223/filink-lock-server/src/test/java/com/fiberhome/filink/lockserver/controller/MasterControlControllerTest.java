package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.service.MasterControlService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * MasterControlController测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class MasterControlControllerTest {

    private Control control;

    @InjectMocks
    private MasterControlController masterControlController;

    @Mock
    private MasterControlService controlService;

    @Before
    public void setUp() {
        control = new Control();
        control.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        control.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        control.setSyncStatus("1");
        control.setHardwareVersion("stm32L4-v001");
        control.setSoftwareVersion("RP9003.002G.bin");
    }

    @Test
    public void saveControlParams() {
        masterControlController.saveControlParams(control);
    }

    @Test
    public void updateControlParamsByDeviceId() {
        control.setControlId("3dffc3413a3f11e9b3520242ac110003");
        masterControlController.updateControlParamsByDeviceId(control);
    }

    @Test
    public void getControlParams() {
        masterControlController.getControlParams("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void getControlFeign() {
        masterControlController.getControlFeign("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void setConfig() {
        //config为空
        try {
            masterControlController.setConfig(null);
        }catch (Exception e){
            e.printStackTrace();
        }
        //ConfigParams为空
        try {
            masterControlController.setConfig(new SetConfigBean());
        }catch (Exception e){
            e.printStackTrace();
        }
        //构造设置配置策略参数
        SetConfigBean setConfigBean = new SetConfigBean();
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("3dffc3413a3f11e9b3520242ac110003");
        Map<String,String> configParamMap = new HashMap<>();
        configParamMap.put("electricity","25");
        configParamMap.put("temperature","43");
        setConfigBean.setConfigParams(configParamMap);
        setConfigBean.setDeviceIds(deviceIds);
        masterControlController.setConfig(setConfigBean);
    }
}