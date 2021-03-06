package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.constant.*;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.utils.AlarmUtil;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.deviceapi.util.DeployStatus;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.sender.FiLinkOceanConnectWellSender;
import com.fiberhome.filink.filinkoceanconnectserver.sender.SendUtil;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectI18n;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectResultCode;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * filink?????????????????????
 *
 * @author CongcaiYu
 */
@Log4j
@Component("fiLinkWellNewBusinessHandler")
public class FiLinkWellNewMsgBusinessHandler implements MsgBusinessHandler {

    @Autowired
    private FiLinkOceanConnectWellSender sender;

    @Autowired
    private LockFeign lockFeign;

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private DeviceLogFeign deviceLogFeign;

    @Autowired
    private FiLinkKafkaSender streamSender;

    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private SendUtil sendUtil;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;


    /**
     * filink??????????????????
     *
     * @param abstractResOutputParams AbstractResOutputParams
     */
    @Override
    public void handleMsg(AbstractResOutputParams abstractResOutputParams) {
        FiLinkOceanResOutputParams outputParams = (FiLinkOceanResOutputParams) abstractResOutputParams;
        //??????id
        String cmdId = outputParams.getCmdId();
        String equipmentId = outputParams.getEquipmentId();
        Integer serialNumber = outputParams.getSerialNumber();
        //??????????????????????????????
        if (commonUtil.filterRepeatCmd(equipmentId, serialNumber)) {
            log.info("the equipmentId : " + equipmentId + " , cmd : " + cmdId + " is repeat>>>>>>>>>>>>");
            return;
        }
        //???????????????????????????
        ControlParam control = abstractResOutputParams.getControlParam();
        if (control == null) {
            throw new ResponseException("control info is null>>>>>>>>>>>>>");
        }
        String deviceId = control.getDeviceId();
        //????????????????????????
        commonUtil.refreshRedisTime(equipmentId, control);
        //???????????????????????????
        updateDeviceStatusNormal(equipmentId, control, deviceId);
        Map<String, Object> dataSource = outputParams.getParams();
        DeviceLog deviceLog = commonUtil.setDeviceLogCommonInfo(control);
        //????????????????????????????????????
        handleCmd(cmdId, deviceId, equipmentId, dataSource, control, deviceLog);
        //????????????????????????????????????,????????????????????????????????????
        updateDeployStatus(equipmentId, control);
        //?????????????????????????????? ??????????????????
        executeUnlockSetConfCmd(equipmentId);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param equipmentId ??????id
     * @param control     ????????????
     */
    private void updateDeployStatus(String equipmentId, ControlParam control) {
        //?????????????????????????????????????????????
        Map<String, Object> dataMap = (Map<String, Object>) RedisUtils.hGet(RedisKey.DEPLOY_CMD, equipmentId);
        if (StringUtils.isEmpty(dataMap)) {
            log.info("deploy status cmd redis is null : " + equipmentId);
        } else {
            resendCmd(dataMap);
        }
        Map<String, Object> configDataMap = (Map<String, Object>) RedisUtils.hGet(RedisKey.SET_CONFIG_CMD, equipmentId);
        if (configDataMap != null && configDataMap.size() != 0) {
            resendCmd(configDataMap);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param cmdId       ??????id
     * @param deviceId    ??????id
     * @param equipmentId ??????id
     * @param dataSource  ?????????
     * @param control     ????????????
     * @param deviceLog   ????????????
     */
    private void handleCmd(String cmdId, String deviceId, String equipmentId, Map<String, Object> dataSource,
                           ControlParam control, DeviceLog deviceLog) {
        switch (cmdId) {
            //????????????
            case CmdId.WEII_HEART_BEAT:
                resolveWellHeartBeat(deviceId, control, dataSource, equipmentId, deviceLog);
                break;
            //todo ????????????
//            case "0x2b5a":
//                resolveWellUpgrade(deviceId, control, dataSource, equipmentId);
//                break;
            //todo ???????????????????????????
//            case "0x285a":
//                resolveWellFirmwareInfo(dataSource,control);
//                break;
            default:
                log.info("cmd id: " + cmdId + " is not support>>>>>>>>>>>>>>");
        }
    }

    /**
     * ?????????????????????
     * todo ???????????? ????????????
     *
     * @param control    ????????????
     * @param dataSource ??????????????????
     */
//    private void resolveWellFirmwareInfo(Map<String, Object> dataSource, ControlParam control) {
//        //?????????
//        String versionNumber = (String) dataSource.get("versionNumber");
//        //todo ???????????????
//        File file = new File("test");
//        int length = (int) file.length();
//        byte[] bytes = new byte[length];
//        try {
//            FileInputStream inputStream = new FileInputStream(file);
//            inputStream.read(bytes);
//            Map<String, Object> params = new HashMap<>(3);
//            params.put("versionNumber", file.getName());
//            params.put("upgradePackageSize", length);
//            params.put("checkValue", WellDataUtil.getCRC(bytes));
//            params.put("upgradeSign", 0);
//            sendResponse(control, "0x285a", params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * ????????????
     * todo ???????????? ????????????
     *
     * @param deviceId    ??????id
     * @param control     ????????????
     * @param dataSource  ??????????????????
     * @param equipmentId ??????/??????id
    //     */
//    private void resolveWellUpgrade(String deviceId, ControlParam control, Map<String, Object> dataSource,
//                                    String equipmentId) {
//        //????????????????????????
//        if (isNeedToResolve(equipmentId, CmdId.PARAMS_UPLOAD)) {
//            log.info("the control : " + equipmentId + " cmd : " + CmdId.PARAMS_UPLOAD + " is not the newest>>>>>>>>");
//            return;
//        }
//        String upgradePackageName = String.valueOf(dataSource.get("upgradePackageName"));
//        int readDataOffsetAddress = Integer.parseInt((String) dataSource.get("readDataOffsetAddress"));
//        int readDataLength = Integer.parseInt((String) dataSource.get("readDataLength"));
//        Map<String, Object> params = new HashMap<>(3);
//        params.put("upgradePackageName", upgradePackageName);
//        params.put("readDataOffsetAddress", readDataOffsetAddress);
//        params.put("readDataLength", readDataLength);
//        //todo ???????????? ????????????
//        File file = new File("test");
//        int length = (int) file.length();
//        byte[] bytes = new byte[length];
//        try {
//            FileInputStream inputStream = new FileInputStream(file);
//            inputStream.read(bytes);
//            byte[] destBytes = new byte[readDataLength];
//            System.arraycopy(bytes, readDataOffsetAddress, destBytes, 0, readDataLength);
//            params.put("data", destBytes);
//            params.put("checkValue", WellDataUtil.crc16Calc(destBytes));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sendResponse(control, "0x2b5a", params);
//    }

    /**
     * ????????????
     *todo ???????????????  ????????????
     * @param control ????????????
     * @param cmdId   ??????id
     * @param params  ????????????
     */
//    private void sendResponse(ControlParam control, String cmdId, Map<String, Object> params) {
//        sender.sendInstruct(commonUtil.getResponseParams(control, cmdId, params, null));
//    }


    /**
     * ??????????????????????????????
     *
     * @param equipmentId ??????id
     * @param control     ????????????
     * @param deviceId    ??????id
     */
    private void updateDeviceStatusNormal(String equipmentId, ControlParam control, String deviceId) {
        String normalCode = DeviceStatus.Normal.getCode();
        String offlineCode = DeviceStatus.Offline.getCode();
        String outContactCode = DeviceStatus.Out_Contact.getCode();
        String unConfiguredCode = DeviceStatus.Unconfigured.getCode();
        String deviceStatus = control.getDeviceStatus();
        if (StringUtils.isEmpty(deviceStatus)) {
            log.error("device status is null : " + equipmentId);
            return;
        }
        if (offlineCode.equals(deviceStatus)
                || outContactCode.equals(deviceStatus)
                || unConfiguredCode.equals(deviceStatus)) {
            log.info("recover communicate alarm>>>>>>>>>>>>>>>>>");
            //??????????????????????????????
            control.setDeviceStatus(normalCode);
            Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.CANCEL_ALARM);
            streamSender.sendAlarm(alarmMap);
            updateDeviceStatus(control);
        }
    }

    /**
     * ??????????????????
     *
     * @param controlParam ????????????
     */
    private void updateDeviceStatus(ControlParam controlParam) {
        Result result = controlFeign.updateControlStatusById(controlParam);
        if(result != null && result.getCode() == 0){
            //??????redis
            RedisUtils.hSet(RedisKey.CONTROL_INFO, controlParam.getHostId(), controlParam);
        }
    }


    /**
     * ??????????????????
     *
     * @param deviceId    ??????id
     * @param control     ????????????
     * @param dataSource  ??????????????????
     * @param equipmentId ??????/??????id
     * @param deviceLog   ??????????????????
     */
    private void resolveWellHeartBeat(String deviceId, ControlParam control, Map<String, Object> dataSource,
                                      String equipmentId, DeviceLog deviceLog) {
        String deployStatus = (String) dataSource.get(ParamsKey.DEPLOY_STATUS);
        //?????????????????????
        String currentAction = String.valueOf(dataSource.get(WellConstant.CURRENT_ACTION));
        if (WellConstant.UNLOCKING_ACTION.equals(currentAction)) {
            resolveUnlockResponse(equipmentId);
        }
        //?????????????????? ????????????????????????
        String successfulConfiguration = String.valueOf(dataSource.get(WellConstant.SUCCESSFUL_CONFIGURATION));
        if (!WellConstant.NORMAL_STATUS.equals(successfulConfiguration)) {
            if (DeployStatus.DEPLOYING.getCode().equals(control.getDeployStatus())) {
                Map<String, Object> redisMap = (Map<String, Object>) RedisUtils.hGet(RedisKey.DEPLOY_CMD, equipmentId);
                if (redisMap == null) {
                    log.error("redisMap is null");
                    return;
                }
                String redisDeployStatus = (String) redisMap.get(ParamsKey.DEPLOY_STATUS);
                if (deployStatus.equals(redisDeployStatus)) {
                    control.setDeployStatus(deployStatus);
                    String key = equipmentId + Constant.SEPARATOR + CmdId.DEPLOY_STATUS;
                    RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
                    RedisUtils.hRemove(RedisKey.DEPLOY_CMD, equipmentId);
                }
            }
            //??????????????????
            String configValue = control.getConfigValue();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> config;
            try {
                config = mapper.readValue(configValue, Map.class);
            } catch (IOException e) {
                throw new ResponseException("active response config value is error: " + equipmentId);
            }
            //?????????????????????
            String heartbeatCycle = config.get(ParamsKey.HEART_BEAT_CYCLE);
            String workTime = config.get(ParamsKey.WORK_TIME);
            String lowTemperature = config.get(ParamsKey.LOW_TEMPERATURE);
            String highTemperature = config.get(ParamsKey.HIGH_TEMPERATURE);
            //??????????????????
            String cHeartbeatCycle = (String) dataSource.get(ParamsKey.HEART_BEAT_CYCLE);
            String cWorkTime = (String) dataSource.get(ParamsKey.WORK_TIME);
            Map<String, Object> temperatureThreshold = (Map<String, Object>) dataSource.get(WellConstant.TEMPERATURE_THRESHOLD);
            //??????????????????
            String cLowTemperature = String.valueOf(temperatureThreshold.get(ParamsKey.LOW_TEMPERATURE));
            String cHighTemperature = String.valueOf(temperatureThreshold.get(ParamsKey.HIGH_TEMPERATURE));
            if (heartbeatCycle.equals(cHeartbeatCycle) && workTime.equals(cWorkTime) && lowTemperature.equals(cLowTemperature) && highTemperature.equals(cHighTemperature)) {
                control.setSyncStatus(Constant.SYNC);
                String key2 = equipmentId + Constant.SEPARATOR + CmdId.SET_CONFIG;
                RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key2);
                RedisUtils.hRemove(RedisKey.SET_CONFIG_CMD, equipmentId);
            }
        }
        //???????????????????????????
        control.setActiveStatus(Constant.ACTIVE_STATUS);
        //??????????????????
        Long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString());
        control.setCurrentTime(time * 1000);
        //????????????????????????
        if (isNeedToResolve(equipmentId, CmdId.PARAMS_UPLOAD)) {
            log.info("the control : " + equipmentId + " cmd : " + CmdId.PARAMS_UPLOAD + " is not the newest>>>>>>>>");
            return;
        }
        // ????????????
        handleWellAlarm(equipmentId, deviceId, dataSource, control, time);
        //??????????????????
        setDeviceMsg(control, dataSource, deviceLog);
        //??????????????????
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        Map<Integer, String> doorMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        //??????
        Lock lock = new Lock();
        lock.setControlId(equipmentId);
        lock.setDoorNum(WellConstant.DOOR_NUM);
        lock.setDoorStatus(getInnerCoverStatus(dataSource));
        lock.setLockStatus(lockMap.get(1));
        lock.setUpdateTime(time * 1000);
        List<Lock> locks = new ArrayList<>();
        //??????
        Lock outLock = new Lock();
        outLock.setControlId(equipmentId);
        outLock.setDoorNum(WellConstant.OUT_COVER);
        outLock.setLockStatus(lockMap.get(1));
        outLock.setUpdateTime(time * 1000);
        outLock.setDoorStatus(doorMap.get(1));
        locks.add(lock);
        locks.add(outLock);
        lockFeign.updateLockStatus(locks);
    }

    /**
     * ??????????????????
     *
     * @param dataSource ?????????
     * @return ????????????
     */
    private String getInnerCoverStatus(Map<String, Object> dataSource) {
        //????????????
        Map<String, Integer> lean = (Map<String, Integer>) dataSource.get(ParamsKey.LEAN);
        String leanStatus = String.valueOf(lean.get(WellConstant.TILT_STATE));
        //??????????????? ????????????
        if (!WellConstant.NORMAL_STATUS.equals(leanStatus)) {
            return WellConstant.OPEAN;
        }
        return WellConstant.CLOSED;
    }

    /**
     * ???????????? ??????????????????
     *
     * @param controlParam ????????????
     * @param dataSource   ??????????????????
     * @param deviceLog    ????????????
     */
    private void setDeviceMsg(ControlParam controlParam, Map<String, Object> dataSource, DeviceLog deviceLog) {
        //?????????????????????
        //??????????????????
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        Map<Integer, String> outCoverMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        //????????????
        Lock lock = new Lock();
        lock.setDoorNum(WellConstant.OUT_COVER);
        lock.setControlId(controlParam.getControlId());
        lock.setDeviceId(controlParam.getDeviceId());
        Lock lastLockInfo = lockFeign.queryLockByDeviceIdAndDoorNum(lock);
        String lastDoorStatus = lastLockInfo.getDoorStatus().trim();
        //???????????????????????????
        Lock lock2 = new Lock();
        lock2.setDoorNum(WellConstant.ONE);
        lock2.setControlId(controlParam.getControlId());
        lock2.setDeviceId(controlParam.getDeviceId());
        Lock lastInnerLockInfo = lockFeign.queryLockByDeviceIdAndDoorNum(lock2);
        String lastInnerDoorStatus = lastInnerLockInfo.getDoorStatus().trim();
        String lastLockStatus = lastInnerLockInfo.getLockStatus().trim();
        //???????????? ?????????0??????????????????
        int eventNum = 0;
        //????????????????????????????????????????????? ??????????????????
        String outCoverStatus = outCoverMap.get(1);
        if (!lastDoorStatus.equals(outCoverStatus)) {
            eventNum++;
            if (WellConstant.OPEAN.equals(outCoverStatus)) {
                deviceLog.setRemarks(systemLanguageUtil.getI18nString(DeviceLogNameI18n.OUT_COVER_STATUS_OPEN));
            } else {
                deviceLog.setRemarks(systemLanguageUtil.getI18nString(DeviceLogNameI18n.OUT_COVER_STATUS_CLOSE));
            }
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.MANHOLE_COVER_CHANGE_EVENT), DeviceEventType.DOOR_STATE_CHANGE_EVENT);
        }
        //????????????????????????????????????????????? ??????????????????
        String innerCoverStatus = getInnerCoverStatus(dataSource);
        if (!lastInnerDoorStatus.equals(innerCoverStatus)) {
            eventNum++;
            if (WellConstant.OPEAN.equals(innerCoverStatus)) {
                deviceLog.setRemarks(systemLanguageUtil.getI18nString(DeviceLogNameI18n.INNER_COVER_STATUS_OPEN));
            } else {
                deviceLog.setRemarks(systemLanguageUtil.getI18nString(DeviceLogNameI18n.INNER_COVER_STATUS_CLOSE));
            }
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.MANHOLE_COVER_CHANGE_EVENT), DeviceEventType.DOOR_STATE_CHANGE_EVENT);
        }
        //?????????????????????????????????????????? ????????????????????????????????????
        String lockStatus = lockMap.get(1);
        if (!lastLockStatus.equals(lockStatus)) {
            eventNum++;
            //??????
            if (lockStatus.equals(WellConstant.UNLOCK)) {
                saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.OPEN_LOCK_EVENT), DeviceEventType.OPEN_LOCK_EVNET);
                //??????
            } else {
                saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.CLOSE_LOCK_EVENT), DeviceEventType.CLOSE_LOCK_EVENT);
            }
        }
        String lastSoftwareVersion = controlParam.getSoftwareVersion();
        String lastHardwareVersion = controlParam.getHardwareVersion();
        String versionNumber = String.valueOf(dataSource.get(WellConstant.VERSION_NUMBER)).trim();
        String[] s = versionNumber.split(WellConstant.UNDER_LINE);
        String currentSoftwareVersion = s[1];
        String currentHardwareVersion = s[2];
        //????????????
        if (!lastSoftwareVersion.equals(currentSoftwareVersion) || !lastHardwareVersion.equals(currentHardwareVersion)) {
            Long time = Long.valueOf((Integer) dataSource.get(ParamsKey.TIME));
            controlParam.setVersionUpdateTime(time * 1000);
            //???????????????
            controlParam.setSoftwareVersion(currentSoftwareVersion);
            controlParam.setHardwareVersion(currentHardwareVersion);
            eventNum++;
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.UPGRADE_SUCCESS_EVENT), DeviceEventType.UPGRADE_SUCCESS_EVENT);
        }
        //????????????
        if (eventNum == 0) {
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.ACTIVE_EVENT), DeviceEventType.ACTIVE_EVENT);
        }
    }


    /**
     * ??????????????????
     *
     * @param equipmentId ??????/??????id
     * @param deviceId    ??????id
     * @param dataSource  ??????????????????
     * @param control     ????????????
     * @param time        ??????????????????
     */
    private void handleWellAlarm(String equipmentId, String deviceId, Map<String, Object> dataSource, ControlParam control, long time) {
        //???????????????????????????
        //??????
        int electricity = Integer.parseInt((String) dataSource.get(ParamsKey.ELECTRICITY));
        Map<String, Object> electricityMap = new HashMap<>(64);
        electricityMap.put(ParamsKey.DATA, String.valueOf(electricity));
        electricityMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //??????
        int actualPower = Integer.parseInt((String) dataSource.get(WellConstant.ACTUAL_POWER));
        //todo ??????
        double temperature = (double) dataSource.get(ParamsKey.TEMPERATURE);
        //??????
        Map<String, Object> temperatureMap = new HashMap<>(64);
        temperatureMap.put(ParamsKey.DATA, String.valueOf((int) temperature));
        temperatureMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //??????
        int humidity = Integer.parseInt((String) dataSource.get(ParamsKey.HUMIDITY));
        Map<String, Object> humidityMap = new HashMap<>(64);
        humidityMap.put(ParamsKey.DATA, String.valueOf(humidity));
        humidityMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //??????
        Map<String, Object> leachMap = new HashMap<>(64);
        leachMap.put(ParamsKey.DATA, null);
        leachMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //????????????
        Map<String, String> outCoverAlarmType = (Map<String, String>) dataSource.get(WellConstant.DOOR_ALARM_TYPE);
        String outCoverAlarmCode = outCoverAlarmType.get(WellConstant.DOOR_LOCK_A).trim();
        //?????????
        Map<String, String> handleAlarmType = (Map<String, String>) dataSource.get(WellConstant.HANDLE_ALARM_TYPE);
        String lockAlarmCode = handleAlarmType.get(WellConstant.DOOR_LOCK_A).trim();
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        //????????????map
        Map<Integer, String> outCoverMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        String leach = (String) dataSource.get(ParamsKey.LEACH);
        String outCoverStatus = outCoverMap.get(1);
        String lockStatus = lockMap.get(1);
        //?????????????????????????????????
        String configValue = control.getConfigValue();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> config;
        try {
            config = mapper.readValue(configValue, Map.class);
        } catch (IOException e) {
            throw new ResponseException("active response config value is error: " + equipmentId);
        }
        List<String> currentAlarmList = new ArrayList<>();
        //????????????
        int configElectricity = Integer.parseInt(config.get(ParamsKey.ELECTRICITY));
        //todo ??????
        //int configActualPower = Integer.parseInt(config.get("voltage"));
        int configHumidity = Integer.parseInt(config.get(ParamsKey.HUMIDITY));
        //??????
        int lowTemperature = Integer.parseInt(config.get(ParamsKey.LOW_TEMPERATURE));
        //??????
        int highTemperature = Integer.parseInt(config.get(ParamsKey.HIGH_TEMPERATURE));
        //????????????paramList
        List paramList = new ArrayList<>();
        String inCoverStatus = getInnerCoverStatus(dataSource);
        //???????????????????????? ??????????????? ???????????????
        if (WellConstant.OPEAN.equals(inCoverStatus) && WellConstant.CLOSED.equals(lockStatus)) {
            Map data = new HashMap<>(64);
            data.put(WellConstant.ONE, inCoverStatus);
            paramList.add(setParamMap(ParamsKey.ILLEGAL_OPENING_INNER_COVER, data, Constant.ALARM));
            currentAlarmList.add(ParamsKey.ILLEGAL_OPENING_INNER_COVER);
        }
        if (electricity < configElectricity) {
            paramList.add(setParamMap(ParamsKey.ELECTRICITY, electricity, Constant.ALARM));
            electricityMap.put(ParamsKey.ALARM_FLAG, Constant.ALARM);
            currentAlarmList.add(ParamsKey.ELECTRICITY);
        }
        if (temperature > highTemperature) {
            paramList.add(setParamMap(ParamsKey.HIGH_TEMPERATURE, String.valueOf((int) temperature), Constant.ALARM));
            temperatureMap.put(ParamsKey.ALARM_FLAG, Constant.ALARM);
            currentAlarmList.add(ParamsKey.HIGH_TEMPERATURE);
        } else if (temperature < lowTemperature) {
            paramList.add(setParamMap(ParamsKey.LOW_TEMPERATURE, String.valueOf((int) temperature), Constant.ALARM));
            temperatureMap.put(ParamsKey.ALARM_FLAG, Constant.ALARM);
            currentAlarmList.add(ParamsKey.LOW_TEMPERATURE);
        }
        if (!WellConstant.ONE.equals(leach)) {
            Map<String, Object> paramMap = setParamMap(ParamsKey.LEACH, null, Constant.ALARM);
            paramList.add(paramMap);
        }
        if (humidity > configHumidity) {
            paramList.add(setParamMap(ParamsKey.HUMIDITY, humidity, Constant.ALARM));
            humidityMap.put(ParamsKey.ALARM_FLAG, Constant.ALARM);
            currentAlarmList.add(ParamsKey.HUMIDITY);
        }
//        //???????????? ????????????
//        if (!WellConstant.NORMAL_STATUS.equals(outCoverAlarmCode)) {
//            Map data = new HashMap<>(64);
//            data.put(WellConstant.OUT_COVER, outCoverStatus);
//            paramList.add(setParamMap(outCoverAlarmCode, data, Constant.ALARM));
//            currentAlarmList.add(outCoverAlarmCode);
//        }
        if (!WellConstant.NORMAL_STATUS.equals(lockAlarmCode)) {
            Map data = new HashMap<>(64);
            data.put(WellConstant.ONE, lockStatus);
            paramList.add(setParamMap(lockAlarmCode, data, Constant.ALARM));
            currentAlarmList.add(lockAlarmCode);
        }
        judgingAlarm(deviceId, currentAlarmList, paramList);
        Map<String, Object> actValueMap = new HashMap<>(64);
        actValueMap.put(ParamsKey.TEMPERATURE, temperatureMap);
        actValueMap.put(ParamsKey.HUMIDITY, humidityMap);
        actValueMap.put(ParamsKey.LEACH, leachMap);
        actValueMap.put(ParamsKey.ELECTRICITY, electricityMap);
        String wirelessModuleSignal = (String) dataSource.get(WellConstant.WIRELESS_MODULE_SIGNAL);
        Map<String, Object> wirelessModuleSignalMap = new HashMap<>(64);
        wirelessModuleSignalMap.put(ParamsKey.DATA, wirelessModuleSignal);
        wirelessModuleSignalMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        actValueMap.put(ParamsKey.WIRELESS_MODULE_SIGNAL, wirelessModuleSignalMap);
        control.setActualValue(JSONObject.toJSONString(actValueMap));
        String deployStatus = control.getDeployStatus();
        sendJudgedAlarm(currentAlarmList, deployStatus, control, paramList, equipmentId, time);
    }

    /**
     * ?????????????????????????????????
     * ????????????
     *
     * @param deviceId         ??????id
     * @param currentAlarmList ??????????????????
     * @param paramList        ????????????????????????
     */
    private void judgingAlarm(String deviceId, List<String> currentAlarmList, List paramList) {
        //????????????
        List<AlarmCurrent> lastAlarm = getCurrentAlarm(deviceId);
        List<String> hasDoorAlarm = new ArrayList<>();
        hasDoorAlarm.add(ParamsKey.NOT_CLOSED);
        hasDoorAlarm.add("unLock");
        hasDoorAlarm.add(ParamsKey.PRY_LOCK);
        hasDoorAlarm.add(ParamsKey.ILLEGAL_OPENING_INNER_COVER);
        if (lastAlarm.size() != 0) {
            //?????????????????? ?????????
            Map<String, List<AlarmCurrent>> collect = lastAlarm.stream().collect(Collectors.groupingBy(AlarmCurrent::getAlarmCode));
            Set<String> alarmCodeSet = collect.keySet();
            //???????????????????????????????????? ???????????????
            for (String code : alarmCodeSet) {
                if (!currentAlarmList.contains(code)) {
                    Object data = null;
                    if (hasDoorAlarm.contains(code)) {
                        data = new HashMap<>(64);
                        ((HashMap) data).put(WellConstant.ONE, "22");
                    }
                    //???????????????
                    paramList.add(setParamMap(code, data, Constant.CANCEL_ALARM));
                }
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param currentAlarmList ???????????????????????????
     * @param deployStatus     ????????????
     * @param control          ????????????
     * @param paramList        ???????????????????????????
     * @param equipmentId      ??????/??????id
     * @param time             ??????????????????
     */
    private void sendJudgedAlarm(List<String> currentAlarmList, String deployStatus, ControlParam control, List paramList, String equipmentId, long time) {
        //?????????????????????
        if (DeployStatus.DEPLOYED.getCode().equals(deployStatus)) {
            //??????????????????
            if (currentAlarmList.size() > 0) {
                //???????????????????????????
                control.setDeviceStatus(DeviceStatus.Alarm.getCode());
            } else {
                //??????????????????????????????
                control.setDeviceStatus(DeviceStatus.Normal.getCode());
            }
            //????????????
            if (paramList.size() > 0) {
                sendAlarmMap(equipmentId, time, control.getDeviceId(), paramList);
            }
        }
        updateDeviceStatus(control);
    }

    /**
     * ?????????????????? ??????????????????????????????????????????
     *
     * @param deviceId ??????id
     * @return ??????????????????
     */
    private List<AlarmCurrent> getCurrentAlarm(String deviceId) {
        List<AlarmCurrent> alarmCurrents = alarmCurrentFeign.queryAlarmDeviceIdCode(deviceId);
        if (alarmCurrents == null) {
            log.error("Failed to get alarm information");
        }
        return alarmCurrents;
    }

    /**
     * ????????????map
     *
     * @param alarmCode ??????code
     * @param data      ????????????
     * @param alarmFlag ????????????
     * @return ????????????
     */
    private Map<String, Object> setParamMap(String alarmCode, Object data, String alarmFlag) {
        Map<String, Object> paramMap = new HashMap<>(64);
        paramMap.put(ParamsKey.DATA_CLASS, alarmCode);
        paramMap.put(ParamsKey.DATA, data);
        paramMap.put(ParamsKey.ALARM_FLAG, alarmFlag);
        return paramMap;
    }

    /**
     * ????????????
     *
     * @param time      ??????????????????
     * @param deviceId  ??????id
     * @param paramList ??????????????????
     */
    private void sendAlarmMap(String equipmentId, long time, String deviceId, Object paramList) {
        //????????????????????????
        Map<String, Object> alarmMap = new HashMap<>(64);
        alarmMap.put(ParamsKey.DEVICE_ID, deviceId);
        alarmMap.put(ParamsKey.TIME, time * 1000);
        alarmMap.put(ParamsKey.PARAMS_KEY, paramList);
        alarmMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        streamSender.sendAlarm(alarmMap);
    }

    /**
     * ??????????????????
     *
     * @param deviceLog ????????????
     * @param logType   ??????????????????
     * @param logName   ??????????????????
     * @param type      ????????????
     */
    public void saveDeviceLog(DeviceLog deviceLog, String logType, String logName, String type) {
        deviceLog.setLogName(logName);
        deviceLog.setLogType(logType);
        deviceLog.setType(type);
        deviceLogFeign.saveDeviceLog(deviceLog);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param equipmentId ??????id
     * @param cmdId       ??????id
     * @return ??????
     */
    private boolean isNeedToResolve(String equipmentId, String cmdId) {
        long currentTime = System.currentTimeMillis();
        //??????key???
        String key = equipmentId + Constant.SEPARATOR + cmdId;
        //???redis????????????????????????
        Long lastTime = (Long) RedisUtils.hGet(RedisKey.CMD_TIME, key);
        if (lastTime == null) {
            RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTime);
            return false;
        } else {
            //?????????????????????????????????,????????????
            if (currentTime > lastTime) {
                RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTime);
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * ??????????????????
     *
     * @param equipmentId ??????id
     */
    private void resolveUnlockResponse(String equipmentId) {

        //??????redis???????????????
        String key = equipmentId + Constant.SEPARATOR + CmdId.UNLOCK;
        RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
        RedisUtils.hRemove(equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER, key);
        //????????????????????????
        String success = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_SUCCESS);
        String slotNum = WellConstant.DOOR_NUM;
        String socketMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_RESULT) + slotNum + ":" + success;
        commonUtil.unLockMessagePush(equipmentId, socketMsg, key, ResultUtils.success(OceanConnectResultCode.WELL_UNLOCKED_SUCCESSFULLY, null));
    }

    /**
     * ??????????????????????????????
     *
     * @param equipmentId ??????id
     */
    private void executeUnlockSetConfCmd(String equipmentId) {
        String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        String key = equipmentId + Constant.SEPARATOR + CmdId.UNLOCK;
        //???????????????????????????
        if (RedisUtils.hasKey(unlockKey)) {
            Map<Object, Object> cmdMap = RedisUtils.hGetMap(unlockKey);
            for (Map.Entry<Object, Object> cmdEntry : cmdMap.entrySet()) {
                Map<String, Object> unlockDataMap = (Map<String, Object>) cmdEntry.getValue();
                resendCmd(unlockDataMap);
            }
        }
        RedisUtils.hRemove(unlockKey, key);
    }

    /**
     * ??????????????????
     *
     * @param dataMap ??????map
     */
    private void resendCmd(Map<String, Object> dataMap) {
        String hexData = (String) dataMap.get(ParamsKey.HEX_DATA);
        //????????????id
        String oceanConnectId = dataMap.get(OceanParamsKey.DEVICE_ID).toString();
        //??????appId
        String appId = dataMap.get(OceanParamsKey.APP_ID).toString();
        //????????????
        FiLinkReqOceanConnectParams oceanConnectParams = new FiLinkReqOceanConnectParams();
        oceanConnectParams.setAppId(appId);
        oceanConnectParams.setOceanConnectId(oceanConnectId);
        sendUtil.sendData(oceanConnectParams, hexData);
    }
}
