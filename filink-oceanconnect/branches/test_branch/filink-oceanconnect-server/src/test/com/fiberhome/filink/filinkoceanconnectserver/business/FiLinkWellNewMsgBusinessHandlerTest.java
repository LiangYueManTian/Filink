package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.util.DeployStatus;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.sender.FiLinkOceanConnectWellSender;
import com.fiberhome.filink.filinkoceanconnectserver.sender.SendUtil;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class FiLinkWellNewMsgBusinessHandlerTest {
    @Tested
    private FiLinkWellNewMsgBusinessHandler fiLinkWellNewMsgBusinessHandler;
    @Injectable
    private FiLinkOceanConnectWellSender sender;

    @Injectable
    private LockFeign lockFeign;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private DeviceLogFeign deviceLogFeign;

    @Injectable
    private DeviceFeign deviceFeign;

    @Injectable
    private FiLinkKafkaSender streamSender;

    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;
    @Injectable
    private CommonUtil commonUtil;
    @Injectable
    private SendUtil sendUtil;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    @Mocked
    private RedisUtils redisUtils;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: handleMsg(AbstractResOutputParams abstractResOutputParams)
     */
    @Test
    public void testHandleMsg() throws Exception {
        AbstractResOutputParams abstractResOutputParams = new FiLinkOceanResOutputParams();
        abstractResOutputParams.setCmdId(CmdId.WEII_HEART_BEAT);
        Map<String, Object> dataSource = new HashMap<>();
        dataSource.put(ParamsKey.DEPLOY_STATUS, DeployStatus.DEPLOYING.getCode());
        dataSource.put(ParamsKey.ELECTRICITY, "50");
        dataSource.put(ParamsKey.HUMIDITY, "90");
        dataSource.put(ParamsKey.HEART_BEAT_CYCLE, "1440");
        dataSource.put(ParamsKey.WORK_TIME, "5");
        dataSource.put(ParamsKey.TIME, 1561951940);
        Map<String, Object> temperatureThreshold = new HashMap<>();
        temperatureThreshold.put(ParamsKey.HIGH_TEMPERATURE, "50");
        temperatureThreshold.put(ParamsKey.LOW_TEMPERATURE, "-10");
        dataSource.put(WellConstant.TEMPERATURE_THRESHOLD, temperatureThreshold);
        dataSource.put(WellConstant.ACTUAL_POWER, "123456");
        dataSource.put(ParamsKey.TEMPERATURE, 90.00);
        dataSource.put(ParamsKey.LEACH, "2");
        //外盖告警
        Map<String, String> outCoverAlarmType = new HashMap<>();
        outCoverAlarmType.put(WellConstant.DOOR_LOCK_A, "0");
        dataSource.put(WellConstant.DOOR_ALARM_TYPE, outCoverAlarmType);

        //锁告警
        Map<String, String> handleAlarmType = new HashMap<>();
        handleAlarmType.put(WellConstant.DOOR_LOCK_A, "unLock");
        dataSource.put(WellConstant.HANDLE_ALARM_TYPE, handleAlarmType);
        //外盖状态map
        Map<Integer, String> outCoverMap = new HashMap<>();
        outCoverMap.put(1, WellConstant.OPEAN);
        //锁状态
        Map<Integer, String> lockMap = new HashMap<>();
        lockMap.put(1, WellConstant.CLOSED);
        Map<String, Object> map = new HashMap<>();
        map.put(WellConstant.DOOR_MAP, outCoverMap);
        map.put(WellConstant.LOCK_MAP, lockMap);
        dataSource.put(WellConstant.DOOR_LOCK_STATE, map);
        //倾斜
        Map<String, Integer> lean = new HashMap<>();
        lean.put(WellConstant.TILT_STATE, 1);
        dataSource.put(ParamsKey.LEAN, lean);
        dataSource.put(WellConstant.VERSION_NUMBER, "HV3.0_SV1.0.0.3_DT6785.63448");
        abstractResOutputParams.setParams(dataSource);

        //主控信息
        ControlParam controlParam = new ControlParam();
        controlParam.setHardwareVersion("11");
        controlParam.setSoftwareVersion("22");
        Map configMap = new HashMap<>();
        configMap.put(ParamsKey.ELECTRICITY, "60");
        configMap.put(ParamsKey.HIGH_TEMPERATURE, "50");
        configMap.put(ParamsKey.LOW_TEMPERATURE, "-10");
        configMap.put(ParamsKey.HUMIDITY, "60");
        configMap.put(ParamsKey.HEART_BEAT_CYCLE, "1440");
        configMap.put(ParamsKey.WORK_TIME, "5");
        String s = JSON.toJSONString(configMap);
        controlParam.setConfigValue(s);

        //锁信息
        Lock lastLockInfo = new Lock();
        lastLockInfo.setDoorStatus(WellConstant.CLOSED);
        lastLockInfo.setLockStatus(WellConstant.OPEAN);
        abstractResOutputParams.setControlParam(controlParam);
        new Expectations() {
            {
                lockFeign.queryLockByDeviceIdAndDoorNum((Lock) any);
                result = lastLockInfo;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        //流水号重复分支
        new Expectations() {
            {
                commonUtil.filterRepeatCmd(anyString, anyInt);
                result = true;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        //主控null分支
        new Expectations() {
            {
                commonUtil.filterRepeatCmd(anyString, anyInt);
                result = false;
            }

        };
        try {
            fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ResponseException.class);
        }
        //设施为null分支
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = null;
            }
        };
        try {
            fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == ResponseException.class);
        }
        //更新设施状态、有开锁状态分支
        dataSource.put(WellConstant.CURRENT_ACTION, WellConstant.UNLOCKING_ACTION);
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        AreaInfo areaInfo = new AreaInfo();
        deviceInfoDto.setAreaInfo(areaInfo);
        controlParam.setDeviceStatus(DeviceStatus.Offline.getCode());
        controlParam.setDeployStatus("6");
        dataSource.put(WellConstant.SUCCESSFUL_CONFIGURATION, 1);
        dataSource.put(ParamsKey.DEPLOY_STATUS, "1");
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        //redisMap 不为null 分支
        Map<String, Object> redisMap = new HashMap<>();
        redisMap.put(ParamsKey.DEPLOY_STATUS, "1");
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = redisMap;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = 156195190545454L;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        Map<Object, Object> cmdMap = new HashMap<>();
        Map dataMap = new HashMap();
        dataMap.put(OceanParamsKey.DEVICE_ID, "1");
        dataMap.put(OceanParamsKey.APP_ID, "2");
        cmdMap.put(1, dataMap);
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = 1561951940000L;
            }

            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = cmdMap;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        //低温告警 之前有告警分支
        dataSource.put(ParamsKey.TEMPERATURE, -90.00);
        List<AlarmCurrent> alarmCurrents = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setAlarmCode("unLock");
        alarmCurrents.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDeviceIdCode(anyString);
                result = alarmCurrents;
            }
        };
        fiLinkWellNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
    }

}
