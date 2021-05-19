//
//package com.fiberhome.filink.lockserver.controller;
//
//import com.fiberhome.filink.bean.PageCondition;
//import com.fiberhome.filink.lockserver.bean.*;
//import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
//import com.fiberhome.filink.lockserver.service.LockService;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * LockController测试类
// *
// * @author CongcaiYu
// */
//
//@RunWith(MockitoJUnitRunner.class)
//public class LockControllerTest {
//
//    private Lock lock;
//
//    /**
//     * 测试对象
//     */
//    @InjectMocks
//    private LockController lockController;
//
//    /**
//     * mock LockService
//     */
//    @Mock
//    private LockService lockService;
//
//    @Before
//    public void setUp() {
//        lock = new Lock();
//        lock.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
//        lock.setDoorName("door-001");
//        lock.setDoorStatus("1");
//        lock.setLockNum("1");
//        lock.setLockStatus("1");
//    }
//
//
//    @Test
//    public void batchUpdateLockStatus() {
//        List<Lock> locks = new ArrayList<>();
//        locks.add(lock);
//        lockController.batchUpdateLockStatus(locks);
//    }
//
//    @Test
//    public void queryLockByControlId() {
//        String controlId = null;
//        try {
//            lockController.queryLockByControlId(controlId);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        controlId = "hostId1";
//        lockController.queryLockByControlId(controlId);
//    }
//
//
//    @Test
//    public void openLock() {
//        //参数为空场景
//        OpenLockBean openLockBeanNull = new OpenLockBean();
//        try {
//            lockController.openLock(openLockBeanNull);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//
//        //构造开锁参数
//        OpenLockBean openLockBean = new OpenLockBean();
//        openLockBean.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
//        List<String> doorNumList = new ArrayList<>();
//        doorNumList.add("1");
//        doorNumList.add("2");
//        openLockBean.setDoorNumList(doorNumList);
//        lockController.openLock(openLockBean);
//    }
//
//    @Test
//    public void openLockForPda() {
//        //参数为空场景
//        OpenLockBean openLockBeanNull = new OpenLockBean();
//        try {
//            lockController.openLockForPda(openLockBeanNull);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //构造开锁参数
//        OpenLockBean openLockBean = new OpenLockBean();
//        openLockBean.setDeviceId("dasdave3ws");
//        openLockBean.setDoorNum("1");
//        lockController.openLockForPda(openLockBean);
//    }
//
//    @Test
//    public void queryLockAndControlByQrcode() {
//        //参数为空
//        QrcodeBean qrcodeBean = new QrcodeBean();
//        try {
//            lockController.queryLockAndControlByQrcode(qrcodeBean);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //正常
//        qrcodeBean.setQrcode("qrcode11");
//        lockController.queryLockAndControlByQrcode(qrcodeBean);
//    }
//
//    @Test
//    public void openLockByQrcode() {
//        //参数为空
//        QrcodeBean qrcodeBean = new QrcodeBean();
//        try {
//            lockController.openLockByQrcode(qrcodeBean);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //正常
//        qrcodeBean.setQrcode("qrcode11");
//        lockController.openLockByQrcode(qrcodeBean);
//    }
//
//
//
//    @Test
//    public void queryLockStatusByDeviceId() {
//        //参数为空
//        OpenLockBean lockBean = new OpenLockBean();
//        try {
//            lockController.queryLockStatusByDeviceId(lockBean);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        lockBean.setDeviceId("1asd");
//        lockController.queryLockStatusByDeviceId(lockBean);
//    }
//
//    @Test
//    public void queryLockAndControlByDeviceId(){
//        ControlReq controlReq = new ControlReq();
////        参数异常
//        try {
//            lockController.queryLockAndControlByDeviceId(controlReq);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        controlReq.setDeviceId("s");
//        lockController.queryLockAndControlByDeviceId(controlReq);
//    }
//
//    @Test
//    public void deleteLockAndControlByDeviceId() {
//        //参数为空
//        OpenLockBean lockBean = new OpenLockBean();
//        try {
//            lockController.deleteLockAndControlByDeviceId(lockBean);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //正常
//        lockBean.setDeviceId("1asd");
//        lockController.deleteLockAndControlByDeviceId(lockBean);
//    }
//
//    @Test
//    public void lockListByPage() {
//        PageCondition pageCondition =new PageCondition();
//        //异常
//        try {
//            lockController.lockListByPage(pageCondition);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //正常
//        pageCondition.setPageNum(3);
//        pageCondition.setPageSize(2);
//
//        lockController.lockListByPage(pageCondition);
//    }
//
//    @Test
//   public void operateControlAndLock(){
//        Control control =new Control();
//        //参数异常
//        try {
//            lockController.operateControlAndLock(control);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//        //正常
//        control.setOperateCode("0");
//
//        control.setDeviceId("as");
//        ControlParam controlParam =new ControlParam();
//        control.setControlParam(controlParam);
//        lockController.operateControlAndLock(control);
//    }
//
//    @Test
//    public void queryLockByDeviceId() {
//        String deviceId = null;
//        try {
//            lockController.queryLockByDeviceId(deviceId);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//
//        //正常
//        deviceId = "c1";
//        lockController.queryLockByDeviceId(deviceId);
//    }
//
//    @Test
//    public void lockListByDeviceIds() {
//        List<String> deviceList =new ArrayList<>();
//        try {
//            lockController.lockListByDeviceIds(deviceList);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FiLinkLockException.class);
//        }
//
//        //正常
//        deviceList.add("1");
//        lockController.lockListByDeviceIds(deviceList);
//    }
//
//
//}
