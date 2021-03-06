package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.filinkoceanconnectapi.bean.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectapi.bean.RegistryResult;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlParamForControl;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.bean.QrCodeBean;
import com.fiberhome.filink.lockserver.dao.LockDao;
import com.fiberhome.filink.lockserver.exception.*;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.lockserver.stream.CodeSender;
import com.fiberhome.filink.lockserver.util.AuthUtils;
import com.fiberhome.filink.lockserver.util.ListUtils;
import com.fiberhome.filink.lockserver.util.OperateLogUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.onenetapi.bean.CreateDevice;
import com.fiberhome.filink.onenetapi.bean.CreateResult;
import com.fiberhome.filink.onenetapi.bean.DeleteDevice;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.AuthFeign;
import com.fiberhome.filink.userapi.bean.DeviceInfo;
import com.fiberhome.filink.userapi.bean.UserAuthInfo;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * LockServiceImpl?????????
 *
 * @author wanzhaozhang@wistronits.com
 */

@RunWith(JMockit.class)
public class LockServiceImplTest {

    /**
     * ???????????????
     */

    private Lock lock;
    /**
     * ????????????
     */
    private Lock addLock;
    /**
     * ???????????????
     */
    private Lock abandonLock;
    /**
     * ????????????
     */
    private Lock updateLock;

    private OpenLockBean openLockBean;

    private List<Lock> lockList;

    private List<Lock> maxLockList;

    private List<Lock> nameResusedLock;

    /**
     * ???????????????????????????
     */
    private List<Lock> lockList2;

    /**
     * ??????????????????
     */
    private List<Lock> oldLockList;

    private List<String> deviceIdList;

    /**
     * ??????????????????
     */
    List<ControlParam> controlParamList;

    /**
     * ?????????????????? ????????????
     */
    List<ControlParam> controlParamList2;

    List<ControlParamForControl> controlParamForControlList;

    List<String> doorNumList;

    private final String ADMIN = "1";
    private final String NOT_ADMIN = "123";
    /**
     * ????????????
     */

    private DeviceInfoDto deviceInfoDto;
    private Result badResult;
    private Result goodResult;


    /**
     * ????????????????????????????????????????????????????????????
     */
    private Lock lockAside1;
    private Lock lockAside2;
    private Lock lockAside3;


    /**
     * ????????????LockServiceImpl
     */

    @Tested
    private LockServiceImpl lockService;


    /**
     * ????????????lockDao
     */

    @Injectable
    private LockDao lockDao;

    /**
     * ????????????controlService
     */
    @Injectable
    private ControlService controlService;


    /**
     * ????????????????????????
     */

    @Injectable
    private CodeSender codeSender;


    /**
     * ????????????LogProcess
     */

    @Injectable
    private LogProcess logProcess;


    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * ????????????
     */

    @Injectable
    private LogCastProcess logCastProcess;


    /**
     * ????????????DeviceFeign
     */

    @Injectable
    private DeviceFeign deviceFeign;
    @Injectable
    private AuthFeign authFeign;
    @Injectable
    private AuthUtils authUtils;
    @Injectable
    private OceanConnectFeign oceanConnectFeign;
    @Injectable
    private OneNetFeign oneNetFeign;
    @Injectable
    private OperateLogUtils logUtils;

    @Mocked
    private QrCodeBean qrCodeBean;


    /**
     * ????????????  ??????
     */
    private ControlParam controlParam;

    /**
     * ????????????  ??????  ?????????????????????????????????
     */
    private ControlParam controlParamCopy;

    /**
     * ????????????  ??????
     */
    private ControlParam controlParam1;

    /**
     * ????????????
     */
    private ControlParam controlParam2;

    @Mocked
    private I18nUtils i18nUtils;


    private ControlReq controlReq;

    @Mocked
    private ListUtils listUtils;

    @Mocked
    private ControlParamForControl controlParamForControl;


    private PageCondition pageCondition;
    @Mocked
    private RequestInfoUtils requestInfoUtils;
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;
    @Mocked
    private HttpServletRequest request;
    @Mocked
    private MpQueryHelper mpQueryHelper;
    @Mocked
    private RegistryResult registryResult;
    @Mocked
    private CreateResult createResult;


    /**
     * ?????????????????????
     */

    @Before
    public void setUp() {
        //?????????????????????
        lock = new Lock();
        lock.setControlId("3DFFC3413A3F11E9B3520242AC110004");
        lock.setDoorName("door-002");
        lock.setDoorStatus("1");
        lock.setLockNum("1");
        lock.setLockStatus("1");
        lock.setQrCode("a");
        lock.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        lock.setDoorNum("1");
        lock.setDoorName("??????1");
        lock.setLockCode("lockewdwdx");

        addLock = new Lock();
        addLock.setDoorNum("2");
        addLock.setLockNum("2");
        addLock.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        addLock.setControlId("3DFFC3413A3F11E9B3520242AC110004");
        addLock.setLockName("???2");
        addLock.setDoorStatus("2");
        addLock.setLockStatus("2");
        addLock.setDoorName("??????2");
        addLock.setLockCode("lockCode2");

        lockList = new ArrayList<>();
        lockList.add(lock);

        lockList2 = new ArrayList<>();
        lockList2.add(lock);
        lockList2.add(addLock);


        abandonLock = new Lock();

        abandonLock.setDoorNum("3");
        abandonLock.setLockNum("3");
        abandonLock.setLockId("adasdddfAqAD");
        abandonLock.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        abandonLock.setControlId("3DFFC3413A3F11E9B3520242AC110004");
        abandonLock.setDoorName("??????3");

        updateLock = new Lock();
        updateLock.setControlId("3DFFC3413A3F11E9B3520242AC110004");
        updateLock.setDoorName("door-002-1");
        updateLock.setLockNum("1");
        updateLock.setQrCode("3362602052ds");
        updateLock.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        updateLock.setDoorNum("1");
        updateLock.setDoorName("??????A");
        updateLock.setLockCode("updateLockCode");
        oldLockList = new ArrayList<>();
        oldLockList.add(updateLock);
        oldLockList.add(abandonLock);

        lockAside1 = new Lock();
        lockAside2 = new Lock();
        lockAside3 = new Lock();
        lockAside1.setDoorName("???1");
        lockAside1.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        lockAside1.setControlId("3DFFC3413A3F11E9B3520242AC");
        lockAside1.setDoorNum("4");
        lockAside2.setLockNum("4");
        lockAside2.setDoorName("???14");
        lockAside2.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        lockAside2.setControlId("3DFFC3413A3F11E9B3520242AC");
        lockAside2.setDoorNum("42");
        lockAside2.setLockNum("42");
        lockAside3.setDoorName("???122");
        lockAside3.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        lockAside3.setControlId("3DFFC3413A3F11E9B3520242AC");
        lockAside3.setDoorNum("41");
        lockAside3.setLockNum("41");
        maxLockList = new ArrayList<>();
        maxLockList.add(updateLock);
        maxLockList.add(addLock);
        maxLockList.add(lockAside1);
        maxLockList.add(lockAside2);
        maxLockList.add(lockAside3);

        nameResusedLock = new ArrayList<>();
        nameResusedLock.add(updateLock);
        nameResusedLock.add(addLock);
        lockAside1.setDoorName("??????2");
        nameResusedLock.add(lockAside1);


        //??????????????????
        //??????device??????
        deviceInfoDto = new DeviceInfoDto();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("??????001");
        deviceInfoDto.setAreaInfo(areaInfo);
        deviceInfoDto.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        //??????AddLogBean
        AddLogBean addLogBean = new AddLogBean();

        controlParam = new ControlParam();
        controlParam.setHostId("3DFFC3413A3F11E9B3520242AC110004");
        controlParam.setHostName("host1");
        controlParam.setLockList(lockList);
        controlParam.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParam.setCloudPlatform("0");
        controlParam.setHostType("1");


        controlParamCopy = new ControlParam();
        controlParamCopy.setHostId("hostId");
        controlParamCopy.setHostName("host1");
        controlParamCopy.setLockList(lockList);
        controlParamCopy.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParamCopy.setCloudPlatform("0");
        controlParamCopy.setHostType("1");


        controlParam1 = new ControlParam();
        controlParam1.setCloudPlatform("1");
        controlParam1.setHostType("2");
        controlParam1.setHostId("3DFFC3413A3F11E9B3520242AC110004");
        controlParam1.setHostName("host1");
        controlParam1.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParam1.setLockList(lockList);

        controlParam2 = new ControlParam();
        controlParam2.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParam2.setHostId("3DFFC3413A3F11E9B3520242AC110004");
        controlParam2.setHostType("0");
        controlParam2.setHostName("????????????");
        controlParam2.setLockList(lockList);


        controlParamList = new ArrayList<>();
        controlParamList.add(controlParam);


        controlParamList2 = new ArrayList<>();
        controlParamList2.add(controlParam);
        controlParamList2.add(controlParam);
        controlParamList2.add(controlParam);
        controlParamList2.add(controlParam);


        controlParamForControlList = new ArrayList<>();
        controlParamForControlList.add(controlParamForControl);

        doorNumList = new ArrayList<>();
        doorNumList.add("1");
        openLockBean = new OpenLockBean();
        openLockBean.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        openLockBean.setDoorNumList(doorNumList);
        openLockBean.setDoorNum("1");

        deviceIdList = new ArrayList<>();
        deviceIdList.add("wQIOdMGJwYKBgim40NT");

        pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setBeginNum(1);
        pageCondition.setPageSize(5);

        badResult = new Result();
        badResult.setCode(-1);
        badResult.setMsg("bad");

        goodResult = new Result();
        goodResult.setCode(0);
        goodResult.setMsg("good");
        goodResult.setData(registryResult);


        controlReq = new ControlReq();
        controlReq.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlReq.setControlId("3DFFC3413A3F11E9B3520242AC110003");
    }


    @Test
    public void queryLockByControlId() {
        String controlId = "ads";
        lockService.queryLockByControlId(controlId);
    }

    @Test
    public void queryLockAndControlByQrCode() {
        //????????????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = lock;
                controlService.getControlParamById(anyString);
                result = controlParam;
                try {
                    authUtils.addAuth(anyString);
                } catch (Exception e) {

                }
                result = new RuntimeException();

            }

        };

        try {
            lockService.queryLockAndControlByQrCode(qrCodeBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkAccessDenyException.class);
        }

        //????????????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = lock;
                controlService.getControlParamById(anyString);
                result = controlParam;
                try {
                    authUtils.addAuth(anyString);
                } catch (Exception e) {

                }

                try {
                    deviceFeign.getDeviceById(anyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDto;
            }

        };

        try {
            lockService.queryLockAndControlByQrCode(qrCodeBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkAccessDenyException.class);
        }

        //?????????
        new Expectations() {
            {

                lockDao.queryLockByQrCode(anyString);
                result = lock;


                controlService.getControlParamById(anyString);
                result = controlParam;

                RequestInfoUtils.getUserId();
                result = ADMIN;
                lockDao.queryLockByDeviceId(anyString);
                result = lockList;

                try {
                    deviceFeign.getDeviceById(anyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDto;
            }
        };


        lockService.queryLockAndControlByQrCode(qrCodeBean);

    }

    /**
     * ?????????????????????????????????
     */

    @Test
    public void updateLockStatus() {
        lock.setLockId("3dffc3413a3f11e9b3520242ac110003");
        lockService.updateLockStatus(lock);
    }

    /**
     * ???????????????????????????????????????
     */

    @Test
    public void batchUpdateLockStatus() {
        List<Lock> locks = new ArrayList<>();
        Lock lock1 = new Lock();
        lock1.setLockNum("1");
        lock1.setLockStatus("1");
        lock1.setDoorStatus("2");
        Lock lock2 = new Lock();
        lock2.setLockNum("1");
        lock2.setLockStatus("1");
        lock2.setDoorStatus("2");
        locks.add(lock1);
        locks.add(lock2);
        //????????????
        try {
            lockService.batchUpdateLockStatus(locks);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceIsNullException.class);
        }
        lockService.batchUpdateLockStatus(locks);
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void openLockByQrCode() {
        //admin??????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = lock;
                RequestInfoUtils.getUserId();
                result = ADMIN;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                controlService.getControlParamById(anyString);
                result = controlParam;
            }
        };
        lockService.openLockByQrCode(qrCodeBean);

        //???admin,?????????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = lock;
                RequestInfoUtils.getUserId();
                result = NOT_ADMIN;
                authFeign.queryAuthInfoByUserIdAndDeviceAndDoor((UserAuthInfo) any);
                result = true;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                controlService.getControlParamById(anyString);
                result = controlParam;
            }
        };
        lockService.openLockByQrCode(qrCodeBean);

        //???admin,?????????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = lock;
                RequestInfoUtils.getUserId();
                result = NOT_ADMIN;
                authFeign.queryAuthInfoByUserIdAndDeviceAndDoor((UserAuthInfo) any);
                result = false;
            }
        };
        try {
            lockService.openLockByQrCode(qrCodeBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkOpenLockException.class);
        }
    }


    /**
     * ???????????????????????????
     */
    @Test
    public void openLock() {
        //??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByDeviceIdAndDoorNum(anyString, anyString);
                result = lock;
                controlService.getControlParamById(anyString);
                result = controlParam;
            }
        };
        lockService.openLock(openLockBean);
        //??????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByDeviceIdAndDoorNum(anyString, anyString);
                result = null;
            }
        };
        try {
            lockService.openLock(openLockBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkDoorNumIsNullException.class);
        }

        //???????????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByDeviceIdAndDoorNum(anyString, anyString);
                result = lock;
                controlService.getControlParamById(anyString);
                result = controlParam2;
            }
        };
        try {
            lockService.openLock(openLockBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceOperateException.class);
        }
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void openLockForPda() {
        //??????
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = ADMIN;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                controlService.getControlParamById(anyString);
                result = controlParam;
            }
        };
        lockService.openLockForPda(openLockBean);
        //?????????
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = NOT_ADMIN;
                authFeign.queryAuthInfoByUserIdAndDeviceAndDoor((UserAuthInfo) any);
                result = false;

            }
        };
        try {
            lockService.openLockForPda(openLockBean);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkOpenLockException.class);
        }
    }

    /**
     * ????????????id?????????????????????????????????
     */

    @Test
    public void queryLockByDeviceId() {
        lockService.queryLockByDeviceId("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void queryLockStatusByDeviceId() {
        new Expectations() {
            {
                controlService.getControlInfoByDeviceIdToCall(anyString);
                result = controlParamList;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByControlId(anyString);
                result = lockList;

            }
        };

        lockService.queryLockStatusByDeviceId(openLockBean);

        //??????????????????
        new Expectations() {
            {
                controlService.getControlInfoByDeviceIdToCall(anyString);
                result = null;
            }
        };

        lockService.queryLockStatusByDeviceId(openLockBean);

    }

    @Test
    public void queryLockAndControlByDeviceId() {
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                controlService.getControlParamForControlByDeviceId(anyString);
                result = controlParamForControlList;
                lockDao.queryLockByControlId(anyString);
                result = lockList;
            }
        };
        lockService.queryLockAndControlByDeviceId(controlReq);

        //???????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = null;
            }
        };
        try {
            lockService.queryLockAndControlByDeviceId(controlReq);
        } catch (Exception e) {

        }
    }

    @Test
    public void lockListByPage() {
        //admin ??????
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = ADMIN;
                controlService.deviceIdListByPage((PageCondition) any, (List<String>) any);
                result = deviceIdList;

            }
        };
        lockService.lockListByPage(pageCondition);
        //???admin ?????? exception
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = NOT_ADMIN;
                authUtils.getUserDeviceIds();
                result = new RuntimeException();

            }
        };
        lockService.lockListByPage(pageCondition);

        //???admin ?????? ??????
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = NOT_ADMIN;
                authUtils.getUserDeviceIds();
                result = deviceIdList;

            }
        };
        lockService.lockListByPage(pageCondition);
    }

    @Test
    public void operateControlAndLock() {
        Control control = new Control();
        control.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        control.setOperateCode("0");
        //?????? ??????  ??????onenet
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = "defaultParams";
                oneNetFeign.createDevice((CreateDevice) any);
                result = goodResult;

            }
        };
        control.setControlParam(controlParam1);
        lockService.operateControlAndLock(control);


        //????????????  ??????onenet
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = "defaultParams";
                oneNetFeign.createDevice((CreateDevice) any);
                result = badResult;

            }
        };
        lockService.operateControlAndLock(control);

        control.setControlParam(controlParam);
        //?????? ??????  ??????oceanconnet
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = "defaultParams";
                oceanConnectFeign.registryDevice((OceanDevice) any);
                result = goodResult;

            }
        };
        lockService.operateControlAndLock(control);


        //?????? ?????????configValue
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = null;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //?????? ??????oceanconnet????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;

                lockDao.queryLockByLockCode(anyString);
                result = null;

                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = "defaultParams";
                oceanConnectFeign.registryDevice((OceanDevice) any);
                result = badResult;

            }
        };
        lockService.operateControlAndLock(control);

        //?????? ?????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;

                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = null;
                deviceFeign.getDefaultParams(anyString);
                result = "defaultParams";
                oceanConnectFeign.registryDevice((OceanDevice) any);
                result = goodResult;
                lockDao.addLocks((List<Lock>) any);
                result = new RuntimeException();

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //?????? lockCode??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = lock;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //?????? qrCode??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = lock;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //?????? ??????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList2;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlMaxNumException.class);
        }

        //?????? ?????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = maxLockList;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDoorMaxNumException.class);
        }

        //?????? ????????????????????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = lockList;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = controlParam;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //?????? ?????????????????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlInfoByDeviceId(anyString);
                result = controlParamList;
                lockDao.queryLockByDeviceId(anyString);
                result = lockList;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {

        }

        //??????
        control.setOperateCode("1");

        //?????? ??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;


                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = lockList;

                lockDao.countLocks((List<Lock>) any, anyString);
                result = lockList.size();
            }
        };
        lockService.operateControlAndLock(control);

        //?????? ???????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = controlParamCopy;

            }
        };
        try {
            lockService.operateControlAndLock(control);
        }catch (Exception e){

        }

        //?????? ?????? ???????????????  ????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;


                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = oldLockList;

                lockDao.countLocks((List<Lock>) any, anyString);
                result = lockList.size();

                lockDao.addLocks((List<Lock>) any);

            }
        };
        control.getControlParam().setLockList(lockList2);
        lockService.operateControlAndLock(control);

        //?????? ?????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;


                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = lockList;

                lockDao.countLocks((List<Lock>) any, anyString);
                result = lockList.size() + 1;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSensorListException.class);
        }

        //?????? ?????????????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;

                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;

                lockDao.queryLockByDeviceId(anyString);
                result = maxLockList;


            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDoorMaxNumException.class);
        }

        //?????? ???????????????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = null;
                controlService.getControlByDeviceIdAndControlName((ControlParam) any);
                result = null;
                lockDao.queryLockByDeviceId(anyString);
                result = nameResusedLock;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceDoorNameReusedException.class);
        }

        //?????? qrcode??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = lock;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkQrCodeReusedException.class);
        }

        //?????? qrcode??????
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockDao.queryLockByQrCode(anyString);
                result = null;
                lockDao.queryLockByLockCode(anyString);
                result = lock;
            }
        };
        try {
            lockService.operateControlAndLock(control);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkLockCodeReusedException.class);
        }
    }

    @Test
    public void deleteAuth() {
        //??????
        lockService.deleteAuth("11", doorNumList);

        new Expectations() {
            {
                authFeign.deleteAuthByDevice((DeviceInfo) any);
                result = null;
            }
        };
        try {
            lockService.deleteAuth("11", doorNumList);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }
    }

    @Test
    public void lockListByDeviceIds() {
        lockService.lockListByDeviceIds(deviceIdList);
    }

    @Test
    public void queryLockByQrCode() {
        //??????????????????
        new Expectations() {
            {
                lockDao.queryLockByQrCode(anyString);
                result = null;
            }
        };
        try {
            lockService.queryLockByQrCode("a");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkQrCodeErrorException.class);
        }
    }

    @Test
    public void queryLockByDeviceIdAndDoorNum() {
        new Expectations() {
            {
                lockDao.queryLockByDeviceIdAndDoorNum(anyString, anyString);
                result = null;
            }
        };
        try {
            lockService.queryLockByDeviceIdAndDoorNum("deviceId", "2");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlIsNullException.class);
        }
    }

    @Test
    public void deleteFromPlatForm() {
        //???????????? ??????
        new Expectations() {
            {
                oceanConnectFeign.deleteDevice((OceanDevice) any);
                result = badResult;
            }
        };
        try {
            lockService.deleteFromPlatForm(controlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }

        //???????????? ??????
        new Expectations() {
            {
                oceanConnectFeign.deleteDevice((OceanDevice) any);
                result = goodResult;
            }
        };
        lockService.deleteFromPlatForm(controlParam);

        //????????????  ??????
        new Expectations() {
            {
                oneNetFeign.deleteDevice((DeleteDevice) any);
                result = badResult;
            }
        };
        try {
            lockService.deleteFromPlatForm(controlParam1);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }
        //???????????? ??????
        new Expectations() {
            {
                oneNetFeign.deleteDevice((DeleteDevice) any);
                result = goodResult;
            }
        };
        lockService.deleteFromPlatForm(controlParam1);


    }
}
