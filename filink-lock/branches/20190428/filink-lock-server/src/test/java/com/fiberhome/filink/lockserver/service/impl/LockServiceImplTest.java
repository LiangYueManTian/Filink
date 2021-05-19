//package com.fiberhome.filink.lockserver.service.impl;
//
//import com.fiberhome.filink.bean.PageCondition;
//import com.fiberhome.filink.bean.QueryCondition;
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.bean.ResultUtils;
//import com.fiberhome.filink.deviceapi.api.DeviceFeign;
//import com.fiberhome.filink.deviceapi.bean.AreaInfo;
//import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
//import com.fiberhome.filink.lockserver.bean.Control;
//import com.fiberhome.filink.lockserver.bean.ControlParam;
//import com.fiberhome.filink.lockserver.bean.ControlParamForControl;
//import com.fiberhome.filink.lockserver.bean.ControlReq;
//import com.fiberhome.filink.lockserver.bean.Lock;
//import com.fiberhome.filink.lockserver.bean.OpenLockBean;
//import com.fiberhome.filink.lockserver.bean.QrcodeBean;
//import com.fiberhome.filink.lockserver.dao.LockDao;
//import com.fiberhome.filink.lockserver.service.ControlService;
//import com.fiberhome.filink.lockserver.util.ListUtils;
//import com.fiberhome.filink.logapi.bean.AddLogBean;
//import com.fiberhome.filink.logapi.log.LogCastProcess;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mocked;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * LockServiceImpl测试类
// *
// * @author CongcaiYu
// */
//
//@RunWith(JMockit.class)
//public class LockServiceImplTest {
//
//    /**
//     * 电子锁对象
//     */
//
//    private Lock lock;
//
//    private List<Lock> lockList;
//
//    /**
//     * 主控参数集合
//     */
//    List<ControlParam> controlParamList;
//
//    List<ControlParamForControl> controlParamForControlList;
//
//    /**
//     * 设施信息
//     */
//
//    private DeviceInfoDto deviceInfoDto;
//
//
//    /**
//     * 测试对象LockServiceImpl
//     */
//
//    @Tested
//    private LockServiceImpl lockService;
//
//
//    /**
//     * 模拟对象lockDao
//     */
//
//    @Injectable
//    private LockDao lockDao;
//
//    /**
//     * 模拟对象controlService
//     */
//    @Injectable
//    private ControlService controlService;
//
//
//    /**
//     * 模拟对象FiLinkStationFeign
//     */
//
//    @Injectable
//    private FiLinkStationFeign fiLinkStationFeign;
//
//
//    /**
//     * 模拟对象LogProcess
//     */
//
//    @Injectable
//    private LogProcess logProcess;
//
//
//    /**
//     * 模拟对象
//     */
//
//    @Injectable
//    private LogCastProcess logCastProcess;
//
//
//    /**
//     * 模拟对象DeviceFeign
//     */
//
//    @Injectable
//    private DeviceFeign deviceFeign;
//
//    @Mocked
//    private QrcodeBean qrcodeBean;
//
//
//    private ControlParam controlParam;
//
//    @Mocked
//    private I18nUtils i18nUtils;
//
//    @Mocked
//    private ControlReq controlReq;
//
//    @Mocked
//    private ListUtils listUtils;
//
//    @Mocked
//    private ControlParamForControl controlParamForControl;
//
//    @Mocked
//    private PageCondition pageCondition;
//
//
//    /**
//     * 初始化对象属性
//     */
//
//    @Before
//    public void setUp() {
//        //构造电子锁对象
//        lock = new Lock();
//        lock.setControlId("3dffc3413a3f11e9b3520242ac110003");
//        lock.setDoorName("door-002");
//        lock.setDoorStatus("1");
//        lock.setLockNum("1");
//        lock.setLockStatus("1");
//        lock.setQrcode("a");
//
//        lockList = new ArrayList<>();
//        lockList.add(lock);
//
//        //构造设施对象
//        //构造device对象
//        deviceInfoDto = new DeviceInfoDto();
//        AreaInfo areaInfo = new AreaInfo();
//        areaInfo.setAreaName("区域001");
//        deviceInfoDto.setAreaInfo(areaInfo);
//        deviceInfoDto.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
//        //构造AddLogBean
//        AddLogBean addLogBean = new AddLogBean();
//
//        controlParam = new ControlParam();
//        controlParam.setHostId("dad");
//        controlParam.setHostName("host1");
//        controlParam.setLockList(lockList);
//
//        controlParamList = new ArrayList<>();
//        controlParamList.add(controlParam);
//
//        controlParamForControlList = new ArrayList<>();
//        controlParamForControlList.add(controlParamForControl);
//    }
//
//
//    @Test
//    public void queryLockByControlId() {
//        String controlId = "ads";
//        lockService.queryLockByControlId(controlId);
//    }
//
//    @Test
//    public void queryLockAndControlByQrcode() {
//        new Expectations() {
//            {
//                lockDao.queryLockByQrcode(anyString);
//                result = lock;
//            }
//
//            {
//                controlService.getControlParamById(anyString);
//                result = controlParam;
//            }
//
//            {
//                try {
//                    deviceFeign.getDeviceById(anyString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                result = deviceInfoDto;
//            }
//
//        };
//
//        lockService.queryLockAndControlByQrcode(qrcodeBean);
//    }
//
//    /**
//     * 更新电子锁状态测试方法
//     */
//
//    @Test
//    public void updateLockStatus() {
//        lock.setLockId("3dffc3413a3f11e9b3520242ac110003");
//        lockService.updateLockStatus(lock);
//    }
//
//    /**
//     * 批量更新电子锁状态测试方法
//     */
//
//    @Test
//    public void batchUpdateLockStatus() {
//        List<Lock> locks = new ArrayList<>();
//        Lock lock1 = new Lock();
//        lock1.setLockNum("1");
//        lock1.setLockStatus("1");
//        lock1.setDoorStatus("2");
//        Lock lock2 = new Lock();
//        lock2.setLockNum("1");
//        lock2.setLockStatus("1");
//        lock2.setDoorStatus("2");
//        locks.add(lock1);
//        locks.add(lock2);
//        lockService.batchUpdateLockStatus(locks);
//    }
//
//    /**
//     * 二维码远程开锁测试
//     */
///*    @Test
//    public void openLockByQrcode() {
//        new Expectations() {
//            {
//                lockDao.queryLockByQrcode(anyString);
//                result = lock;
//                deviceFeign.getDeviceById(anyString);
//                result = deviceInfoDto;
//                fiLinkStationFeign.sendUdpReq((FiLinkReqParamsDto) any);
//                result = ResultUtils.success();
//            }
//        };
//        lockService.openLockByQrcode(qrcodeBean);
//    }*/
//
//
//
//
//    /**
//     * 二维码远程开锁测试
//     */
// /*   @Test
//    public void openLockForPda() {
//        new Expectations() {
//            {
//                fiLinkStationFeign.sendUdpReq((FiLinkReqParamsDto) any);
//                result = ResultUtils.success();
//            }
//        };
//        lockService.openLockByQrcode(qrcodeBean);
//    }*/
//
//    /**
//     * 根据设施id查询电子锁信息测试方法
//     */
//
//    @Test
//    public void queryLockByDeviceId() {
//        lockService.queryLockByDeviceId("3dffc3413a3f11e9b3520242ac110003");
//    }
//
//    @Test
//    public void queryLockStatusByDeviceId() {
//        new Expectations() {
//            {
//                deviceFeign.getDeviceById(anyString);
//                result = deviceInfoDto;
//                controlService.getControlInfoByDeviceId(anyString);
//                result = controlParamList;
//                lockDao.queryLockByControlId(anyString);
//                result = lock;
//            }
//        };
//        OpenLockBean lockBean = new OpenLockBean();
//        lockService.queryLockStatusByDeviceId(lockBean);
//    }
//
//    @Test
//    public void queryLockAndControlByDeviceId() {
//        new Expectations() {
//            {
//                deviceFeign.getDeviceById(anyString);
//                result = deviceInfoDto;
//                controlService.getControlParamForControlByDeviceId(anyString);
//                result = controlParamForControlList;
//                lockDao.queryLockByControlId(anyString);
//                result = lock;
//            }
//        };
//        lockService.queryLockAndControlByDeviceId(controlReq);
//    }
//
//    @Test
//    public void deleteLockAndContorlByDeviceId() {
//        lockService.deleteLockAndContorlByDeviceId(new OpenLockBean());
//    }
//
//    @Test
//    public void lockListByPage() {
//        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
//        deviceInfoDtoList.add(deviceInfoDto);
//        Result result1 = ResultUtils.success(deviceInfoDtoList);
//        new Expectations() {
//            {
//                deviceFeign.deviceListByPage((QueryCondition<DeviceInfoDto>) any);
//                result = result1;
//                deviceFeign.getDeviceById(anyString);
//                result = deviceInfoDto;
//                controlService.getControlInfoByDeviceId(anyString);
//                result = controlParamList;
//            }
//        };
//        lockService.lockListByPage(pageCondition);
//    }
//
//    @Test
//    public void operateControlAndLock() {
//        Control control = new Control();
//        control.setDeviceId("dad");
//        control.setOperateCode("0");
//        control.setControlParam(controlParam);
//
//        new Expectations() {
//            {
//                deviceFeign.getDeviceById(anyString);
//                result = deviceInfoDto;
//                lockDao.queryLockByQrcode(anyString);
//                result = null;
//                lockDao.queryLockByDeviceIdAndDoorNum(anyString, anyString);
//                result = null;
//                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
//                result = null;
//                controlService.getControlInfoByDeviceId(anyString);
//                result = controlParamList;
//                lockDao.queryLockByDeviceId(anyString);
//                result = null;
//            }
//        };
//
//        lockService.operateControlAndLock(control);
//    }
//
//}
