package com.fiberhome.filink.stationserver.business;


import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.DeviceLogNameI18n;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.UnlockPushBean;
import com.fiberhome.filink.commonstation.entity.config.UpgradeConfig;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.commonstation.utils.AliYunPushUtil;
import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.constant.StationI18n;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResOutputParams;
import com.fiberhome.filink.stationserver.sender.UdpChannel;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

/**
 * businessHandler测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkNewMsgBusinessHandlerTest {

    @Tested
    private FiLinkNewMsgBusinessHandler businessHandler;

    @Injectable
    private AbstractInstructSender sender;

    @Injectable
    private LockFeign lockFeign;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private DeviceLogFeign deviceLogFeign;

    @Injectable
    private FiLinkKafkaSender streamSender;

    @Injectable
    private UdpChannel sendUtil;

    @Injectable
    private AliYunPushUtil pushUtil;

    @Injectable
    private ParameterFeign parameterFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private String ftpFilePath;

    @Injectable
    private String tmpDirPath;

    @Injectable
    private String zipPassword;

    @Injectable
    private Integer maxUpgradeLen;

    @Injectable
    private Integer maxUpgradeCount;

    private Map<String, Object> dataSource;

    @Before
    public void before() {
        ftpFilePath = "/test/ftp/";
        tmpDirPath = "C:\\test\\";
        zipPassword = "FiLink";
        List<Map<String, Object>> mapList = new ArrayList<>();
        dataSource = new HashMap<>();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("1", "12");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(ParamsKey.SLOT_NUM, String.valueOf(i + 1));
            paramMap.put(ParamsKey.RESULT, String.valueOf(i));
            String dataclass;
            if (i < 3) {
                dataclass = ParamsKey.EMERGENCEY_LOCK;
            } else if (i < 6) {
                dataclass = ParamsKey.HIGH_TEMPERATURE;
            } else {
                dataclass = "ssss";
            }
            paramMap.put(ParamsKey.DATA_CLASS, dataclass);
            paramMap.put(ParamsKey.DATA, dataMap);
            paramMap.put(ParamsKey.ALARM_FLAG, "2");
            mapList.add(paramMap);
        }
        dataSource.put(ParamsKey.PARAMS_KEY, mapList);
        dataSource.put(ParamsKey.TIME, "435646387");
        dataSource.put(ParamsKey.IMSI, "435646387");
        dataSource.put(ParamsKey.IMEI, "435646387");
        dataSource.put(ParamsKey.REBOOT_REASON, "0");
        dataSource.put(ParamsKey.MODULE_TYPE, "0");
        dataSource.put(ParamsKey.ELECTRICITY, "0");
        dataSource.put(ParamsKey.TEMPERATURE, "0");
        dataSource.put(ParamsKey.HUMIDITY, "0");
        dataSource.put(ParamsKey.LEAN, "0");
        dataSource.put(ParamsKey.LEACH, "0");
        dataSource.put(ParamsKey.WIRELESS_MODULE_SIGNAL, "0");
        dataSource.put(ParamsKey.SUPPLY_ELECTRICITY_WAY, "0");
        dataSource.put(ParamsKey.OPERATOR, "0");
        dataSource.put(ParamsKey.SOFTWARE_VERSION, "RP9003.004B.bin");
        dataSource.put(ParamsKey.HARDWARE_VERSION, "NRF52840_elock");
        dataSource.put(ParamsKey.DOOR_LOCK_STATE, dataMap);
        dataSource.put(ParamsKey.LOCK_STATE, "22");
        dataSource.put(ParamsKey.DOOR_STATE, "1");
        dataSource.put(ParamsKey.LOCK_NUM, "1");
        dataSource.put(ParamsKey.DOOR_NUM, "1");
        dataSource.put(ParamsKey.INDEX, "1");
        dataSource.put(ParamsKey.FILE_FORMAT, "jpg");
        dataSource.put(ParamsKey.DATA_SIZE, "200");
        dataSource.put(ParamsKey.FILE_TYPE, "1");
        dataSource.put(ParamsKey.PACKAGE_SUM, "5");
        dataSource.put(ParamsKey.ALARM_CODE, "noClose");
        dataSource.put(ParamsKey.PACKAGE_NUM, "5");
        dataSource.put(ParamsKey.CHECK_NUM, "164");
        dataSource.put(ParamsKey.PACKAGE_DATA, "");
        dataSource.put(ParamsKey.UPGRADE_RESULT, "0");
        dataSource.put(ParamsKey.UPGRADE_TYPE, "0");
        dataSource.put(ParamsKey.DEPLOY_STATUS, "0");
        dataSource.put(ParamsKey.CONTINUE_PACKAGE_NUM, "");
        maxUpgradeLen = 3;
        maxUpgradeCount = 2;
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgUnlockTest() {
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2201");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(65);
        resOutputParams.setCmdType("2");
        List<Integer> serialNumList = new ArrayList<>();
        serialNumList.add(resOutputParams.getSerialNumber());
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, any);

                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = serialNumList;

                systemLanguageUtil.getI18nString(anyString);
                result = "test";

                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        resOutputParams.setCmdOk(1);
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        resOutputParams.setControlParam(control);
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        resOutputParams.getControlParam().setConfigValue("{}");
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        resOutputParams.getControlParam().setConfigValue("{\"restartTime\":54656464}");
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        resOutputParams.getControlParam().setConfigValue("{\"restartTime\":54656464,\"heartbeatCycle\":6545445}");
        businessHandler.handleMsg(resOutputParams);
        serialNumList.clear();
        serialNumList.add(5);
        resOutputParams.getControlParam().setHostName("zhu");
        resOutputParams.setParams(dataSource);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        UnlockPushBean pushBean = new UnlockPushBean();
        pushBean.setPhoneId("165564565856");
        AliAccessKey aliAccessKey = new AliAccessKey();
        aliAccessKey.setAccessKeyId("ajhdfjkahwkdjhkuawh");
        aliAccessKey.setAccessKeySecret("sajkhdikuwhahaikhdikha");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, anyString);
                result = pushBean;

                RedisUtils.get(RedisKey.ALI_PUSH_KEY);
                result = null;

                parameterFeign.queryMobilePush();
                result = aliAccessKey;

                RedisUtils.set(RedisKey.ALI_PUSH_KEY, any);
            }
        };
        businessHandler.handleMsg(resOutputParams);
        new Expectations() {
            {
                parameterFeign.queryMobilePush();
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        pushBean.setPhoneId("");
        businessHandler.handleMsg(resOutputParams);
        pushBean.setToken("asjhdwihakh");
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgParamUploadTest() {
        String key = "FH444JH55IL" + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put(ParamsKey.HEX_DATA, "FEEEEEEEEEEEEEEEEEEEEEEEE");
        dataMap.put(ParamsKey.IMEI, "3564534564");
        Map<Object, Object> cmdMap = new HashMap<>(16);
        cmdMap.put("1", dataMap);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = dataMap;

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = true;

                RedisUtils.hGetMap(key);
                result = cmdMap;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2208");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":54656464,\"heartbeatCycle\":6545445}");
        control.setHostName("zhu");
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        resOutputParams.setParams(dataSource);
        businessHandler.handleMsg(resOutputParams);
        Long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        control.setActualValue("{\"temperature\":{\"data\":{\"1\":\"12\"},\"alarmFlag\":\"2\"},\"ssss\":{\"data\":{\"$ref\":\"$.temperature.data\"},\"alarmFlag\":\"2\"}}");
        control.setCurrentTime(time);
        new Expectations() {
            {
                controlFeign.updateControlParam((ControlParam) any);
                result = new RuntimeException();
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgActiveTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2204");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        new Expectations(RedisUtils.class) {
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";

                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";
            }
        };
        businessHandler.handleMsg(resOutputParams);
        Set<String> deviceUpgradeSet = getSetString();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "43564638700";

                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = deviceUpgradeSet;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        resOutputParams.getControlParam().setActualValue("{\"shake\":{\"data\":\"0\",\"alarmFlag\":\"2\"},\"lean\":{\"data\":\"24\",\"alarmFlag\":\"1\"}," +
                "\"moduleType\":{\"data\":\"1\",\"alarmFlag\":\"1\"},\"constructedState\":\"0\",\"temperature\":{\"data\":\"0\",\"alarmFlag\":\"2\"},\"humidity\":{\"data\":\"0\",\"alarmFlag\":\"1\"},\"electricity\":{\"data\":\"99\"," +
                "\"alarmFlag\":\"1\"},\"supplyElectricityWay\":{\"data\":\"0\",\"alarmFlag\":\"1\"},\"leach\":{\"data\":\"2\",\"alarmFlag\":\"1\"},\"operator\":{\"data\":\"0\"," +
                "\"alarmFlag\":\"1\"},\"wirelessModuleSignal\":{\"data\":\"8\",\"alarmFlag\":\"1\"}}");
        UpgradeConfig upgradeConfigTmp = new UpgradeConfig();
        upgradeConfigTmp.setSoftwareVersion("RP9003.004C.bin");
        upgradeConfigTmp.setDependentHardVersion("NRF52840_elock");
        upgradeConfigTmp.setDependentSoftVersion("RP9003.004B.bin");
        upgradeConfigTmp.setHexBinFile("3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E6");
        Set<String> equipmentIdList = new HashSet<>();
        equipmentIdList.add("FH444JH55IL");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;

                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = null;

                RedisUtils.get("upgradeFilePrefixRP9003");
                result = upgradeConfigTmp;

                RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, "FH444JH55IL");
                result = null;

                RedisUtils.set(RedisKey.UPGRADE_DEVICE_COUNT, equipmentIdList);
            }
        };
        businessHandler.handleMsg(resOutputParams);
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress("39.98.72.132");
        ftpSettings.setInnerIpAddress("39.98.72.132");
        ftpSettings.setPort(2341);
        ftpSettings.setUserName("filink");
        ftpSettings.setPassword("filink");
        ftpSettings.setDisconnectTime(2);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get("upgradeFilePrefixRP9003");
                result = null;


                parameterFeign.queryFtpSettings();
                result = null;
            }
        };
        UpgradeConfig upgradeConfig = new UpgradeConfig();
        new Expectations() {
            {
                Deencapsulation.invoke(DeviceUpgradeUtil.class, "setUpgradeConfig", ftpSettings, zipPassword, ftpFilePath, tmpDirPath, upgradeConfig);
            }
        };
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {

        }
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = new HashSet<>();
            }
        };
        new Expectations() {
            {
                Deencapsulation.invoke(DeviceUpgradeUtil.class, "setUpgradeConfig", ftpSettings, zipPassword, ftpFilePath, tmpDirPath, upgradeConfig);
            }
        };
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {
        }
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgSetConfigTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2207");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_RESEND_BUFFER, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(ParamsKey.SERIAL_NUMBER, 0);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_RESEND_BUFFER, anyString);
                result = dataMap;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        dataMap.put(ParamsKey.SERIAL_NUMBER, 1000);
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgSleepTest() {
        String adviseKey = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + "FH444JH55IL";
        Map<String, Map<String, String>> adviseMap = new HashMap<>(16);
        Map<String, String> adviseMapBase = new HashMap<>(16);
        adviseMap.put("1", adviseMapBase);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hSet(anyString, anyString, any);
                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = null;
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2205");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        resOutputParams.getControlParam().setActualValue("{\"leach\":{\"alarmFlag\":\"1\"},\"temperature\":{\"data\":\"25\"," +
                "\"alarmFlag\":\"1\"},\"humidity\":{\"data\":\"50\",\"alarmFlag\":\"1\"},\"wirelessModuleSignal\":{\"data\":\"20\"" +
                ",\"alarmFlag\":\"1\"},\"electricity\":{\"data\":\"100\",\"alarmFlag\":\"1\"}}");
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgOpenLockTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                systemLanguageUtil.getI18nString(StationI18n.OPEN_LOCK_MSG);
                result = "锁${num}";

                systemLanguageUtil.getI18nString(DeviceLogNameI18n.OPEN_LOCK_EVENT);
                result = "test";

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x3201");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgCloseLockTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;
                systemLanguageUtil.getI18nString(StationI18n.CLOSE_LOCK_MSG);
                result = "锁${num}";

                systemLanguageUtil.getI18nString(DeviceLogNameI18n.CLOSE_LOCK_EVENT);
                result = "test";

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x3202");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgDoorStateChangeTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;
                systemLanguageUtil.getI18nString(StationI18n.OPEN_DOOR_MSG);
                result = "门开${num}";
                systemLanguageUtil.getI18nString(StationI18n.CLOSE_DOOR_MSG);
                result = "门关${num}";
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.DOOR_STATE_CHANGE_EVENT);
                result = "test";

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x3204");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        dataSource.put(ParamsKey.DOOR_STATE, "2");
        resOutputParams.setParams(dataSource);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgFileAdviseTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2202");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgFileUploadTest() {
        String key = RedisKey.PIC_UPLOAD + Constant.SEPARATOR + "FH444JH55IL" + Constant.SEPARATOR + "1";
        String adviseKey = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + "FH444JH55IL";
        Map<String, Object> picMap = new HashMap<>(64);
        picMap.put("2", "dhgh");
        picMap.put("3", "dhgh");
        picMap.put("4", "dhgh");
        picMap.put("5", "dhgh");
        picMap.put("6", "dhgh");
        picMap.put("7", "dhgh");
        picMap.put("8", "dhgh");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGetMap(key);
                result = picMap;

                RedisUtils.hGet(adviseKey, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;


                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2203");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        try {
            businessHandler.handleMsg(resOutputParams);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        dataSource.put(ParamsKey.PACKAGE_DATA, "FEEEEEEEEEEE");
        resOutputParams.setParams(dataSource);
        picMap.put("2", "");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.hGet(adviseKey, anyString);
                result = new HashMap<>(64);
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgUpgradeSuccessTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);

                systemLanguageUtil.getI18nString(anyString);
                result = "test";

                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = "4356463870000";

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x3203");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        dataSource.put(ParamsKey.UPGRADE_RESULT, "1");
        dataSource.put(ParamsKey.UPGRADE_TYPE, "1");
        resOutputParams.setParams(dataSource);
        businessHandler.handleMsg(resOutputParams);
        dataSource.put(ParamsKey.UPGRADE_RESULT, "1");
        dataSource.put(ParamsKey.UPGRADE_TYPE, "2");
        resOutputParams.setParams(dataSource);
        businessHandler.handleMsg(resOutputParams);
        dataSource.put(ParamsKey.UPGRADE_RESULT, "1");
        dataSource.put(ParamsKey.UPGRADE_TYPE, "4");
        resOutputParams.setParams(dataSource);
        businessHandler.handleMsg(resOutputParams);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.CMD_TIME, anyString);
                result = null;
            }
        };
        dataSource.put(ParamsKey.UPGRADE_RESULT, "0");
        dataSource.put(ParamsKey.UPGRADE_TYPE, "3");
        resOutputParams.setParams(dataSource);
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgResolveDataTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x220a");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        Map<String, Object> upgradeMap = new HashMap<>(64);
        upgradeMap.put(ParamsKey.PACKAGE_SUM, "2");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, anyString);
                result = upgradeMap;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        dataSource.put(ParamsKey.CONTINUE_PACKAGE_NUM, "3");
        Set<String> deviceUpgradeSet = new HashSet<>();
        deviceUpgradeSet.add("FH444JH55IL");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(RedisKey.UPGRADE_DEVICE_COUNT, any);

                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = deviceUpgradeSet;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        upgradeMap.put(ParamsKey.PACKAGE_SUM, "4");
        upgradeMap.put(ParamsKey.FILE_KEY, "276");
        UpgradeConfig upgradeConfig = new UpgradeConfig();
        upgradeConfig.setHexBinFile("FGFYHHIGJJHTTDFYUIYHGTYYFGHJHT");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.UPGRADE_FILE_PREFIX + "276");
                result = null;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(RedisKey.UPGRADE_FILE_PREFIX + "276");
                result = upgradeConfig;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        upgradeMap.put(ParamsKey.PACKAGE_SUM, "3");
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgResolveData2Test() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;

                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.hSet(anyString, anyString, any);

                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;

                RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x220b");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgDeployStatusTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, any);
                RedisUtils.set(anyString, any, anyLong);
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x220c");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
        Map<String, Object> dataMapStatus = new HashMap<>();
        dataMapStatus.put(ParamsKey.SERIAL_NUMBER, 1);
        dataMapStatus.put(ParamsKey.HEX_DATA, "GUYHGHJ");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = dataMapStatus;
            }
        };
        businessHandler.handleMsg(resOutputParams);
        dataMapStatus.put(ParamsKey.SERIAL_NUMBER, 0);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);
            }
        };
        businessHandler.handleMsg(resOutputParams);
    }

    /**
     * handleMsg
     */
    @Test
    public void handleMsgHeartBeatTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(anyString);
                result = false;

                RedisUtils.hSet(anyString, anyString, any);
                RedisUtils.set(anyString, any, anyLong);

                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x2206");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
    }

    @Test
    public void handleMsgTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hSet(anyString, anyString, any);
                RedisUtils.set(anyString, any, anyLong);
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, anyString);
                result = null;
                RedisUtils.hasKey(anyString);
                result = false;
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = null;
            }
        };
        AbstractResOutputParams resOutputParams = new FiLinkResOutputParams();
        resOutputParams.setCmdId("0x220XX");
        resOutputParams.setEquipmentId("FH444JH55IL");
        resOutputParams.setSerialNumber(0);
        resOutputParams.setCmdOk(1);
        resOutputParams.setCmdType("2");
        ControlParam control = new ControlParam();
        control.setDeviceId("545454545");
        control.setActiveStatus(Constant.SLEEP_STATUS);
        control.setHostId("2545245544");
        control.setProductId("2333232");
        control.setImei("35645245252");
        control.setConfigValue("{\"restartTime\":\"54656464\",\"heartbeatCycle\":\"6545445\",\"humidity\":\"\"}");
        control.setHostName("zhu");
        resOutputParams.setParams(dataSource);
        control.setSoftwareVersion("RP9003.004B.bin");
        control.setHardwareVersion("NRF52840_elock");
        control.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        resOutputParams.setControlParam(control);
        businessHandler.handleMsg(resOutputParams);
    }

    private Set<String> getSetString() {
        return new Set<String>() {
            @Override
            public int size() {
                return 400;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
    }

}
