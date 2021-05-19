//package com.fiberhome.filink.lockserver.controller;
//
//import com.fiberhome.filink.lockserver.bean.ControlParam;
//import com.fiberhome.filink.lockserver.bean.ControlReq;
//import com.fiberhome.filink.lockserver.bean.SetConfigBean;
//import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
//import com.fiberhome.filink.lockserver.service.ControlService;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * MasterControlController测试类
// *
// * @author CongcaiYu
// */
//
//@RunWith(MockitoJUnitRunner.class)
//public class ControlControllerTest {
//
//    private ControlParam controlParam;
//
//    @InjectMocks
//    private ControlController controlController;
//
//    /**
//     * mock LockService
//     */
//    @Mock
//    private ControlService controlService;
//
//
//    @Before
//    public void setUp() {
//        controlParam = new ControlParam();
//        controlParam.setHostId("hostId1");
//        controlParam.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
//        controlParam.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
//        controlParam.setSyncStatus("1");
//        controlParam.setHardwareVersion("stm32L4-v001");
//        controlParam.setSoftwareVersion("RP9003.002G.bin");
//        controlParam.setDeviceStatus("2");
//        controlParam.setDeployStatus("1");
//        controlParam.setSourceType("1");
//    }
//
//    @Test
//    public void queryControlByControlId() {
//        ControlReq controlReq = new ControlReq();
//        try {
//            controlController.queryControlByControlId(controlReq);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlReq.setControlId("cote");
//        controlController.queryControlByControlId(controlReq);
//    }
//
//
//    @Test
//    public void getControlParams() {
//        try {
//            controlController.getControlParams("");
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.getControlParams("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//    @Test
//    public void getDeviceIdByControlId() {
//        try {
//            controlController.getDeviceIdByControlId("");
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.getDeviceIdByControlId("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//
//    @Test
//    public void getControlFeign() {
//        try {
//            controlController.getControlFeign("");
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.getControlFeign("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//    @Test
//    public void getControlFeignForPda() {
//        try {
//            controlController.getControlFeignForPda("");
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.getControlFeignForPda("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//    @Test
//    public void getControlParamById() {
//        try {
//            controlController.getControlParamById("");
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.getControlParamById("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//  /*  @Test
//    public void updateDeployStatusById() {
//        try {
//            controlController.updateDeployStatusById(null);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.updateDeployStatusById(controlParam);
//    }*/
//
// /*   @Test
//    public void updateDeviceStatusById() {
//        try {
//            controlController.updateDeviceStatusById(null);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        controlController.updateDeviceStatusById(controlParam);
//    }*/
///*
//    @Test
//    public void updateDeployStatusByDeviceId() {
//        SetDeployStatusBean setDeployStatusBean = new SetDeployStatusBean();
//        try {
//            controlController.updateDeployStatusByDeviceId(setDeployStatusBean);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
//        }
//        List<String> deviceIds = new ArrayList<>();
//        deviceIds.add("das");
//        setDeployStatusBean.setDeviceIds(deviceIds);
//        setDeployStatusBean.setDeployStatus("5");
//        controlController.updateDeployStatusByDeviceId(setDeployStatusBean);
//    }*/
//
//    @Test
//    public void setConfig() {
//        //config为空
//        try {
//            controlController.setConfig(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //ConfigParams为空
//        try {
//            controlController.setConfig(new SetConfigBean());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //构造设置配置策略参数
//        SetConfigBean setConfigBean = new SetConfigBean();
//        List<String> deviceIds = new ArrayList<>();
//        deviceIds.add("3dffc3413a3f11e9b3520242ac110003");
//        setConfigBean.setConfigParams(null);
//        setConfigBean.setDeviceIds(deviceIds);
//        //configParamMap为空
//        try {
//            controlController.setConfig(setConfigBean);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Map<String, String> configParamMap = new HashMap<>();
//        configParamMap.put("electricity", "25");
//        configParamMap.put("temperature", "43");
//        setConfigBean.setConfigParams(configParamMap);
//        controlController.setConfig(setConfigBean);
//    }
//}
