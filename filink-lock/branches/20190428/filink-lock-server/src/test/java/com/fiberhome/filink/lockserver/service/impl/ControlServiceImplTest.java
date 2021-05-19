//
//package com.fiberhome.filink.lockserver.service.impl;
//
//import com.fiberhome.filink.deviceapi.api.DeviceFeign;
//import com.fiberhome.filink.lockserver.bean.ControlParam;
//import com.fiberhome.filink.lockserver.dao.ControlDao;
//import com.fiberhome.filink.logapi.log.LogCastProcess;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
//import mockit.Injectable;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// * MasterControlServiceImpl测试类
// *
// * @author CongcaiYu
// */
//
//@RunWith(JMockit.class)
//public class ControlServiceImplTest {
//
//    /**
//     * 主控对象
//     */
//
//    private ControlParam controlParam;
//
//
//    /**
//     * 模拟MasterControlServiceImpl
//     */
//
//    @Tested
//    private ControlServiceImpl controlService;
//
//
//    /**
//     * 模拟MasterControlDao
//     */
//
//    @Injectable
//    private ControlDao controlDao;
//
//
//    /**
//     * 模拟FiLinkStationFeign
//     */
//
//    @Injectable
//    private FiLinkStationFeign stationFeign;
//
//
//    /**
//     * 模拟DeviceFeign
//     */
//
//    @Injectable
//    private DeviceFeign deviceFeign;
//
//
//    /**
//     * 模拟LogProcess
//     */
//
//    @Injectable
//    private LogProcess logProcess;
//
//    /**
//     * 模拟LogCastProcess
//     */
//
//    @Injectable
//    private LogCastProcess logCastProcess;
//
//
//    /**
//     * 初始化主控对象
//     */
//
//    @Before
//    public void setUp() {
//        controlParam = new ControlParam();
//        controlParam.setDeviceId("3dffc3413a3f4356r3520242ac110003");
//        controlParam.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
//        controlParam.setSyncStatus("1");
//        controlParam.setHardwareVersion("stm32L4-v001");
//        controlParam.setSoftwareVersion("RP9003.002G.bin");
//    }
//
//
//    /**
//     * 根据设施id查询主控测试方法
//     */
//
//    @Test
//    public void getControlInfoByDeviceId() {
//        controlService.getControlInfoByDeviceId("2df750a8377f11e9b3520242ac110003");
//    }
//
//
//}
