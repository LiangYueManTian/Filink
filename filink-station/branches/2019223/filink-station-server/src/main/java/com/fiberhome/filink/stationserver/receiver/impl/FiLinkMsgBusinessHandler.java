package com.fiberhome.filink.stationserver.receiver.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.api.DeviceLogFeign;
import com.fiberhome.filink.device_api.bean.AreaInfo;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.device_api.bean.DeviceLog;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.stationserver.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResOutputParams;
import com.fiberhome.filink.stationserver.exception.FiLinkBusinessException;
import com.fiberhome.filink.stationserver.receiver.MsgBusinessHandler;
import com.fiberhome.filink.stationserver.sender.AbstractInstructSender;
import com.fiberhome.filink.stationserver.util.JsonUtils;
import com.fiberhome.filink.stationserver.util.lockenum.AlarmFlag;
import com.fiberhome.filink.stationserver.util.lockenum.CmdId;
import com.fiberhome.filink.stationserver.util.lockenum.CmdType;
import com.fiberhome.filink.stationserver.util.lockenum.ParamsKey;
import com.fiberhome.filink.stationserver.util.log.DeviceLogName;
import com.fiberhome.filink.stationserver.util.log.DeviceLogType;
import lombok.extern.log4j.Log4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filink数据业务处理类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class FiLinkMsgBusinessHandler implements MsgBusinessHandler {

    @Autowired
    private AbstractInstructSender sender;

    @Autowired
    private LockFeign lockFeign;

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private DeviceLogFeign deviceLogFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String NOT_SYNC_STATE = "1";

    private final String SYNC_STATE = "2";

    private final String OPEN_LOCK_STATE = "1";

    private final String SUCCESS_OPEN_LOCK = "success";

    /**
     * filink数据业务处理
     *
     * @param abstractResOutputParams AbstractResOutputParams
     */
    @Override
    public void handleMsg(AbstractResOutputParams abstractResOutputParams) throws Exception {
        FiLinkResOutputParams outputParams = (FiLinkResOutputParams) abstractResOutputParams;
        // todo 心跳帧
        if (outputParams.isHeartBeat()) {
            return;
        }
        //设施日志
        DeviceLog deviceLog = new DeviceLog();
        //指令id
        String cmdId = outputParams.getCmdId();
        String serialNum = outputParams.getDeviceId();
        Map<String, Object> dataSource = outputParams.getParams();
        //根据serialNum查询设施对象
        DeviceInfoDto deviceInfoDto = deviceFeign.findDeviceBySeriaNumber(serialNum);
        if (deviceInfoDto == null) {
            throw new FiLinkBusinessException("device info is null>>>>>>>");
        }
        String deviceId = deviceInfoDto.getDeviceId();
        //查询该设施主控信息
        Control control = controlFeign.getControlParams(deviceId);
        //获取主控信息
        if (control == null) {
            throw new FiLinkBusinessException("control is null>>>>>>>");
        }
        //设置设施日志信息
        deviceLog.setSerialNum(serialNum);
        AreaInfo areaInfo = deviceInfoDto.getAreaInfo();
        deviceLog.setNodeObject(control.getControlType());
        deviceLog.setAreaName(areaInfo.getAreaName());
        deviceLog.setDeviceType(deviceInfoDto.getDeviceType());
        deviceLog.setDeviceCode(deviceInfoDto.getDeviceCode());
        deviceLog.setDeviceName(deviceInfoDto.getDeviceName());
        deviceLog.setCurrentTime(System.currentTimeMillis());
        deviceLog.setDeviceId(deviceId);
        //开锁响应帧
        if (CmdId.UNLOCK.equals(cmdId)) {
            resolveUnlockResponse(deviceId,dataSource,deviceLog);
            //参数上报
        } else if (CmdId.PARAMS_UPLOAD.equals(cmdId)) {
            sendResponse(serialNum, cmdId);
            resolveParamUpload(deviceId, control, dataSource,deviceLog);
            //激活事件
        } else if (CmdId.ACTIVE.equals(cmdId)) {
            resolveActive(deviceId, dataSource,deviceLog);
            //todo 回复响应
            //配置设施策略
        } else if (CmdId.SET_CONFIG.equals(cmdId)) {
            resolveSetConfig(control);
            //休眠事件
        } else if (CmdId.SLEEP.equals(cmdId)) {
            sendResponse(serialNum, cmdId);
            resolveSleep(deviceLog);
            //开锁结果上报
        } else if (CmdId.OPEN_LOCK_UPLOAD.equals(cmdId)) {
            sendResponse(serialNum, cmdId);
            resolveOpenLockUpload(deviceId, dataSource, deviceLog);
            //关锁结果上报
        }else if (CmdId.CLOSE_LOCK_UPLOAD.equals(cmdId)){
            resolveCloseLockUpload(deviceId,dataSource,deviceLog);
            //箱门变化事件
        }else if (CmdId.DOOR_STATE_CHANGE.equals(cmdId)){
            resolveDoorStateChange(deviceId,dataSource,deviceLog);
        }
    }


    /**
     * 处理箱门状态变化事件
     *
     * @param deviceId 设施id
     * @param dataSource 数据源
     * @param deviceLog 设施日志
     */
    private void resolveDoorStateChange(String deviceId, Map<String, Object> dataSource, DeviceLog deviceLog) {
        String doorNum = dataSource.get(ParamsKey.DOOR_NUM).toString();
        String doorLockState = dataSource.get(ParamsKey.DOOR_LOCK_STATE).toString();
        String time = dataSource.get(ParamsKey.TIME).toString();
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.DOOR_STATE_CHANGE);
        //更新门锁状态
        Lock lock = setDoorLockInfo(deviceId, doorNum, doorLockState);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }

    /**
     * 处理关锁上报
     *
     * @param deviceId 设施id
     * @param dataSource 数据源
     * @param deviceLog 设施日志
     */
    private void resolveCloseLockUpload(String deviceId, Map<String, Object> dataSource,
                                        DeviceLog deviceLog) {

        String lockState = dataSource.get(ParamsKey.LOCK_STATE).toString();
        String lockNum = dataSource.get(ParamsKey.LOCK_NUM).toString();
        String time = dataSource.get(ParamsKey.TIME).toString();
        //设置日志信息 todo
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.CLOSE_LOCK_RESULT);
        //更新门锁状态
        Lock lock = setDoorLockInfo(deviceId, lockNum, lockState);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }

    /**
     * 处理开锁响应
     *
     * @param deviceId 设施id
     * @param dataSource 数据源
     * @param deviceLog 设施日志
     */
    private void resolveUnlockResponse(String deviceId, Map<String, Object> dataSource, DeviceLog deviceLog) {
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.OPEN_LOCK);
        List<Map<String, Object>> paramList = (List<Map<String, Object>>) dataSource.get(ParamsKey.PARAMS_KEY);
        if(paramList != null && paramList.size() > 0){
            List<Lock> locks = new ArrayList<>();
            for (Map<String, Object> paramMap : paramList) {
                String slotNum = paramMap.get(ParamsKey.SLOT_NUM).toString();
                String result = paramMap.get(ParamsKey.RESULT).toString();
                //如果成功更新锁状态
                if(SUCCESS_OPEN_LOCK.equals(result)){
                    Lock lock = setLockInfo(deviceId, slotNum, OPEN_LOCK_STATE);
                    locks.add(lock);
                }
            }
            //更新电子锁状态
            lockFeign.updateLockStatus(locks);
        }
    }


    /**
     * 处理休眠事件
     *
     * @param deviceLog 设施日志
     */
    private void resolveSleep(DeviceLog deviceLog) {
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.SLEEP);
    }

    /**
     * 处理开锁上报
     *
     * @param deviceLog  设施日志
     * @param dataSource 数据源
     * @param deviceId   设施id
     */
    private void resolveOpenLockUpload(String deviceId, Map<String, Object> dataSource, DeviceLog deviceLog) {
        String lockState = dataSource.get(ParamsKey.LOCK_STATE).toString();
        String lockNum = dataSource.get(ParamsKey.LOCK_NUM).toString();
        String lockType = dataSource.get(ParamsKey.LOCK_TYPE).toString();
        String time = dataSource.get(ParamsKey.TIME).toString();
        //设置日志信息
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.OPEN_LOCK_RESULT);
        //更新门锁状态
        Lock lock = setDoorLockInfo(deviceId, lockNum, lockState);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }


    /**
     * 处理配置策略响应
     *
     * @param control 主控信息
     */
    private void resolveSetConfig(Control control) {
        //将配置状态更新为同步
        String syncStatus = control.getSyncStatus();
        if (NOT_SYNC_STATE.equals(syncStatus)) {
            control.setSyncStatus(SYNC_STATE);
        }
        controlFeign.updateControlParamsByDeviceId(control);
    }

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志
     * @param logType   设施日志类型
     * @param logName   设施日志名称
     */
    private void saveDeviceLog(DeviceLog deviceLog, String logType, String logName) {
        deviceLog.setLogName(logName);
        deviceLog.setLogType(logType);
        deviceLogFeign.saveDeviceLog(deviceLog);
    }

    /**
     * 解析激活事件元数据
     *
     * @param deviceLog 设施日志
     * @param deviceId   设施id
     * @param dataSource 元数据信息
     */
    private void resolveActive(String deviceId, Map<String, Object> dataSource,DeviceLog deviceLog) {
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.ACTIVE);
        //获取软硬件版本
        String softwareVersion = dataSource.get(ParamsKey.SOFTWARE_VERSION).toString().trim();
        String hardwareVersion = dataSource.get(ParamsKey.HARDWARE_VERSION).toString().trim();
        Control control = new Control();
        control.setHardwareVersion(hardwareVersion);
        control.setSoftwareVersion(softwareVersion);
        control.setDeviceId(deviceId);
        controlFeign.updateControlParamsByDeviceId(control);
    }


    /**
     * 解析参数上报元数据
     *
     * @param deviceLog 设施日志对象
     * @param deviceId   设施id
     * @param control    控制器信息
     * @param dataSource 元数据
     */
    private void resolveParamUpload(String deviceId, Control control, Map<String, Object> dataSource,DeviceLog deviceLog) {
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT, DeviceLogName.PARAMS_UPLOAD);
        //处理参数上报元数据
        String actualValueJson = control.getActualValue();
        Map<String, Object> result;
        //判断主控json是否为空
        if (StringUtils.isEmpty(actualValueJson)) {
            result = new HashMap<>(64);
        } else {
            result = JsonUtils.jsonToMap(actualValueJson);
        }
        //获取参数list集合
        List<Map<String, Object>> paramList = (List<Map<String, Object>>) dataSource.get(ParamsKey.PARAMS_KEY);
        List<Document> alarmMaps = new ArrayList<>();
        for (Map<String, Object> paramMap : paramList) {
            //获取dataClass
            String key = paramMap.get(ParamsKey.DATA_CLASS).toString();
            Object data = paramMap.get(ParamsKey.DATA);
            Object alarmFlag = paramMap.get(ParamsKey.ALARM_FLAG);
            // todo 告警信息
            if (AlarmFlag.ALARM.equals(alarmFlag)) {
                Document document = new Document(paramMap);
                alarmMaps.add(document);
            }
            //未关锁信息,更新到数据库中
            if (ParamsKey.UNLOCK.equals(key) || ParamsKey.NOT_CLOSED.equals(key)) {
                //更新电子锁状态
                batchSaveLockStatus(data, deviceId);
            } else {
                result.put(key, data.toString());
            }
        }
        // todo 告警存入mongo
        mongoTemplate.insertAll(alarmMaps);
        try {
            if (result.size() > 0) {
                //保存主控信息
                String resultJson = JSONObject.toJSONString(result);
                control.setActualValue(resultJson);
                controlFeign.updateControlParamsByDeviceId(control);
            }
        } catch (Exception e) {
            log.info("control params to json failed>>>>>>>>");
        }
    }

    /**
     * 更新电子锁状态
     *
     * @param data     元数据
     * @param deviceId 设施id
     */
    private void batchSaveLockStatus(Object data, String deviceId) {
        List<Lock> locks = new ArrayList<>();
        Map<String, String> dataMap = (Map<String, String>) data;
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            //锁具编号
            String lockNum = entry.getKey();
            //门状态:   锁状态:  0:无效 1:开 2:关
            String status = entry.getValue();
            Lock lock = setDoorLockInfo(deviceId, lockNum, status);
            locks.add(lock);
        }
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }

    /**
     * 设置电子锁状态信息
     *
     * @param deviceId 设施id
     * @param lockNum  锁具编号
     * @param status   门锁状态
     */
    private Lock setLockInfo(String deviceId, String lockNum, String status) {
        Lock lock = new Lock();
        lock.setDeviceId(deviceId);
        lock.setLockNum(lockNum);
        lock.setLockStatus(status);
        return lock;
    }

    /**
     * 设置电子锁和门状态信息
     *
     * @param deviceId 设施id
     * @param lockNum  锁具编号
     * @param status   门锁状态
     */
    private Lock setDoorLockInfo(String deviceId, String lockNum, String status) {
        Lock lock = new Lock();
        String doorStatus = String.valueOf(status.charAt(0));
        String lockStatus = String.valueOf(status.charAt(1));
        lock.setDeviceId(deviceId);
        lock.setLockNum(lockNum);
        lock.setDoorStatus(doorStatus);
        lock.setLockStatus(lockStatus);
        return lock;
    }

    /**
     * 回复响应帧数据
     *
     * @param deviceId 设施id
     * @param cmdId    指令id
     */
    private void sendResponse(String deviceId, String cmdId) {
        FiLinkReqParams fiLinkReqParams = new FiLinkReqParams();
        //设置指令类型
        fiLinkReqParams.setCmdType(CmdType.RESPONSE_TYPE);
        //设置指令id
        fiLinkReqParams.setCmdId(cmdId);
        //设置设施id
        fiLinkReqParams.setDeviceId(deviceId);
        sender.sendInstruct(fiLinkReqParams);
    }
}
