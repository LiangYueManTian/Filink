package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.RemoveAlarm;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.bean.SetDeployStatusBean;
import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlIsNullException;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ControlController测试类
 *
 * @author wanzhaozhang@wistronits.com
 */

@RunWith(JMockit.class)
public class ControlControllerTest {

    private ControlParam controlParam;

    @Tested
    private ControlController controlController;

    /**
     * mock LockService
     */
    @Injectable
    private ControlService controlService;

    @Mocked
    private I18nUtils i18nUtils;

    @Mocked
    private ControlParam emptyControlParam;
    @Mocked
    private List<RemoveAlarm> removeAlarmList;

    @Before
    public void setUp() {
        controlParam = new ControlParam();
        controlParam.setHostId("hostId1");
        controlParam.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        controlParam.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        controlParam.setSyncStatus("1");
        controlParam.setHardwareVersion("stm32L4-v001");
        controlParam.setSoftwareVersion("RP9003.002G.bin");
        controlParam.setDeviceStatus("2");
        controlParam.setDeployStatus("1");
        controlParam.setSourceType("1");
    }

    @Test
    public void queryControlByControlId() {
        ControlReq controlReq = new ControlReq();
        try {
            controlController.queryControlByControlId(controlReq);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlReq.setControlId("cote");
        controlController.queryControlByControlId(controlReq);
    }


    @Test
    public void getControlParams() {
        try {
            controlController.getControlParams("");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.getControlParams("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void getDeviceIdByControlId() {
        try {
            controlController.getDeviceIdByControlId("");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.getDeviceIdByControlId("3dffc3413a3f11e9b3520242ac110003");
    }


    @Test
    public void getControlFeign() {
        try {
            controlController.getControlFeign("");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.getControlFeign("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void getControlFeignForPda() {
        try {
            controlController.getControlFeignForPda("");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.getControlFeignForPda("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void getControlParamById() {
        try {
            controlController.getControlParamById("");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }

        controlController.getControlParamById("3dffc3413a3f11e9b3520242ac110003");

        new Expectations() {
            {
                controlService.getControlParamById(anyString);
                result = new FiLinkControlIsNullException();
            }
        };
        controlController.getControlParamById("3dffc3413a3f11e9b3520242ac110003");


    }

    @Test
    public void updateDeployStatusById() {
        try {
            controlController.updateDeployStatusById(emptyControlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.updateDeployStatusById(controlParam);
    }

    @Test
    public void updateDeviceStatusById() {
        try {
            controlController.updateDeviceStatusById(emptyControlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.updateDeviceStatusById(controlParam);
    }

    @Test
    public void updateControlStatusById(){
        controlController.updateControlStatusById(controlParam);
    }

    @Test
    public void updateDeployStatusByDeviceId() {
        SetDeployStatusBean setDeployStatusBean = new SetDeployStatusBean();
        try {
            controlController.updateDeployStatusByDeviceId(setDeployStatusBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("das");
        setDeployStatusBean.setDeviceIds(deviceIds);
        setDeployStatusBean.setDeployStatus("5");
        controlController.updateDeployStatusByDeviceId(setDeployStatusBean);
    }

    @Test
    public void setConfig() {

        //ConfigParams为空
        try {
            controlController.setConfig(new SetConfigBean());
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        //构造设置配置策略参数
        SetConfigBean setConfigBean = new SetConfigBean();
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("3dffc3413a3f11e9b3520242ac110003");
        setConfigBean.setConfigParams(null);
        setConfigBean.setDeviceIds(deviceIds);
        //configParamMap为空
        try {
            controlController.setConfig(setConfigBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }

        Map<String, String> configParamMap = new HashMap<>();
        configParamMap.put("electricity", "25");
        configParamMap.put("temperature", "43");
        setConfigBean.setConfigParams(configParamMap);
        controlController.setConfig(setConfigBean);
    }

    @Test
    public void deleteControlByDeviceIds() {
        List<String> deviceIdList = new ArrayList<>();
        try {
            controlController.deleteControlByDeviceIds(deviceIdList);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        deviceIdList.add("ss");
        controlController.deleteControlByDeviceIds(deviceIdList);
    }

    @Test
    public void deleteLockAndControlByControlId() {
        ControlReq controlReq = new ControlReq();
        try {
            controlController.deleteLockAndControlByControlId(controlReq);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlReq.setControlId("controlId");
        controlReq.setDeviceId("deviceId");
        controlController.deleteLockAndControlByControlId(controlReq);
    }

    @Test
    public void updateControlParam() {
        try {
            controlController.updateControlParam(emptyControlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        controlController.updateControlParam(controlParam);
    }

    @Test
    public void removeAlarm(){
        try {
            controlController.removeAlarm(null);
        }catch (Exception e){
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }

        controlController.removeAlarm(removeAlarmList);
    }
}
