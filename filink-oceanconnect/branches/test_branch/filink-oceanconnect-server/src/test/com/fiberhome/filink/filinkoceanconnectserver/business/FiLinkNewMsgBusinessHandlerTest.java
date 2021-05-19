package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.commonstation.constant.*;
import com.fiberhome.filink.commonstation.entity.config.UpgradeConfig;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.deviceapi.util.DeployStatus;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.sender.FiLinkOceanConnectSender;
import com.fiberhome.filink.filinkoceanconnectserver.sender.SendUtil;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.commons.collections.map.HashedMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author:qiqizhu@wistronits.com Date:2019/4/15
 */
@RunWith(JMockit.class)
public class FiLinkNewMsgBusinessHandlerTest {

    @Tested
    private FiLinkNewMsgBusinessHandler fiLinkNewMsgBusinessHandler;

    @Injectable
    private FiLinkOceanConnectSender sender;

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
    private SendUtil sendUtil;

    @Injectable
    private ParameterFeign parameterFeign;
    @Injectable
    private CommonUtil commonUtil;

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

    @Mocked
    private RedisUtils redisUtils;

    @Mocked
    private DeviceUpgradeUtil DeviceUpgradeUtil;

    @Before
    public void before() throws Exception {
        maxUpgradeLen = 300;
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

        abstractResOutputParams.setCmdId(CmdId.HEART_BEAT);
        abstractResOutputParams.setEquipmentId("testId");
        abstractResOutputParams.setSerialNumber(1001);
        abstractResOutputParams.setCmdOk(CmdOk.FAIL);
        abstractResOutputParams.setCmdType("2");
        fiLinkNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        abstractResOutputParams.setCmdType("1");
        ControlParam controlParam = new ControlParam();
        controlParam.setDeviceId("testId");
        controlParam.setDeviceStatus(DeviceStatus.Unconfigured.getCode());
        controlParam.setActiveStatus(Constant.SLEEP_STATUS);
        abstractResOutputParams.setControlParam(controlParam);
        Map<Object, Object> cmdMap = new HashMap<>();
        Map dataMap = new HashMap();
        dataMap.put(OceanParamsKey.DEVICE_ID, "1");
        dataMap.put(OceanParamsKey.APP_ID, "2");
        cmdMap.put(1, dataMap);
        Map<String, Object> redisDataMap = new HashedMap();
        redisDataMap.put(ParamsKey.HEX_DATA, "FEX");
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = cmdMap;
            }

            {
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = redisDataMap;
            }
        };
        fiLinkNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
        abstractResOutputParams.setCmdId(CmdId.UNLOCK);
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> stringObjectMap = new HashedMap();
        stringObjectMap.put(ParamsKey.SLOT_NUM, "1");
        stringObjectMap.put(ParamsKey.RESULT, "1");
        paramList.add(stringObjectMap);
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.PARAMS_KEY, paramList);
        abstractResOutputParams.setParams(dataSource);
        fiLinkNewMsgBusinessHandler.handleMsg(abstractResOutputParams);
    }


    /**
     * Method: executeUnlockCmd(String equipmentId)
     */
    @Test
    public void testExecuteUnlockCmd() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("executeUnlockCmd", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: updateDeployStatus(String equipmentId, ControlParam control)
     */
    @Test
    public void testUpdateDeployStatus() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("updateDeployStatus", String.class, ControlParam.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: handleCmd(String cmdId, String deviceId, String equipmentId, Map<String, Object> dataSource, ControlParam control, DeviceLog deviceLog, Integer serialNumber)
     */
    @Test
    public void testHandleCmd() throws Exception {
        Map<String, Object> dataSource = new HashedMap();
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("handleCmd", String.class, String.class, String.class, Map.class, ControlParam.class, DeviceLog.class, Integer.class);
        method.setAccessible(true);
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.PARAMS_UPLOAD, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.ACTIVE, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.SET_CONFIG, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.SLEEP, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.OPEN_LOCK_UPLOAD, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.CLOSE_LOCK_UPLOAD, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.DOOR_STATE_CHANGE, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.FILE_ADVISE, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.FILE_UPLOAD, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.UPGRADE_SUCCESS, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.UPGRADE_ADVISE, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.UPGRADE_DATA, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.DEPLOY_STATUS, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, CmdId.HEART_BEAT, "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, "test", "test", "test", dataSource, new ControlParam(), new DeviceLog(), 1);
        } catch (Exception e) {
        }

    }

    /**
     * Method: updateDeviceStatusNormal(String equipmentId, ControlParam control, String deviceId)
     */
    @Test
    public void testUpdateDeviceStatusNormal() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveDeployStatus", String.class, Integer.class, Map.class);
        method.setAccessible(true);
        Map<String, Object> dataMap = new HashedMap();
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.DEPLOY_STATUS, DeployStatus.DEPLOYING.getCode());
        dataMap.put(ParamsKey.SERIAL_NUMBER, 1000);
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEPLOY_CMD, anyString);
                result = dataMap;
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", 1000, dataSource);
    }

    /**
     * Method: updateDeviceStatus(String equipmentId, String status)
     */
    @Test
    public void testUpdateDeviceStatus() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("updateDeviceStatus", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveHeartBeat()
     */
    @Test
    public void testResolveHeartBeat() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("resolveHeartBeat"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveDeployStatus(String equipmentId, Integer currentSerialNumber, Map<String, Object> dataSource)
     */
    @Test
    public void testResolveDeployStatus() throws Exception {

    }

    /**
     * Method: resolveData(String equipmentId, ControlParam controlParam, Integer serialNumber, Map<String, Object> dataSource)
     */
    @Test
    public void testResolveData() throws Exception {
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.DEPLOY_STATUS, DeployStatus.DEPLOYING.getCode());
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveData", String.class, ControlParam.class, Integer.class, Map.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", controlParam, 001, dataSource);
        Map upgradeMap = new HashedMap();
        upgradeMap.put(ParamsKey.PACKAGE_SUM, "30");
        upgradeMap.put(ParamsKey.FILE_KEY, "test");
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, anyString);
                result = upgradeMap;
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", controlParam, 001, dataSource);
        dataSource.put(ParamsKey.CONTINUE_PACKAGE_NUM, "255");
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", controlParam, 001, dataSource);
        upgradeMap.put(ParamsKey.PACKAGE_SUM, "300");
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", controlParam, 001, dataSource);
        controlParam.setSoftwareVersion("HVS1.5.SV1.05");
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, "testId", controlParam, 001, dataSource);
        } catch (Exception e) {
        }
    }

    /**
     * Method: getCurrentFileData(int packageNum, int packageSum, String fileKey, int maxUpgradeData, ControlParam controlParam)
     */
    @Test
    public void testGetCurrentFileData() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("getCurrentFileData", int.class, int.class, String.class, int.class, ControlParam.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getUpgradeFileFromFtp(String software)
     */
    @Test
    public void testGetUpgradeFileFromFtp() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("getUpgradeFileFromFtp", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getCrcData(String packageData)
     */
    @Test
    public void testGetCrcData() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("getCrcData", String.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "test");
    }

    /**
     * Method: resolveUpgradeSuccess(ControlParam control, Map<String, Object> dataSource, DeviceLog deviceLog, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveUpgradeSuccess() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveUpgradeSuccess", ControlParam.class, Map.class, DeviceLog.class, String.class, Integer.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.UPGRADE_RESULT, Constant.UNLOCK_SUCCESS);
        dataSource.put(ParamsKey.TIME, 1562136328);
        dataSource.put(ParamsKey.SOFTWARE_VERSION, "testVersion");
        dataSource.put(ParamsKey.HARDWARE_VERSION, "testVesion");
        DeviceLog deviceLog = new DeviceLog();
        method.invoke(fiLinkNewMsgBusinessHandler, controlParam, dataSource, deviceLog, "testId", 100);
    }

    /**
     * Method: deleteUpgradeDeviceCount(String equipmentId)
     */
    @Test
    public void testDeleteUpgradeDeviceCount() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("deleteUpgradeDeviceCount", String.class);
        method.setAccessible(true);
        new Expectations() {
            {
                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = new HashSet<>();
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, "test");
    }

    /**
     * Method: resolveFileUpload(ControlParam control, String deviceId, Map<String, Object> dataSource, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveFileUpload() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveFileUpload", ControlParam.class, String.class, Map.class, String.class, Integer.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.INDEX, "test");
        dataSource.put(ParamsKey.PACKAGE_SUM, "200");
        dataSource.put(ParamsKey.PACKAGE_NUM, "200");
        dataSource.put(ParamsKey.CHECK_NUM, "194");
        dataSource.put(ParamsKey.PACKAGE_DATA, "10025858");
        Map<String, String> adviseMap = new HashedMap();
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEVICE_ADVISE, anyString);
                result = adviseMap;
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, controlParam, "testId", dataSource, "testId", 001);
    }

    /**
     * Method: makeChecksum(String data)
     */
    @Test
    public void testMakeChecksum() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("makeChecksum", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: mergePic(String equipmentId, String index, int packageSum, Map<String, Object> picMap, String deviceId)
     */
    @Test
    public void testMergePic() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("mergePic", String.class, String.class, int.class, Map<String,.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveFileAdvise(String equipmentId, Map<String, Object> dataSource)
     */
    @Test
    public void testResolveFileAdvise() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveFileAdvise", String.class, Map.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.INDEX, "50");
        dataSource.put(ParamsKey.FILE_FORMAT, "test");
        dataSource.put(ParamsKey.DATA_SIZE, "300");
        dataSource.put(ParamsKey.DOOR_NUM, "1");
        dataSource.put(ParamsKey.FILE_TYPE, "exe");
        dataSource.put(ParamsKey.ALARM_CODE, "pruDoor");
        dataSource.put(ParamsKey.TIME, 1562136328);
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", dataSource);
    }

    /**
     * Method: resolveDoorStateChange(ControlParam controlParam, Map<String, Object> dataSource, DeviceLog deviceLog, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveDoorStateChange() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveDoorStateChange", ControlParam.class, Map.class, DeviceLog.class, String.class, Integer.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        Map<String, Object> dataSource = new HashedMap();
        DeviceLog deviceLog = new DeviceLog();
        dataSource.put(ParamsKey.TIME, 1562139975);
        dataSource.put(ParamsKey.DOOR_NUM, "1");
        dataSource.put(ParamsKey.DOOR_STATE, "1");
        new Expectations() {
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, controlParam, dataSource, deviceLog, "testId", 100);
    }

    /**
     * Method: resolveCloseLockUpload(ControlParam controlParam, Map<String, Object> dataSource, DeviceLog deviceLog, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveCloseLockUpload() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveCloseLockUpload", ControlParam.class, Map.class, DeviceLog.class, String.class, Integer.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        Map<String, Object> dataSource = new HashedMap();
        DeviceLog deviceLog = new DeviceLog();
        dataSource.put(ParamsKey.TIME, 1562139975);
        dataSource.put(ParamsKey.LOCK_NUM, "1");
        dataSource.put(ParamsKey.LOCK_STATE, "101");
        new Expectations() {
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, controlParam, dataSource, deviceLog, "testId", 100);
    }

    /**
     * Method: resolveUnlockResponse(Map<String, Object> dataSource, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveUnlockResponse() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("resolveUnlockResponse", Map<String,.class, String.class, Integer.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getUnlockResultString(String slotNum, String result)
     */
    @Test
    public void testGetUnlockResultString() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("getUnlockResultString", String.class, String.class);
        method.setAccessible(true);
        new Expectations() {
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.SUCCESS);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.KEY_NOT_CONFIG);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.PARAMETER_ERROR);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.LOCK_ON);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.ACTIVE_CODE_ERROR);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.LOCK_NOT_CONFIG);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.REPEAT_CMD);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.STOP);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", LockResult.OTHER);
        method.invoke(fiLinkNewMsgBusinessHandler, "001", "test");
    }

    /**
     * Method: resolveSleep(ControlParam control, Map<String, Object> dataSource, DeviceLog deviceLog, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveSleep() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveSleep", ControlParam.class, Map.class, DeviceLog.class, String.class, Integer.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        DeviceLog deviceLog = new DeviceLog();
        ControlParam control = new ControlParam();
        dataSource.put(ParamsKey.TIME, 1562139975);
        Map<String, String> dataMap = new HashedMap();
        dataMap.put("101", "101");
        dataMap.put("201", "101");
        dataSource.put(ParamsKey.DOOR_LOCK_STATE, dataMap);
        new Expectations() {
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, control, dataSource, deviceLog, "testId", 001);
    }

    /**
     * Method: resolveOpenLockUpload(ControlParam control, Map<String, Object> dataSource, DeviceLog deviceLog, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveOpenLockUpload() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveOpenLockUpload", ControlParam.class, Map.class, DeviceLog.class, String.class, Integer.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.LOCK_STATE,"1001");
        dataSource.put(ParamsKey.LOCK_NUM,"1");
        dataSource.put(ParamsKey.TIME,15230000);
        ControlParam controlParam = new ControlParam();
        new Expectations(){
            {
                systemLanguageUtil.getI18nString(anyString);
                result = "test";
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler,controlParam,dataSource,new DeviceLog(),"testId",100);

    }

    /**
     * Method: setRemarks(DeviceLog deviceLog, String lockNum, String msg)
     */
    @Test
    public void testSetRemarks() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("setRemarks", DeviceLog.class, String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveSetConfig(String equipmentId, Integer currentSerialNumber)
     */
    @Test
    public void testResolveSetConfig() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("resolveSetConfig", String.class, Integer.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: saveDeviceLog(DeviceLog deviceLog, String logType, String logName, String type)
     */
    @Test
    public void testSaveDeviceLog() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("saveDeviceLog", DeviceLog.class, String.class, String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: resolveActive(String equipmentId, ControlParam control, Map<String, Object> dataSource, DeviceLog deviceLog, Integer serialNumber)
     */
    @Test
    public void testResolveActive() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveActive", String.class, ControlParam.class, Map.class, DeviceLog.class, Integer.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashMap<>();
        dataSource.put(ParamsKey.SOFTWARE_VERSION,"test");
        dataSource.put(ParamsKey.HARDWARE_VERSION,"test");
        dataSource.put(ParamsKey.IMEI,"test");
        dataSource.put(ParamsKey.IMSI,"test");
        dataSource.put(ParamsKey.TIME,"15230000");
        dataSource.put(ParamsKey.REBOOT_REASON,"test");
        ControlParam controlParam = new ControlParam();
        Map map = new HashedMap();
        map.put("test", "test");
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> paramMap = new HashedMap();
        paramMap.put(ParamsKey.DATA_CLASS, ParamsKey.EMERGENCEY_LOCK);
        Map<String, String> dataMap = new HashedMap();
        dataMap.put("101", "101");
        dataMap.put("201", "101");
        paramMap.put(ParamsKey.DATA, dataMap);
        paramMap.put(ParamsKey.ALARM_FLAG, "1");
        paramList.add(paramMap);
        dataSource.put(ParamsKey.PARAMS_KEY, paramList);
        controlParam.setConfigValue(JSON.toJSONString(map));
        dataSource.put(ParamsKey.DOOR_LOCK_STATE, dataMap);
        method.invoke(fiLinkNewMsgBusinessHandler,"testId",controlParam,dataSource,new DeviceLog(),100);
    }

    /**
     * Method: isNeedUpgrade(String equipmentId, String rebootReason, ControlParam controlParam)
     */
    @Test
    public void testIsNeedUpgrade() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("isNeedUpgrade", String.class, String.class, ControlParam.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        controlParam.setHardwareVersion("test.test");
        controlParam.setSoftwareVersion("test.test");
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, "testId", RebootReason.START_SELF, controlParam);
        } catch (Exception e) {
        }
    }

    /**
     * Method: getPackageSum(String hexBinFile, int maxData)
     */
    @Test
    public void testGetPackageSum() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("getPackageSum", String.class, int.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "FECCSD", 2);
    }

    /**
     * Method: addUpgradeDeviceCount(String equipmentId)
     */
    @Test
    public void testAddUpgradeDeviceCount() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("addUpgradeDeviceCount", String.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "test");
        Set<String> deviceUpgradeSet = new HashSet<>();
        new Expectations() {
            {
                RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
                result = deviceUpgradeSet;
            }
        };
        method.invoke(fiLinkNewMsgBusinessHandler, "test");
    }

    /**
     * Method: checkUpgradeFile(ControlParam controlParam, UpgradeConfig upgradeConfig)
     */
    @Test
    public void testCheckUpgradeFile() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("checkUpgradeFile", ControlParam.class, UpgradeConfig.class);
        method.setAccessible(true);
        ControlParam controlParam = new ControlParam();
        controlParam.setSoftwareVersion("test.test");
        controlParam.setHardwareVersion("test.test");
        UpgradeConfig upgradeConfig = new UpgradeConfig();
        try {
            method.invoke(fiLinkNewMsgBusinessHandler, controlParam, upgradeConfig);
        } catch (Exception e) {
        }
    }

    /**
     * Method: copyUpgradeConfig(UpgradeConfig upgradeConfig, UpgradeConfig upgradeConfigTmp)
     */
    @Test
    public void testCopyUpgradeConfig() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("copyUpgradeConfig", UpgradeConfig.class, UpgradeConfig.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, new UpgradeConfig(), new UpgradeConfig());
    }

    /**
     * Method: sendUpgradeAdvise(String equipmentId, int packageSum, ControlParam controlParam, String check)
     */
    @Test
    public void testSendUpgradeAdvise() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("sendUpgradeAdvise", String.class, int.class, ControlParam.class, String.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", 5, new ControlParam(), "test");
    }

    /**
     * Method: resolveParamUpload(String deviceId, ControlParam control, Map<String, Object> dataSource, String equipmentId, Integer serialNumber)
     */
    @Test
    public void testResolveParamUpload() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveParamUpload", String.class, ControlParam.class, Map.class, String.class, Integer.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        dataSource.put(ParamsKey.TIME, 152300);
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> paramMap = new HashedMap();
        paramMap.put(ParamsKey.DATA_CLASS, ParamsKey.EMERGENCEY_LOCK);
        Map<String, String> dataMap = new HashedMap();
        dataMap.put("101", "101");
        dataMap.put("201", "101");
        paramMap.put(ParamsKey.DATA, dataMap);
        paramMap.put(ParamsKey.ALARM_FLAG, "1");
        paramList.add(paramMap);
        dataSource.put(ParamsKey.PARAMS_KEY, paramList);
        ControlParam control = new ControlParam();
        Map map = new HashedMap();
        map.put("test", "test");
        control.setActualValue(JSON.toJSONString(map));
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", control, dataSource, "testId", 001);
    }

    /**
     * Method: resolveAlarmValue(List<Map<String, Object>> paramList, String equipmentId, Long time, Map<String, Object> result)
     */
    @Test
    public void testResolveAlarmValue() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("resolveAlarmValue", List.class, String.class, Long.class, Map.class);
        method.setAccessible(true);
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> paramMap = new HashedMap();
        paramMap.put(ParamsKey.DATA_CLASS, ParamsKey.EMERGENCEY_LOCK);
        Map<String, String> dataMap = new HashedMap();
        dataMap.put("101", "101");
        dataMap.put("201", "101");
        paramMap.put(ParamsKey.DATA, dataMap);
        paramMap.put(ParamsKey.ALARM_FLAG, "1");
        paramList.add(paramMap);
        Map<String, Object> result = new HashedMap();
        method.invoke(fiLinkNewMsgBusinessHandler, paramList, "testId", 152360000L, result);
        paramMap.put(ParamsKey.DATA_CLASS, ParamsKey.LOW_TEMPERATURE);
        method.invoke(fiLinkNewMsgBusinessHandler, paramList, "testId", 152360000L, result);
        paramMap.put(ParamsKey.DATA_CLASS, "aa");
        method.invoke(fiLinkNewMsgBusinessHandler, paramList, "testId", 152360000L, result);
    }

    /**
     * Method: getDataMap(Object data, String alarmFlag)
     */
    @Test
    public void testGetDataMap() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("getDataMap", Object.class, String.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "1", "test");
    }

    /**
     * Method: isNeedToResolve(String equipmentId, String cmdId, long currentTime)
     */
    @Test
    public void testIsNeedToResolve() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("isNeedToResolve", String.class, String.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: batchSaveLockStatus(Object data, String equipmentId, long updateTime)
     */
    @Test
    public void testBatchSaveLockStatus() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("batchSaveLockStatus", Object.class, String.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setLockInfo(String equipmentId, String doorNum, String status, long updateTime)
     */
    @Test
    public void testSetLockInfo() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("setLockInfo", String.class, String.class, String.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setDoorInfo(String equipmentId, String doorNum, String doorState, long updateTime)
     */
    @Test
    public void testSetDoorInfo() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("setDoorInfo", String.class, String.class, String.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: setDoorLockInfo(String equipmentId, String doorNum, String status, long updateTime)
     */
    @Test
    public void testSetDoorLockInfo() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("setDoorLockInfo", String.class, String.class, String.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: updateSleepControlInfo(ControlParam control, Map<String, Object> dataSource)
     */
    @Test
    public void testUpdateSleepControlInfo() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("updateSleepControlInfo", ControlParam.class, Map<String,.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: updateActiveControlInfo(ControlParam control, Map<String, Object> dataSource)
     */
    @Test
    public void testUpdateActiveControlInfo() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("updateActiveControlInfo", ControlParam.class, Map.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        ControlParam control = new ControlParam();
        Map map = new HashedMap();
        map.put("test", "test");
        control.setActualValue(JSON.toJSONString(map));
        method.invoke(fiLinkNewMsgBusinessHandler, control, dataSource);
    }

    /**
     * Method: putActiveResultMap(Map<String, Object> result, Map<String, Object> dataSource, String key)
     */
    @Test
    public void testPutActiveResultMap() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("putActiveResultMap", Map<String,.class, Map<String,.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: updateDoorLockState(String equipmentId, Map<String, Object> dataSource, long updateTime)
     */
    @Test
    public void testUpdateDoorLockState() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("updateDoorLockState", String.class, Map<String,.class, long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: sendNullDataResponse(ControlParam control, String cmdId, Integer serialNumber)
     */
    @Test
    public void testSendNullDataResponse() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("sendNullDataResponse", ControlParam.class, String.class, Integer.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: sendResponse(ControlParam control, String cmdId, Map<String, Object> params, Integer serialNumber)
     */
    @Test
    public void testSendResponse() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = FiLinkNewMsgBusinessHandler.getClass().getMethod("sendResponse", ControlParam.class, String.class, Map<String,.class, Integer.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: sendRequest(String equipmentId, String cmdId, Map<String, Object> params, ControlParam controlParam)
     */
    @Test
    public void testSendRequest() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("sendRequest", String.class, String.class, Map.class, ControlParam.class);
        method.setAccessible(true);
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", "test", new HashMap<>(), new ControlParam());
    }

    /**
     * Method: sendActiveResponse(String equipmentId, ControlParam control, Integer serialNumber)
     */
    @Test
    public void testSendActiveResponse() throws Exception {
        Method method = fiLinkNewMsgBusinessHandler.getClass().getDeclaredMethod("sendActiveResponse", String.class, ControlParam.class, Integer.class);
        method.setAccessible(true);
        Map<String, Object> dataSource = new HashedMap();
        ControlParam control = new ControlParam();
        Map map = new HashedMap();
        map.put("test", "test");
        control.setConfigValue(JSON.toJSONString(map));
        method.invoke(fiLinkNewMsgBusinessHandler, "testId", control, 001);
    }

} 
