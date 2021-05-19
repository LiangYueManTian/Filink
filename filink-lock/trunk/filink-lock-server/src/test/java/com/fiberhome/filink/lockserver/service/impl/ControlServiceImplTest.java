
package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceConfigFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.api.StatisticsFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.bean.UpdateDeviceStatusPda;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.RemoveAlarm;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.dao.ControlDao;
import com.fiberhome.filink.lockserver.exception.FiLinkAccessDenyException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlIdReusedException;
import com.fiberhome.filink.lockserver.exception.FiLinkControlIsNullException;
import com.fiberhome.filink.lockserver.exception.FiLinkDeviceIsNullException;
import com.fiberhome.filink.lockserver.exception.FiLinkDeviceOperateException;
import com.fiberhome.filink.lockserver.exception.FiLinkSystemException;
import com.fiberhome.filink.lockserver.service.LockService;
import com.fiberhome.filink.lockserver.stream.CodeSender;
import com.fiberhome.filink.lockserver.stream.ControlSender;
import com.fiberhome.filink.lockserver.util.AuthUtils;
import com.fiberhome.filink.lockserver.util.CheckUtil;
import com.fiberhome.filink.lockserver.util.OperateLogUtils;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ControlServiceImpl测试类
 *
 * @author wanzhaozhang@wistronits.com
 */

@RunWith(JMockit.class)
public class ControlServiceImplTest {

    /**
     * 有源主控
     */
    private ControlParam controlParam;
    /**
     * 有源主控
     */
    private ControlParam controlParam2;
    /**
     * 无源主控
     */
    private ControlParam controlParam3;

    /**
     * 有源主控集合 两个主控
     */
    private List<ControlParam> controlParamList;
    /**
     * 无源主控集合
     */
    private List<ControlParam> controlParamList2;
    /**
     * 有源主控集合  一个主控
     */
    private List<ControlParam> oneControlParamList;


    private Lock lock;
    private List<Lock> locks;

    private List<String> deviceIds;
    private List<String> deviceIdList;

    private List<DeviceInfoDto> deviceInfoDtos;
    private List<DeviceInfoDto> deviceInfoDtoList;
    private DeviceInfoDto deviceInfoDto;

    private Result returnResult;



    /**
     * 模拟MasterControlServiceImpl
     */

    @Mocked
    @Tested
    private ControlServiceImpl controlService;


    /**
     * 模拟MasterControlDao
     */

    @Injectable
    ControlDao controlDao;


    @Injectable
    private LockService lockService;
    @Injectable
    DeviceConfigFeign deviceConfigFeign;


    /**
     * 模拟DeviceFeign
     */

    @Injectable
    private DeviceFeign deviceFeign;


    /**
     * 模拟LogProcess
     */

    @Injectable
    private LogProcess logProcess;
    @Injectable
    private OceanConnectFeign oceanConnectFeign;
    @Injectable
    private OneNetFeign oneNetFeign;

    /**
     * 模拟LogCastProcess
     */

    @Injectable
    private LogCastProcess logCastProcess;

    @Injectable
    private AuthUtils authUtils;
    @Injectable
    private CodeSender codeSender;
    @Injectable
    private OperateLogUtils logUtils;
    @Injectable
    private ControlSender controlSender;
    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;
    @Injectable
    private ProcBaseFeign procBaseFeign;
    @Mocked
    private CheckUtil checkUtil;

    @Injectable
    private MongoTemplate mongoTemplate;
    @Injectable
    private StatisticsFeign statisticsFeign;


    @Mocked
    private ServletRequestAttributes servletRequestAttributes;
    @Mocked
    private HttpServletRequest request;
    @Mocked
    private RequestInfoUtils requestInfoUtils;
    @Mocked
    private ControlReq controlReq;
    @Mocked
    private SetConfigBean setConfigBean;
    @Mocked
    private Map<String, String> map;
    @Mocked
    private I18nUtils i18nUtils;


    private String configValue;

    private SetConfigBean setConfigBean2;

    private Map<String, String> configParamMap;


    /**
     * 初始化主控对象
     */

    @Before
    public void setUp() {
        controlParam = new ControlParam();
        controlParam.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParam.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        controlParam.setSyncStatus("1");
        controlParam.setHardwareVersion("stm32L4-v001");
        controlParam.setCloudPlatform("0");
        controlParam.setSoftwareVersion("RP9003.002G.bin");
        controlParam.setHostType("1");
        controlParam.setDeployStatus("2");
        controlParam.setActualValue("{\"lean\":{\"data\":\"0\",\"alarmFlag\":\"2\"},\"moduleType\":{\"data\":\"2\",\"alarmFlag\":\"1\"},\"constructedState\":\"0\",\"temperature\":{\"data\":\"27\",\"alarmFlag\":\"2\"},\"humidity\":{\"data\":\"60\",\"alarmFlag\":\"2\"},\"electricity\":{\"data\":\"99\",\"alarmFlag\":\"2\"},\"supplyElectricityWay\":{\"data\":\"0\",\"alarmFlag\":\"1\"},\"leach\":{\"data\":\"1\",\"alarmFlag\":\"2\"},\"operator\":{\"data\":\"3\",\"alarmFlag\":\"1\"},\"wirelessModuleSignal\":{\"data\":\"26\",\"alarmFlag\":\"1\"}}");

        controlParam2 = new ControlParam();
        controlParam2.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        controlParam2.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        controlParam2.setSyncStatus("1");
        controlParam2.setHardwareVersion("stm32L4-v001");
        controlParam2.setCloudPlatform("1");
        controlParam2.setSoftwareVersion("RP9003.002G.bin");
        controlParam2.setDeployStatus("2");
        controlParam2.setHostType("1");

        controlParamList = new ArrayList<>();
        controlParamList.add(controlParam);
        controlParamList.add(controlParam2);

        controlParam3 = new ControlParam();
        controlParam3.setHostType("0");
        controlParam3.setControlId("controlId");


        controlParamList2 = new ArrayList<>();
        controlParamList2.add(controlParam3);


        oneControlParamList = new ArrayList<>();
        oneControlParamList.add(controlParam);

        lock = new Lock();
        lock.setDoorNum("2");
        lock.setDoorName("门");
        lock.setLockId("2saaca");
        lock.setLockNum("2");
        lock.setLockName("锁");
        lock.setControlId("hostId");

        locks = new ArrayList<>();
        locks.add(lock);

        deviceIds = new ArrayList<>();
        deviceIds.add("wQIOdMGJwYKBgim40NT");

        deviceIdList = new ArrayList<>();
        deviceIdList.add("");
        deviceIdList.add("wQIOdMGJwYKBgim40NT");

        deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("pWtqQjoI7drgDgns5Fb");
        deviceInfoDto.setDeviceName("testpdadevice1243");
        deviceInfoDto.setDeviceType("001");
        deviceInfoDto.setDeviceStatus("1");
        deviceInfoDto.setDeployStatus("6");
        deviceInfoDto.setDeviceCode("41610010431079");
        deviceInfoDto.setAddress("jinitaimei");

        deviceInfoDtos = new ArrayList<>();
        deviceInfoDtos.add(deviceInfoDto);

        deviceInfoDtoList = new ArrayList<>();
        deviceInfoDtoList.add(deviceInfoDto);
        deviceInfoDtoList.add(deviceInfoDto);

        returnResult = new Result(1);

        setConfigBean2 = new SetConfigBean();
        setConfigBean2.setDeviceIds(deviceIds);
        configParamMap = new HashMap<>();
        configParamMap.put("electricity", "25");
        configParamMap.put("temperature", "43");
        configParamMap.put("day", "");
        setConfigBean2.setConfigParams(configParamMap);

    }


    /**
     * 根据设施id查询主控测试方法
     */

    @Test
    public void getControlInfoByDeviceId() {
        //网页调用
        controlService.getControlInfoByDeviceId("2df750a8377f11e9b3520242ac110003");
    }

    @Test
    public void getControlInfoByDeviceIdToCall() {
        //feign调用
        new Expectations() {
            {
                requestInfoUtils.getUserId();
                result = new RuntimeException();
            }
        };
        try {
            controlService.getControlInfoByDeviceIdToCall("2df750a8377f11e9b3520242ac110003");
        } catch (Exception e) {

        }

        new Expectations() {
            {
                requestInfoUtils.getUserId();
                result = "0";
                authUtils.addAuth(anyString);
                result = new FiLinkAccessDenyException();
            }
        };
        controlService.getControlInfoByDeviceIdToCall("2df750a8377f11e9b3520242ac110003");

        new Expectations() {
            {
                requestInfoUtils.getUserId();
                result = "0";
                authUtils.addAuth(anyString);
            }
        };
        controlService.getControlInfoByDeviceIdToCall("2df750a8377f11e9b3520242ac110003");


    }

    @Test
    public void getControlByDeviceIdAndControlName() {
        controlService.getControlByDeviceIdAndControlName(controlParam);
    }

    @Test
    public void getControlParamForControlByDeviceId() {
        controlService.getControlParamForControlByDeviceId("s");
    }

    @Test
    public void queryControlByControlId() {
        //主控不存在
        new Expectations() {
            {
                controlDao.queryControlParam((ControlReq) any);
                result = null;
            }
        };
        controlService.queryControlByControlId(controlReq);

        //主控存在,锁不存在
        new Expectations() {
            {
                controlDao.queryControlParam((ControlReq) any);
                result = controlParamList;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                lockService.queryLockByControlId(anyString);
                result = Collections.EMPTY_LIST;

            }
        };
        controlService.queryControlByControlId(controlReq);

        //主控存在,锁存在
        new Expectations() {
            {
                controlDao.queryControlParam((ControlReq) any);
                result = controlParamList;
                lockService.queryLockByControlId(anyString);
                result = locks;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };
        controlService.queryControlByControlId(controlReq);

    }

    @Test
    public void getControlParamById() {
        //不存在
        new Expectations() {
            {
                controlDao.getControlParamById(anyString);
                result = null;
            }
        };
        try {
            controlService.getControlParamById("s");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlIsNullException.class);
        }

        //存在
        new Expectations() {
            {
                controlDao.getControlParamById(anyString);
                result = controlParam;
            }
        };
        controlService.getControlParamById("s");
    }

    @Test
    public void addControlParam() {
        //设施不存在
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = null;
            }
        };
        try {
            controlService.addControlParam(controlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceIsNullException.class);
        }
        //有源
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
                controlDao.getDeployStatusById(anyString);
                result = controlParamList;
            }
        };
        controlService.addControlParam(controlParam);
        //无源
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };
        controlService.addControlParam(controlParam3);
    }

    @Test
    public void updateControlParam() {
        controlParam.setHostName("test");
        controlService.updateControlParam(controlParam, LogConstants.OPT_TYPE_PDA);
    }

    @Test
    public void updateDeployStatusByDeviceId() {
        //正常
        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtos;
                controlDao.getControlByDeviceId(anyString);
                result = controlParamList;
                deviceFeign.updateDeviceListStatus((UpdateDeviceStatusPda) any);
                result = returnResult;
            }
        };
        controlService.updateDeployStatusByDeviceId(deviceIds, "1");
        controlService.updateDeployStatusByDeviceId(deviceIds, "2");
        controlService.updateDeployStatusByDeviceId(deviceIds, "3");
        controlService.updateDeployStatusByDeviceId(deviceIds, "4");


        //更新设施的设施状态失败
        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtos;
                controlDao.getControlByDeviceId(anyString);
                result = controlParamList;
                deviceFeign.updateDeviceListStatus((UpdateDeviceStatusPda) any);
                result = null;
            }
        };
        try {
            controlService.updateDeployStatusByDeviceId(deviceIds, "2");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }

        //批量操作时缺少设施id
        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtoList;

            }
        };
        try {
            controlService.updateDeployStatusByDeviceId(deviceIds, "2");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceIsNullException.class);
        }

        //批量设施不存在主控
        new Expectations() {
            {
                controlDao.getControlByDeviceId(anyString);
                result = null;
            }
        };
        try {
            controlService.updateDeployStatusByDeviceId(deviceIds, "2");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceOperateException.class);
        }
        //设置主控为无源
        new Expectations() {
            {
                controlDao.getControlByDeviceId(anyString);
                result = controlParamList2;
            }
        };
        try {
            controlService.updateDeployStatusByDeviceId(deviceIds, "2");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkDeviceOperateException.class);
        }


    }

    @Test
    public void deleteControlByDeviceIds() {
        new Expectations() {
            {
                controlDao.getControlByDeviceId(anyString);
                result = controlParamList;
                lockService.queryLockByDeviceId(anyString);
                result = locks;
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtos;
            }
        };

        controlService.deleteControlByDeviceIds(deviceIds);

        new Expectations() {
            {
                controlDao.getControlByDeviceId(anyString);
                result = controlParamList;
                lockService.queryLockByDeviceId(anyString);
                result = locks;
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtos;
            }
        };

        controlService.deleteControlByDeviceIds(deviceIds);
    }

    @Test
    public void updateDeployStatusById() {


        controlService.updateDeployStatusById(controlParam);

        //正常
        new Expectations() {
            {
                controlDao.getDeployStatusById(anyString);
                result = oneControlParamList;
            }
        };

        controlService.updateDeployStatusById(controlParam);
    }


    @Test
    public void setConfig() {
        //获取不到配置属性
        new Expectations() {
            {
                try {
                    deviceConfigFeign.getConfigPatterns();
                } catch (Exception e) {

                }
                result = new RuntimeException();

            }
        };
        try {
            controlService.setConfig(setConfigBean);
        } catch (Exception e) {

        }

        //规则不对
        new Expectations() {
            {
                try {
                    deviceConfigFeign.getConfigPatterns();
                } catch (Exception e) {

                }
                result = map;
                CheckUtil.checkConfig((Map<String, String>) any, (Map<String, String>) any);
                result = false;

            }
        };
        try {
            controlService.setConfig(setConfigBean);
        } catch (Exception e) {

        }

        //所选设施中包含没有主控信息的设施
        new Expectations() {
            {
                try {
                    deviceConfigFeign.getConfigPatterns();
                } catch (Exception e) {

                }
                result = map;
                CheckUtil.checkConfig((Map<String, String>) any, (Map<String, String>) any);
                result = true;

                setConfigBean.getDeviceIds();
                result = deviceIds;
                controlDao.getControlByDeviceId(anyString);
                result = null;

            }
        };
        controlService.setConfig(setConfigBean);


        //正常
        new Expectations() {
            {
                setConfigBean.getConfigParams();
                result = configParamMap;
                setConfigBean.getDeviceIds();
                result = deviceIds;
                try {
                    deviceConfigFeign.getConfigPatterns();
                } catch (Exception e) {

                }
                result = map;
                CheckUtil.checkConfig((Map<String, String>) any, (Map<String, String>) any);
                result = true;
                controlDao.getControlByDeviceId(anyString);
                result = controlParam;
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;


            }
        };
        try {
            controlService.setConfig(setConfigBean);
        } catch (Exception e) {

        }
    }

    @Test
    public void updateDeviceStatusById() {
        new Expectations() {
            {
                controlDao.getDeviceStatusById(anyString);
                result = controlParam2;
            }
        };
        controlService.updateDeviceStatusById(controlParam);

        //feign调用异常
        new Expectations() {
            {

                controlDao.getDeviceStatusById(anyString);
                result = controlParam2;
                try {
                    deviceFeign.updateDeviceStatus((DeviceInfoDto) any);
                } catch (Exception r) {

                }
                result = new FiLinkSystemException("");
            }
        };
        try {
            controlService.updateDeviceStatusById(controlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }
        //feign调用异常
        new Expectations() {
            {

                controlDao.getDeviceStatusById(anyString);
                result = controlParam2;
                try {
                    deviceFeign.updateDeviceStatus((DeviceInfoDto) any);
                } catch (Exception r) {

                }
                result = null;
            }
        };
        try {
            controlService.updateDeviceStatusById(controlParam);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkSystemException.class);
        }
    }

    @Test
    public void deviceIdList() {
        controlService.deviceIdList();
    }

    @Test
    public void getDeviceIdByControlId() {
        String controlId = "controlId";
        try {
            controlService.getDeviceIdByControlId(controlId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlException.class);
        }
        new Expectations() {
            {
                controlDao.getDeviceIdByControlId(anyString);
                result = "deviceId";

            }
        };
        controlService.getDeviceIdByControlId(controlId);
    }

    @Test
    public void deleteLockAndControlByControlId() {
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };
        controlService.deleteLockAndControlByControlId(controlReq);
    }

    @Test
    public void checkControlIdReused() {
        String controlId = "controlId";
        new Expectations() {
            {
                controlDao.getControlParamById(anyString);
                result = null;
            }
        };
        controlService.checkControlIdReused(controlId);

        new Expectations() {
            {
                controlDao.getControlParamById(anyString);
                result = controlParam;

            }
        };
        try {
            controlService.checkControlIdReused(controlId);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FiLinkControlIdReusedException.class);
        }
    }

    @Test
    public void deviceIdListByPage() {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setBeginNum(1);
        pageCondition.setPageSize(5);
        controlService.deviceIdListByPage(pageCondition, deviceIds);
    }

    @Test
    public void updateControlStatusById() {
        new Expectations() {
            {
                controlDao.getDeviceStatusById(anyString);
                result = controlParam;
                controlDao.getDeployStatusById(anyString);
                result = oneControlParamList;

            }
        };
        controlService.updateControlStatusById(controlParam);
    }

    @Test
    public void addSensor() {
        //没有主控传感值
        controlService.addSensor(controlParam2);
        //有主控传感值
        controlService.addSensor(controlParam);
        //有主控传感值，转换失败
        controlService.addSensor(controlParam);

    }

    @Test
    public void removeAlarm() {
        List<RemoveAlarm> removeAlarmList = new ArrayList<>();
        RemoveAlarm removeAlarm = new RemoveAlarm();
        removeAlarm.setControlId("123456");
        removeAlarm.setAlarmType(Arrays.asList("leach","temperature","humidity","wirelessModuleSignal","electricity"));
        removeAlarmList.add(removeAlarm);

        ControlParam  controlParam = new ControlParam();
        controlParam.setHostId("123456");
        controlParam.setActualValue("{\"temperature\":{\"data\":\"25\",\"alarmFlag\":\"1\"},\"humidity\":{\"data\":\"50\",\"alarmFlag\":\"1\"},\"electricity\":{\"data\":\"88\",\"alarmFlag\":\"1\"},\"leach\":{\"alarmFlag\":\"1\"},\"wirelessModuleSignal\":{\"data\":\"20\",\"alarmFlag\":\"1\"}}");

        new Expectations(BeanUtils.class) {
            {

                controlDao.getControlParamById(anyString);
                result = controlParam;

            }
        };

        controlService.removeAlarm(removeAlarmList);
    }

}
