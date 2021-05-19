package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
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
 * filink数据业务处理类
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
     * filink数据业务处理
     *
     * @param abstractResOutputParams AbstractResOutputParams
     */
    @Override
    public void handleMsg(AbstractResOutputParams abstractResOutputParams) {
        FiLinkOceanResOutputParams outputParams = (FiLinkOceanResOutputParams) abstractResOutputParams;
        //指令id
        String cmdId = outputParams.getCmdId();
        String equipmentId = outputParams.getEquipmentId();
        Integer serialNumber = outputParams.getSerialNumber();
        //过滤相同流水号的指令
        if (commonUtil.filterRepeatCmd(equipmentId, serialNumber)) {
            log.info("the equipmentId : " + equipmentId + " , cmd : " + cmdId + " is repeat>>>>>>>>>>>>");
            return;
        }
        //查询该设施主控信息
        ControlParam control = abstractResOutputParams.getControlParam();
        if (control == null) {
            throw new ResponseException("control info is null>>>>>>>>>>>>>");
        }
        String deviceId = control.getDeviceId();
        //刷新主控心跳时间
        commonUtil.refreshRedisTime(equipmentId, control);
        //更新设施状态为正常
        updateDeviceStatusNormal(equipmentId, control, deviceId);
        Map<String, Object> dataSource = outputParams.getParams();
        DeviceLog deviceLog = commonUtil.setDeviceLogCommonInfo(control);
        //根据不同指令进行不同处理
        handleCmd(cmdId, deviceId, equipmentId, dataSource, control, deviceLog);
        //判断部署状态是否是部署中,如果是则进行部署状态下发
        updateDeployStatus(equipmentId, control);
        //下发设备缓存开锁命令 参数下发命令
        executeUnlockSetConfCmd(equipmentId);
    }

    /**
     * 判断是否需要下发部署状态指令
     *
     * @param equipmentId 主控id
     * @param control     主控信息
     */
    private void updateDeployStatus(String equipmentId, ControlParam control) {
        //如果是部署中则进行部署状态下发
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
     * 根据不同指令进行不同处理
     *
     * @param cmdId       命令id
     * @param deviceId    设施id
     * @param equipmentId 装置id
     * @param dataSource  元数据
     * @param control     主控信息
     * @param deviceLog   设施日志
     */
    private void handleCmd(String cmdId, String deviceId, String equipmentId, Map<String, Object> dataSource,
                           ControlParam control, DeviceLog deviceLog) {
        switch (cmdId) {
            //人井心跳
            case CmdId.WEII_HEART_BEAT:
                resolveWellHeartBeat(deviceId, control, dataSource, equipmentId, deviceLog);
                break;
            //todo 设备升级
//            case "0x2b5a":
//                resolveWellUpgrade(deviceId, control, dataSource, equipmentId);
//                break;
            //todo 查询设备升级包信息
//            case "0x285a":
//                resolveWellFirmwareInfo(dataSource,control);
//                break;
            default:
                log.info("cmd id: " + cmdId + " is not support>>>>>>>>>>>>>>");
        }
    }

    /**
     * 获取升级包信息
     * todo 暂时挂起 预留开发
     *
     * @param control    主控信息
     * @param dataSource 设备上报信息
     */
//    private void resolveWellFirmwareInfo(Map<String, Object> dataSource, ControlParam control) {
//        //版本号
//        String versionNumber = (String) dataSource.get("versionNumber");
//        //todo 升级包文件
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
     * 人井升级
     * todo 暂时挂起 预留开发
     *
     * @param deviceId    设施id
     * @param control     主控信息
     * @param dataSource  设备上报参数
     * @param equipmentId 主控/设备id
    //     */
//    private void resolveWellUpgrade(String deviceId, ControlParam control, Map<String, Object> dataSource,
//                                    String equipmentId) {
//        //判断是否需要处理
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
//        //todo 暂时挂起 预留开发
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
     * 发送指令
     *todo 人井升级用  暂时挂起
     * @param control 主控信息
     * @param cmdId   命令id
     * @param params  参数信息
     */
//    private void sendResponse(ControlParam control, String cmdId, Map<String, Object> params) {
//        sender.sendInstruct(commonUtil.getResponseParams(control, cmdId, params, null));
//    }


    /**
     * 将主控状态更新为正常
     *
     * @param equipmentId 主控id
     * @param control     主控信息
     * @param deviceId    设施id
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
            //发送通信中断告警恢复
            control.setDeviceStatus(normalCode);
            Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.CANCEL_ALARM);
            streamSender.sendAlarm(alarmMap);
            updateDeviceStatus(control);
        }
    }

    /**
     * 更新主控状态
     *
     * @param controlParam 主控信息
     */
    private void updateDeviceStatus(ControlParam controlParam) {
        //更新redis
        RedisUtils.hSet(RedisKey.CONTROL_INFO, controlParam.getHostId(), controlParam);
        controlFeign.updateControlStatusById(controlParam);
    }


    /**
     * 人井心跳处理
     *
     * @param deviceId    设施id
     * @param control     主控信息
     * @param dataSource  设备上报信息
     * @param equipmentId 主控/设备id
     * @param deviceLog   设施日志实体
     */
    private void resolveWellHeartBeat(String deviceId, ControlParam control, Map<String, Object> dataSource,
                                      String equipmentId, DeviceLog deviceLog) {
        String deployStatus = (String) dataSource.get(ParamsKey.DEPLOY_STATUS);
        //如果有开锁动作
        String currentAction = String.valueOf(dataSource.get(WellConstant.CURRENT_ACTION));
        if (WellConstant.UNLOCKING_ACTION.equals(currentAction)) {
            resolveUnlockResponse(equipmentId);
        }
        //如果参数修改 清除缓存中的参数
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
            //获取配置参数
            String configValue = control.getConfigValue();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> config;
            try {
                config = mapper.readValue(configValue, Map.class);
            } catch (IOException e) {
                throw new ResponseException("active response config value is error: " + equipmentId);
            }
            //数据库中存的值
            String heartbeatCycle = config.get(ParamsKey.HEART_BEAT_CYCLE);
            String workTime = config.get(ParamsKey.WORK_TIME);
            String lowTemperature = config.get(ParamsKey.LOW_TEMPERATURE);
            String highTemperature = config.get(ParamsKey.HIGH_TEMPERATURE);
            //设备上报的值
            String cHeartbeatCycle = (String) dataSource.get(ParamsKey.HEART_BEAT_CYCLE);
            String cWorkTime = (String) dataSource.get(ParamsKey.WORK_TIME);
            Map<String, Object> temperatureThreshold = (Map<String, Object>) dataSource.get(WellConstant.TEMPERATURE_THRESHOLD);
            //设置主控时间
            String cLowTemperature = String.valueOf(temperatureThreshold.get(ParamsKey.LOW_TEMPERATURE));
            String cHighTemperature = String.valueOf(temperatureThreshold.get(ParamsKey.HIGH_TEMPERATURE));
            if (heartbeatCycle.equals(cHeartbeatCycle) && workTime.equals(cWorkTime) && lowTemperature.equals(cLowTemperature) && highTemperature.equals(cHighTemperature)) {
                control.setSyncStatus(Constant.SYNC);
                String key2 = equipmentId + Constant.SEPARATOR + CmdId.SET_CONFIG;
                RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key2);
                RedisUtils.hRemove(RedisKey.SET_CONFIG_CMD, equipmentId);
            }
        }
        //将主控状态改为激活
        control.setActiveStatus(Constant.ACTIVE_STATUS);
        //获取上报时间
        Long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString());
        control.setCurrentTime(time * 1000);
        //判断是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.PARAMS_UPLOAD)) {
            log.info("the control : " + equipmentId + " cmd : " + CmdId.PARAMS_UPLOAD + " is not the newest>>>>>>>>");
            return;
        }
        // 处理告警
        handleWellAlarm(equipmentId, deviceId, dataSource, control, time);
        //判断事件变化
        setDeviceMsg(control, dataSource, deviceLog);
        //跟新门锁信息
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        Map<Integer, String> doorMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        //内盖
        Lock lock = new Lock();
        lock.setControlId(equipmentId);
        lock.setDoorNum(WellConstant.DOOR_NUM);
        lock.setDoorStatus(getInnerCoverStatus(dataSource));
        lock.setLockStatus(lockMap.get(1));
        lock.setUpdateTime(time * 1000);
        List<Lock> locks = new ArrayList<>();
        //外盖
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
     * 获取内盖状态
     *
     * @param dataSource 传感值
     * @return 内盖状态
     */
    private String getInnerCoverStatus(Map<String, Object> dataSource) {
        //内盖状态
        Map<String, Integer> lean = (Map<String, Integer>) dataSource.get(ParamsKey.LEAN);
        String leanStatus = String.valueOf(lean.get(WellConstant.TILT_STATE));
        //如果有倾斜 内盖打开
        if (!WellConstant.NORMAL_STATUS.equals(leanStatus)) {
            return WellConstant.OPEAN;
        }
        return WellConstant.CLOSED;
    }

    /**
     * 事件判断 处理设施日志
     *
     * @param controlParam 主控信息
     * @param dataSource   设备上报信息
     * @param deviceLog    设施日志
     */
    private void setDeviceMsg(ControlParam controlParam, Map<String, Object> dataSource, DeviceLog deviceLog) {
        //获取事件中的值
        //获取门锁状态
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        Map<Integer, String> outCoverMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        //外盖状态
        Lock lock = new Lock();
        lock.setDoorNum(WellConstant.OUT_COVER);
        lock.setControlId(controlParam.getControlId());
        lock.setDeviceId(controlParam.getDeviceId());
        Lock lastLockInfo = lockFeign.queryLockByDeviceIdAndDoorNum(lock);
        String lastDoorStatus = lastLockInfo.getDoorStatus().trim();
        //查询上次的内盖状态
        Lock lock2 = new Lock();
        lock2.setDoorNum(WellConstant.ONE);
        lock2.setControlId(controlParam.getControlId());
        lock2.setDeviceId(controlParam.getDeviceId());
        Lock lastInnerLockInfo = lockFeign.queryLockByDeviceIdAndDoorNum(lock2);
        String lastInnerDoorStatus = lastInnerLockInfo.getDoorStatus().trim();
        String lastLockStatus = lastInnerLockInfo.getLockStatus().trim();
        //事件数量 如果为0则是唤醒事件
        int eventNum = 0;
        //如果上次的外盖状态与这此的不同 则为外盖变化
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
        //如果上次的内盖状态与这此的不同 则为井盖变化
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
        //如果上次的锁状态与这此的不同 则为关锁事件或者开锁事件
        String lockStatus = lockMap.get(1);
        if (!lastLockStatus.equals(lockStatus)) {
            eventNum++;
            //开锁
            if (lockStatus.equals(WellConstant.UNLOCK)) {
                saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.OPEN_LOCK_EVENT), DeviceEventType.OPEN_LOCK_EVNET);
                //关锁
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
        //升级事件
        if (!lastSoftwareVersion.equals(currentSoftwareVersion) || !lastHardwareVersion.equals(currentHardwareVersion)) {
            Long time = Long.valueOf((Integer) dataSource.get(ParamsKey.TIME));
            controlParam.setVersionUpdateTime(time * 1000);
            //设置版本号
            controlParam.setSoftwareVersion(currentSoftwareVersion);
            controlParam.setHardwareVersion(currentHardwareVersion);
            eventNum++;
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.UPGRADE_SUCCESS_EVENT), DeviceEventType.UPGRADE_SUCCESS_EVENT);
        }
        //唤醒事件
        if (eventNum == 0) {
            saveDeviceLog(deviceLog, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.ACTIVE_EVENT), DeviceEventType.ACTIVE_EVENT);
        }
    }


    /**
     * 处理人井告警
     *
     * @param equipmentId 主控/设备id
     * @param deviceId    设施id
     * @param dataSource  设备上报参数
     * @param control     主控信息
     * @param time        设备上报时间
     */
    private void handleWellAlarm(String equipmentId, String deviceId, Map<String, Object> dataSource, ControlParam control, long time) {
        //获取产生告警的字段
        //电量
        int electricity = Integer.parseInt((String) dataSource.get(ParamsKey.ELECTRICITY));
        Map<String, Object> electricityMap = new HashMap<>(64);
        electricityMap.put(ParamsKey.DATA, String.valueOf(electricity));
        electricityMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //电压
        int actualPower = Integer.parseInt((String) dataSource.get(WellConstant.ACTUAL_POWER));
        //todo 温度
        double temperature = (double) dataSource.get(ParamsKey.TEMPERATURE);
        //温度
        Map<String, Object> temperatureMap = new HashMap<>(64);
        temperatureMap.put(ParamsKey.DATA, String.valueOf((int) temperature));
        temperatureMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //湿度
        int humidity = Integer.parseInt((String) dataSource.get(ParamsKey.HUMIDITY));
        Map<String, Object> humidityMap = new HashMap<>(64);
        humidityMap.put(ParamsKey.DATA, String.valueOf(humidity));
        humidityMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //水浸
        Map<String, Object> leachMap = new HashMap<>(64);
        leachMap.put(ParamsKey.DATA, null);
        leachMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
        //外盖告警
        Map<String, String> outCoverAlarmType = (Map<String, String>) dataSource.get(WellConstant.DOOR_ALARM_TYPE);
        String outCoverAlarmCode = outCoverAlarmType.get(WellConstant.DOOR_LOCK_A).trim();
        //锁告警
        Map<String, String> handleAlarmType = (Map<String, String>) dataSource.get(WellConstant.HANDLE_ALARM_TYPE);
        String lockAlarmCode = handleAlarmType.get(WellConstant.DOOR_LOCK_A).trim();
        Map<String, Object> map = (Map<String, Object>) dataSource.get(WellConstant.DOOR_LOCK_STATE);
        //外盖状态map
        Map<Integer, String> outCoverMap = (Map<Integer, String>) map.get(WellConstant.DOOR_MAP);
        Map<Integer, String> lockMap = (Map<Integer, String>) map.get(WellConstant.LOCK_MAP);
        String leach = (String) dataSource.get(ParamsKey.LEACH);
        String outCoverStatus = outCoverMap.get(1);
        String lockStatus = lockMap.get(1);
        //获取数据库中的配置参数
        String configValue = control.getConfigValue();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> config;
        try {
            config = mapper.readValue(configValue, Map.class);
        } catch (IOException e) {
            throw new ResponseException("active response config value is error: " + equipmentId);
        }
        List<String> currentAlarmList = new ArrayList<>();
        //告警阈值
        int configElectricity = Integer.parseInt(config.get(ParamsKey.ELECTRICITY));
        //todo 电压
        //int configActualPower = Integer.parseInt(config.get("voltage"));
        int configHumidity = Integer.parseInt(config.get(ParamsKey.HUMIDITY));
        //低温
        int lowTemperature = Integer.parseInt(config.get(ParamsKey.LOW_TEMPERATURE));
        //高温
        int highTemperature = Integer.parseInt(config.get(ParamsKey.HIGH_TEMPERATURE));
        //构造告警paramList
        List paramList = new ArrayList<>();
        String inCoverStatus = getInnerCoverStatus(dataSource);
        //如果锁状态是关的 内盖是开的 则非法开门
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
//        //外盖告警 目前不管
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
     * 判断是告警生成还是清除
     * 判断告警
     *
     * @param deviceId         设施id
     * @param currentAlarmList 当前告警信息
     * @param paramList        发送告警信息集合
     */
    private void judgingAlarm(String deviceId, List<String> currentAlarmList, List paramList) {
        //如果存在
        List<AlarmCurrent> lastAlarm = getCurrentAlarm(deviceId);
        List<String> hasDoorAlarm = new ArrayList<>();
        hasDoorAlarm.add(ParamsKey.NOT_CLOSED);
        hasDoorAlarm.add("unLock");
        hasDoorAlarm.add(ParamsKey.PRY_LOCK);
        hasDoorAlarm.add(ParamsKey.ILLEGAL_OPENING_INNER_COVER);
        if (lastAlarm.size() != 0) {
            //获取告警编号 门编号
            Map<String, List<AlarmCurrent>> collect = lastAlarm.stream().collect(Collectors.groupingBy(AlarmCurrent::getAlarmCode));
            Set<String> alarmCodeSet = collect.keySet();
            //之前有的告警，现在没有了 做告警恢复
            for (String code : alarmCodeSet) {
                if (!currentAlarmList.contains(code)) {
                    Object data = null;
                    if (hasDoorAlarm.contains(code)) {
                        data = new HashMap<>(64);
                        ((HashMap) data).put(WellConstant.ONE, "22");
                    }
                    //恢复此告警
                    paramList.add(setParamMap(code, data, Constant.CANCEL_ALARM));
                }
            }
        }
    }

    /**
     * 发送已经处理的告警
     *
     * @param currentAlarmList 当前设备上报的告警
     * @param deployStatus     部署状态
     * @param control          主控信息
     * @param paramList        发送告警的信息集合
     * @param equipmentId      主控/设备id
     * @param time             设备上报时间
     */
    private void sendJudgedAlarm(List<String> currentAlarmList, String deployStatus, ControlParam control, List paramList, String equipmentId, long time) {
        //如果是布防状态
        if (DeployStatus.DEPLOYED.getCode().equals(deployStatus)) {
            //如果存在告警
            if (currentAlarmList.size() > 0) {
                //将主控状态改为告警
                control.setDeviceStatus(DeviceStatus.Alarm.getCode());
            } else {
                //将主控信息更改为正常
                control.setDeviceStatus(DeviceStatus.Normal.getCode());
            }
            //发送告警
            if (paramList.size() > 0) {
                sendAlarmMap(equipmentId, time, control.getDeviceId(), paramList);
            }
        }
        updateDeviceStatus(control);
    }

    /**
     * 获取告警信息 与上报心跳进行比对，判断事件
     *
     * @param deviceId 设施id
     * @return 当前告警集合
     */
    private List<AlarmCurrent> getCurrentAlarm(String deviceId) {
        List<AlarmCurrent> alarmCurrents = alarmCurrentFeign.queryAlarmDeviceIdCode(deviceId);
        if (alarmCurrents == null) {
            log.error("Failed to get alarm information");
        }
        return alarmCurrents;
    }

    /**
     * 设置告警map
     *
     * @param alarmCode 告警code
     * @param data      告警信息
     * @param alarmFlag 告警标志
     * @return 告警信息
     */
    private Map<String, Object> setParamMap(String alarmCode, Object data, String alarmFlag) {
        Map<String, Object> paramMap = new HashMap<>(64);
        paramMap.put(ParamsKey.DATA_CLASS, alarmCode);
        paramMap.put(ParamsKey.DATA, data);
        paramMap.put(ParamsKey.ALARM_FLAG, alarmFlag);
        return paramMap;
    }

    /**
     * 发送告警
     *
     * @param time      设备上报事件
     * @param deviceId  设施id
     * @param paramList 告警信息集合
     */
    private void sendAlarmMap(String equipmentId, long time, String deviceId, Object paramList) {
        //构造告警参数信息
        Map<String, Object> alarmMap = new HashMap<>(64);
        alarmMap.put(ParamsKey.DEVICE_ID, deviceId);
        alarmMap.put(ParamsKey.TIME, time * 1000);
        alarmMap.put(ParamsKey.PARAMS_KEY, paramList);
        alarmMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        streamSender.sendAlarm(alarmMap);
    }

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志
     * @param logType   设施日志类型
     * @param logName   设施日志名称
     * @param type      事件类型
     */
    public void saveDeviceLog(DeviceLog deviceLog, String logType, String logName, String type) {
        deviceLog.setLogName(logName);
        deviceLog.setLogType(logType);
        deviceLog.setType(type);
        deviceLogFeign.saveDeviceLog(deviceLog);
    }

    /**
     * 判断该命令时间是否大于上次时间
     *
     * @param equipmentId 主控id
     * @param cmdId       指令id
     * @return 结果
     */
    private boolean isNeedToResolve(String equipmentId, String cmdId) {
        long currentTime = System.currentTimeMillis();
        //拼接key值
        String key = equipmentId + Constant.SEPARATOR + cmdId;
        //从redis获取该指令的时间
        Long lastTime = (Long) RedisUtils.hGet(RedisKey.CMD_TIME, key);
        if (lastTime == null) {
            RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTime);
            return false;
        } else {
            //该指令时间大于上次时间,执行操作
            if (currentTime > lastTime) {
                RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTime);
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 处理开锁响应
     *
     * @param equipmentId 主控id
     */
    private void resolveUnlockResponse(String equipmentId) {

        //清除redis中重发指令
        String key = equipmentId + Constant.SEPARATOR + CmdId.UNLOCK;
        RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
        RedisUtils.hRemove(equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER, key);
        //拼接推送提示信息
        String success = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_SUCCESS);
        String slotNum = WellConstant.DOOR_NUM;
        String socketMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_RESULT) + slotNum + ":" + success;
        commonUtil.unLockMessagePush(equipmentId, socketMsg, key, ResultUtils.success(OceanConnectResultCode.WELL_UNLOCKED_SUCCESSFULLY, null));
    }

    /**
     * 下发设备缓存开锁命令
     *
     * @param equipmentId 设备id
     */
    private void executeUnlockSetConfCmd(String equipmentId) {
        String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        String key = equipmentId + Constant.SEPARATOR + CmdId.UNLOCK;
        //获取缓存中开锁命令
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
     * 重发命令方法
     *
     * @param dataMap 数据map
     */
    private void resendCmd(Map<String, Object> dataMap) {
        String hexData = (String) dataMap.get(ParamsKey.HEX_DATA);
        //获取平台id
        String oceanConnectId = dataMap.get(OceanParamsKey.DEVICE_ID).toString();
        //获取appId
        String appId = dataMap.get(OceanParamsKey.APP_ID).toString();
        //发送指令
        FiLinkReqOceanConnectParams oceanConnectParams = new FiLinkReqOceanConnectParams();
        oceanConnectParams.setAppId(appId);
        oceanConnectParams.setOceanConnectId(oceanConnectId);
        sendUtil.sendData(oceanConnectParams, hexData);
    }
}
