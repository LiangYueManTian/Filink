package com.fiberhome.filink.filinkoceanconnectserver.business;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.constant.*;
import com.fiberhome.filink.commonstation.entity.config.UpgradeConfig;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.websocket.WebSocketUnlockResult;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.utils.AlarmUtil;
import com.fiberhome.filink.commonstation.utils.CrcUtil;
import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.sender.FiLinkOceanConnectSender;
import com.fiberhome.filink.filinkoceanconnectserver.sender.SendUtil;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectI18n;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectUtil;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * filink数据业务处理类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component("fiLinkNewBusinessHandler")
public class FiLinkNewMsgBusinessHandler implements MsgBusinessHandler {

    @Autowired
    private FiLinkOceanConnectSender sender;

    @Autowired
    private LockFeign lockFeign;

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private DeviceLogFeign deviceLogFeign;

    @Autowired
    private FiLinkKafkaSender streamSender;

    @Autowired
    private SendUtil sendUtil;

    @Autowired
    private ParameterFeign parameterFeign;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Value("${constant.ftpFilePath}")
    private String ftpFilePath;

    @Value("${constant.tmpDirPath}")
    private String tmpDirPath;

    @Value("${constant.zipPassword}")
    private String zipPassword;

    @Value("${constant.maxUpgradeLen}")
    private Integer maxUpgradeLen;

    @Value("${constant.maxUpgradeCount}")
    private Integer maxUpgradeCount;


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
        Integer cmdOk = outputParams.getCmdOk();
        Integer cmdType = Integer.parseInt(outputParams.getCmdType());
        //判断是否是错误应答
        if (CmdType.RESPONSE_TYPE == cmdType && cmdOk != CmdOk.SUCCESS) {
            log.info("[receiveMsg-business] : the equipmentId : {} cmd : {} response is error", equipmentId, cmdId);
            //移除重发指令中的上一个升级包指令
            String key = equipmentId + Constant.SEPARATOR + serialNumber;
            RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
            return;
        }
        //查询该设施主控信息
        ControlParam control = outputParams.getControlParam();
        String deviceId = control.getDeviceId();
        //更新主控激活状态
        updateControlActiveStatus(control);
        //下发设备缓存开锁命令
        if (!CmdId.UNLOCK.equals(cmdId)) {
            executeUnlockCmd(equipmentId);
        }
        //判断部署状态是否是部署中,如果是则进行部署状态下发
        updateDeployStatus(equipmentId, control);
        //刷新主控心跳时间
        commonUtil.refreshHeatBeatTime(equipmentId, control);
        //更新设施状态为正常
        updateDeviceStatusNormal(equipmentId, control, deviceId);
        String key = equipmentId + Constant.SEPARATOR + cmdType;
        //过滤相同流水号的指令
        if (!CmdId.FILE_UPLOAD.equals(cmdId) && commonUtil.filterRepeatCmd(key, serialNumber)) {
            log.info("[receiveMsg-business] : the control : {} ,cmd : {} is repeat", equipmentId, cmdId);
            return;
        }
        Map<String, Object> dataSource = outputParams.getParams();
        DeviceLog deviceLog = commonUtil.setDeviceLogCommonInfo(control);
        //根据不同指令进行不同处理
        handleCmd(cmdId, deviceId, equipmentId, dataSource, control, deviceLog, serialNumber);
    }

    /**
     * 更新主控激活状态
     *
     * @param control 主控信息
     */
    private void updateControlActiveStatus(ControlParam control) {
        String activeStatus = control.getActiveStatus();
        if (!Constant.ACTIVE_STATUS.equals(activeStatus)) {
            //更新数据库主控状态
            ControlParam controlParam = new ControlParam();
            controlParam.setHostId(control.getHostId());
            controlParam.setActiveStatus(Constant.ACTIVE_STATUS);
            Result result = controlFeign.updateControlParam(controlParam);
            if (result != null && result.getCode() == 0) {
                //更新redis主控信息
                control.setActiveStatus(Constant.ACTIVE_STATUS);
                updateRedisControl(control);
            }
        }
    }

    /**
     * 下发设备缓存开锁命令
     *
     * @param equipmentId 设备id
     */
    private void executeUnlockCmd(String equipmentId) {
        String key = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        if (RedisUtils.hasKey(key)) {
            Map<Object, Object> cmdMap = RedisUtils.hGetMap(key);
            for (Map.Entry<Object, Object> cmdEntry : cmdMap.entrySet()) {
                Map<String, Object> dataMap = (Map<String, Object>) cmdEntry.getValue();
                //获取数据包
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
        if (!StringUtils.isEmpty(dataMap)) {
            log.info("[receiveMsg-business] : send deploy cmd : {}", equipmentId);
            FiLinkReqOceanConnectParams oceanConnectParams = new FiLinkReqOceanConnectParams();
            oceanConnectParams.setAppId(control.getProductId());
            oceanConnectParams.setOceanConnectId(control.getPlatformId());
            sendUtil.sendData(oceanConnectParams, dataMap.get(ParamsKey.HEX_DATA).toString());
        }
    }

    /**
     * 根据不同指令进行不同处理
     *
     * @param cmdId        命令id
     * @param deviceId     设施id
     * @param equipmentId  装置id
     * @param dataSource   元数据
     * @param control      主控信息
     * @param deviceLog    设施日志
     * @param serialNumber 流水号
     */
    private void handleCmd(String cmdId, String deviceId, String equipmentId, Map<String, Object> dataSource,
                           ControlParam control, DeviceLog deviceLog, Integer serialNumber) {
        switch (cmdId) {
            //开锁响应帧
            case CmdId.UNLOCK:
                resolveUnlockResponse(dataSource, equipmentId, serialNumber);
                break;
            //参数上报
            case CmdId.PARAMS_UPLOAD:
                resolveParamUpload(deviceId, control, dataSource, equipmentId, serialNumber);
                break;
            //激活事件
            case CmdId.ACTIVE:
                resolveActive(equipmentId, control, dataSource, deviceLog, serialNumber);
                break;
            //配置设施策略
            case CmdId.SET_CONFIG:
                resolveSetConfig(equipmentId, serialNumber);
                break;
            //休眠事件
            case CmdId.SLEEP:
                resolveSleep(control, dataSource, deviceLog, equipmentId, serialNumber);
                break;
            //开锁事件
            case CmdId.OPEN_LOCK_UPLOAD:
                resolveOpenLockUpload(control, dataSource, deviceLog, equipmentId, serialNumber);
                break;
            //关锁事件
            case CmdId.CLOSE_LOCK_UPLOAD:
                resolveCloseLockUpload(control, dataSource, deviceLog, equipmentId, serialNumber);
                break;
            //箱门变化事件
            case CmdId.DOOR_STATE_CHANGE:
                resolveDoorStateChange(control, dataSource, deviceLog, equipmentId, serialNumber);
                break;
            //文件类信息通知
            case CmdId.FILE_ADVISE:
                sendNullDataResponse(control, cmdId, serialNumber);
                resolveFileAdvise(equipmentId, dataSource);
                break;
            //文件数据上传
            case CmdId.FILE_UPLOAD:
                resolveFileUpload(control, deviceId, dataSource, equipmentId, serialNumber);
                break;
            //升级成功事件
            case CmdId.UPGRADE_SUCCESS:
                resolveUpgradeSuccess(control, dataSource, deviceLog, equipmentId, serialNumber);
                break;
            //电子锁升级通知
            case CmdId.UPGRADE_ADVISE:
                resolveData(equipmentId, control, serialNumber, dataSource);
                break;
            //电子锁升级数据包
            case CmdId.UPGRADE_DATA:
                resolveData(equipmentId, control, serialNumber, dataSource);
                break;
            //部署状态
            case CmdId.DEPLOY_STATUS:
                resolveDeployStatus(equipmentId, serialNumber, dataSource);
                break;
            //心跳指令
            case CmdId.HEART_BEAT:
                resolveHeartBeat();
                break;
            default:
                log.info("cmd id: " + cmdId + " is not support>>>>>>>>>>>>>>");
        }
    }

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
            log.error("device status is null : {}", equipmentId);
            return;
        }
        if (offlineCode.equals(deviceStatus)
                || outContactCode.equals(deviceStatus)
                || unConfiguredCode.equals(deviceStatus)) {
            //将状态更新为正常
            Result result = updateDeviceStatus(equipmentId, normalCode);
            if (result != null && result.getCode() == 0) {
                //更新redis主控状态
                control.setDeviceStatus(normalCode);
                updateRedisControl(control);
            }
            //发送通信中断告警恢复
            Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.CANCEL_ALARM);
            alarmMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
            streamSender.sendAlarm(alarmMap);
            log.info("[receiveMsg-business] : the control {] recover communicate alarm", equipmentId);
        }
    }

    /**
     * 更新主控状态
     *
     * @param equipmentId 主控id
     * @param status      状态
     */
    private Result updateDeviceStatus(String equipmentId, String status) {
        ControlParam controlParam = new ControlParam();
        controlParam.setHostId(equipmentId);
        controlParam.setDeviceStatus(status);
        return controlFeign.updateDeviceStatusById(controlParam);
    }


    /**
     * 心跳处理
     */
    private void resolveHeartBeat() {
        log.info("[receiveMsg-heartBeat]");
    }


    /**
     * 部署状态处理
     *
     * @param equipmentId         设施id
     * @param currentSerialNumber 流水号
     * @param dataSource          元数据
     */
    private void resolveDeployStatus(String equipmentId, Integer currentSerialNumber, Map<String, Object> dataSource) {
        //获取缓存中是否有部署状态指令
        Map<String, Object> dataMap = (Map<String, Object>) RedisUtils.hGet(RedisKey.DEPLOY_CMD, equipmentId);
        if (dataMap == null || dataMap.size() == 0) {
            log.error("[receiveMsg-deploy] : the control {} deploy status dataMap is null", equipmentId);
            return;
        }
        //获取指令发送时流水号
        Integer serialNumber = (Integer) dataMap.get(ParamsKey.SERIAL_NUMBER);
        //如果缓存中有该指令,则进行更新状态操作
        if (currentSerialNumber >= serialNumber) {
            //获取部署状态
            String deployStatus = dataSource.get(ParamsKey.DEPLOY_STATUS).toString();
            //更新部署状态
            ControlParam controlParam = new ControlParam();
            controlParam.setHostId(equipmentId);
            controlParam.setDeployStatus(deployStatus);
            controlFeign.updateDeployStatusById(controlParam);
            //清除缓存该指令
            //拼接key值
            OceanConnectUtil.clearDeployCmd(equipmentId);
            log.info("[receiveMsg-deploy] : the control {} deploy success", equipmentId);
        } else {
            log.error("[receiveMsg-deploy] : the control {} deploy cmd currentSerialNum {} ,cmd serialNum {}",
                    equipmentId, currentSerialNumber, serialNumber);
        }
    }


    /**
     * 计算升级包数据
     *
     * @param equipmentId  设施序列id
     * @param controlParam 主控信息
     * @param dataSource   元数据
     * @param serialNumber 流水号
     */
    private void resolveData(String equipmentId, ControlParam controlParam, Integer serialNumber, Map<String, Object> dataSource) {
        //移除重发指令中的上一个升级包指令
        String key = equipmentId + Constant.SEPARATOR + serialNumber;
        RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
        //获取redis中数据包信息
        Map upgradeMap = (Map) RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, equipmentId);
        if (upgradeMap == null) {
            log.error("[receiveMsg-upgrade] : the control {} upgrade info is null in the redis", equipmentId);
            return;
        }
        //收到电子锁升级控制响应,发送升级数据包
        //获取续传包序号
        String continuePackageNum = (String) dataSource.get(ParamsKey.CONTINUE_PACKAGE_NUM);
        if (StringUtils.isEmpty(continuePackageNum)) {
            log.error("[receiveMsg-upgrade] : the control {} continuePackageNum is null", equipmentId);
            return;
        }
        int packageNum = Integer.parseInt(continuePackageNum);
        //获取总包数
        int packageSum = Integer.parseInt(upgradeMap.get(ParamsKey.PACKAGE_SUM).toString());
        //如果这是最后一个包,则返回
        if (packageNum > packageSum) {
            //更新缓存中升级设施数量
            OceanConnectUtil.deleteUpgradeDeviceCount(equipmentId);
            log.info("[receiveMsg-upgrade] : the control {} send the last package {}", equipmentId, packageNum);
            return;
        }
        //获取文件唯一标识
        String fileKey = upgradeMap.get(ParamsKey.FILE_KEY).toString();
        //根据当前包序号获取文件
        String packageData = null;
        try {
            packageData = getCurrentFileData(packageNum, packageSum, fileKey, maxUpgradeLen, controlParam);
        } catch (Exception e) {
            log.error("[receiveMsg-upgrade] : the control {} get current file : {} data failed, {}",
                    equipmentId, continuePackageNum, e);
            return;
        }
        //获取crc
        String crc = getCrcData(packageData);
        //构造升级包参数
        Map<String, Object> params = new HashMap<>(64);
        //升级包序号
        params.put(ParamsKey.PACKAGE_NUM, packageNum);
        //数据包长度
        params.put(ParamsKey.PACKAGE_LEN, packageData.length() / 2);
        //数据内容
        params.put(ParamsKey.PACKAGE_DATA, packageData);
        //CRC
        params.put(ParamsKey.PACKAGE_CRC, crc);
        //发送升级数据包指令
        sendRequest(equipmentId, CmdId.UPGRADE_DATA, params, controlParam);
        log.info("[receiveMsg-upgrade] : the control {} current packageNum {}", equipmentId, packageNum);
        //更新redis中升级包数据
        upgradeMap.put(ParamsKey.PACKAGE_NUM, String.valueOf(packageNum + 1));
        upgradeMap.put(ParamsKey.PACKAGE_CRC, crc);
        RedisUtils.hSet(RedisKey.DEVICE_UPGRADE, equipmentId, upgradeMap);
    }


    /**
     * 获取当前包序号数据
     *
     * @param maxUpgradeData 每个包最大数量
     * @param fileKey        文件唯一标识
     * @param packageNum     当前包序号
     * @param packageSum     总包数
     * @param controlParam   主控信息
     * @return 包数据
     */
    private String getCurrentFileData(int packageNum, int packageSum, String fileKey, int maxUpgradeData, ControlParam controlParam) {
        String softwareVersion = controlParam.getSoftwareVersion();
        //截取软件版本
        String software = softwareVersion.substring(0, softwareVersion.indexOf("."));
        //从redis获取文件信息
        String key = RedisKey.UPGRADE_FILE_PREFIX + fileKey;
        UpgradeConfig upgradeConfig = (UpgradeConfig) RedisUtils.get(key);
        if (upgradeConfig == null) {
            log.info("upgrade file is null , get file from ftp>>>");
            upgradeConfig = getUpgradeFileFromFtp(software);
            RedisUtils.set(key, upgradeConfig);
        }
        String hexBinFile = upgradeConfig.getHexBinFile();
        String hexData;
        if (packageNum == packageSum) {
            hexData = hexBinFile.substring((packageNum - 1) * maxUpgradeData * 2);
        } else {
            hexData = hexBinFile.substring((packageNum - 1) * maxUpgradeData * 2, packageNum * maxUpgradeData * 2);
        }
        return hexData;
    }

    /**
     * 从ftp获取升级包
     *
     * @param software 文件名
     * @return 升级包信息
     */
    private UpgradeConfig getUpgradeFileFromFtp(String software) {
        //拼接升级包文件路径
        String filePath = ftpFilePath + software + Constant.UPGRADE_FILE_SUFFIX;
        UpgradeConfig upgradeConfig = new UpgradeConfig();
        FtpSettings ftpSettings = parameterFeign.queryFtpSettings();
        DeviceUpgradeUtil.setUpgradeConfig(ftpSettings, zipPassword, filePath, tmpDirPath, upgradeConfig);
        if (StringUtils.isEmpty(upgradeConfig.getDependentHardVersion())
                || StringUtils.isEmpty(upgradeConfig.getDependentSoftVersion())
                || StringUtils.isEmpty(upgradeConfig.getHexBinFile())) {
            log.error("dependentHardVersion or dependentSoftVersion or hexBinFile is null>>>>>>>>>>>>");
            throw new ResponseException("upgrade file is null>>>>>>>>");
        }
        return upgradeConfig;
    }

    /**
     * 根据文件16进制获取crc
     *
     * @param packageData 16进制数据包
     * @return crc
     */
    private String getCrcData(String packageData) {
        //获取文件字节数组
        byte[] packageDataBytes = HexUtil.hexStringToByte(packageData);
        //根据字节数组获取crc
        return CrcUtil.calcCrc8(packageDataBytes);
    }


    /**
     * 升级成功事件处理
     *
     * @param dataSource   元数据
     * @param deviceLog    设施日志
     * @param equipmentId  主控id
     * @param serialNumber 流水号
     * @param control      主控信息
     */
    private void resolveUpgradeSuccess(ControlParam control, Map<String, Object> dataSource,
                                       DeviceLog deviceLog, String equipmentId, Integer serialNumber) {
        //事件时间
        String time = dataSource.get(ParamsKey.TIME).toString();
        long currentTime = Long.parseLong(time) * 1000;
        deviceLog.setCurrentTime(currentTime);
        //发送回包
        sendNullDataResponse(control, CmdId.UPGRADE_SUCCESS, serialNumber);
        //记录日志信息
        String upgradeResult = dataSource.get(ParamsKey.UPGRADE_RESULT).toString();
        //升级方式
        String upgradeTypeMsg;
        String upgradeType = (String) dataSource.get(ParamsKey.UPGRADE_TYPE);
        switch (upgradeType) {
            case Constant.PDA_UPGRADE:
                upgradeTypeMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.PDA_UPGRADE);
                break;
            case Constant.PLATFORM_UPGRADE:
                upgradeTypeMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.PLATFORM_UPGRADE);
                break;
            case Constant.OCEAN_CONNECT_UPGRADE:
                upgradeTypeMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.OCEAN_CONNECT_UPGRADE);
                break;
            case Constant.ONE_NET_UPGRADE:
                upgradeTypeMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.ONE_NET_UPGRADE);
                break;
            default:
                log.info("[receiveMsg-upgradeEvent] : the control {} upgradeType {} is error", equipmentId, upgradeType);
                upgradeTypeMsg = "";
        }
        //记录升级结果
        String upgradeResultMsg;
        if (Constant.UPGRADE_SUCCESS.equals(upgradeResult)) {
            upgradeResultMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UPGRADE_SUCCESS);
        } else {
            upgradeResultMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UPGRADE_FAIL);
        }
        deviceLog.setRemarks(upgradeTypeMsg + upgradeResultMsg);
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.UPGRADE_SUCCESS_EVENT),
                DeviceEventType.UPGRADE_SUCCESS_EVENT);
        //判断是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.UPGRADE_SUCCESS, currentTime)) {
            log.info("[receiveMsg-upgradeEvent] : the cmd of the control {} is not the newest", equipmentId);
            return;
        }
        //软硬件版本信息
        String softwareVersion = (String) dataSource.get(ParamsKey.SOFTWARE_VERSION);
        String hardwareVersion = (String) dataSource.get(ParamsKey.HARDWARE_VERSION);
        //更新主控软硬件更新时间
        control.setSoftwareVersion(softwareVersion.trim());
        control.setHardwareVersion(hardwareVersion.trim());
        control.setVersionUpdateTime(Long.parseLong(time) * 1000);
        //更新主控信息
        ControlParam controlParam = new ControlParam();
        controlParam.setSoftwareVersion(softwareVersion.trim());
        controlParam.setHardwareVersion(hardwareVersion.trim());
        controlParam.setVersionUpdateTime(Long.parseLong(time) * 1000);
        controlParam.setHostId(control.getHostId());
        Result result = controlFeign.updateControlParam(control);
        if (result != null && result.getCode() == 0) {
            //更新redis主控信息
            updateRedisControl(control);
        }
        log.info("[receiveMsg-upgradeEvent] : the control {} upgrade success , upgradeType {} , upgradeResult",
                equipmentId, upgradeType, upgradeResult);
    }


    /**
     * 处理图片数据上传
     *
     * @param control      主控信息
     * @param deviceId     设施id
     * @param dataSource   元数据
     * @param equipmentId  主控id
     * @param serialNumber 流水号
     */
    private void resolveFileUpload(ControlParam control, String deviceId, Map<String, Object> dataSource,
                                   String equipmentId, Integer serialNumber) {
        //照片索引
        String index = dataSource.get(ParamsKey.INDEX).toString();
        //分包总数
        int packageSum = Integer.parseInt(dataSource.get(ParamsKey.PACKAGE_SUM).toString());
        //包序号
        String packageNum = dataSource.get(ParamsKey.PACKAGE_NUM).toString();
        //校验位
        String check = dataSource.get(ParamsKey.CHECK_NUM).toString();
        //包数据内容
        String dataContent = dataSource.get(ParamsKey.PACKAGE_DATA).toString();
        String selfCheck = makeChecksum(dataContent);
        if (selfCheck.equals(check)) {
            //发送回包
            sendNullDataResponse(control, CmdId.FILE_UPLOAD, serialNumber);
        } else {
            log.error("the equipment : " + equipmentId + " ,picNum : " + packageNum + " index : " + index + " data is error>>>>>");
        }
        //拼接redis中key值
        String key = RedisKey.PIC_UPLOAD + Constant.SEPARATOR + equipmentId + Constant.SEPARATOR + index;
        //从redis中获取文件包
        RedisUtils.hSet(key, ParamsKey.INDEX, index);
        RedisUtils.hSet(key, ParamsKey.PACKAGE_SUM, packageSum);
        RedisUtils.hSet(key, packageNum, dataContent);
        Map<String, Object> picMap = (Map) RedisUtils.hGetMap(key);
        log.info("current picNum : {} ,size : {}", packageNum, picMap.size());
        int count = 2;
        //判断包是否全部传输完毕
        if (picMap.size() - count == packageSum) {
            //拼接图片文件
            mergePic(equipmentId, index, packageSum, picMap, deviceId);
            //清除redis中图片信息
            RedisUtils.remove(key);
            //清除redis中图片基本信息
            String adviseKey = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + equipmentId;
            RedisUtils.hRemove(adviseKey, index);
        }
    }


    /**
     * 校验图片是否正确
     *
     * @param data 传入参数
     * @return 校验后结果
     */
    private String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        //用256求余最大是255，即16进制的FF
        return String.valueOf(total % 256);
    }

    /**
     * 合并图片文件
     *
     * @param equipmentId 主控id
     * @param index       照片索引
     * @param packageSum  总包数
     * @param picMap      包数据
     * @param deviceId    设施id
     */
    private void mergePic(String equipmentId, String index, int packageSum, Map<String, Object> picMap, String deviceId) {
        //从redis获取该文件基本信息
        String adviseKey = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + equipmentId;
        Map<String, String> adviseMap = (Map<String, String>) RedisUtils.hGet(adviseKey, index);
        if (adviseMap == null) {
            throw new ResponseException("device pic info is not exist>>>>>>>>");
        }
        StringBuilder fileData = new StringBuilder();
        for (int i = 1; i <= packageSum; i++) {
            Object packageData = picMap.get(String.valueOf(i));
            if (StringUtils.isEmpty(packageData)) {
                log.error("the pic num : " + i + " data is null>>>>>>>");
                continue;
            }
            //整合文件16进制数据包
            fileData.append(packageData.toString());
        }
        log.info("receive pic : " + fileData.toString());
        //将图片放到map
        adviseMap.put(ParamsKey.PIC_DATA, fileData.toString());
        //将设施id放到map中
        adviseMap.put(ParamsKey.DEVICE_ID, deviceId);
        //将图片发送到告警图片队列
        streamSender.sendAlarmPic(adviseMap);
    }

    /**
     * 处理文件类信息通知
     *
     * @param equipmentId 主控id
     * @param dataSource  元数据
     */
    private void resolveFileAdvise(String equipmentId, Map<String, Object> dataSource) {
        Map<String, String> adviseMap = new HashMap<>(64);
        //照片索引
        String index = dataSource.get(ParamsKey.INDEX).toString();
        //文件格式
        String fileFormat = dataSource.get(ParamsKey.FILE_FORMAT).toString();
        adviseMap.put(ParamsKey.FILE_FORMAT, fileFormat);
        //文件数据大小
        String dataSize = dataSource.get(ParamsKey.DATA_SIZE).toString();
        adviseMap.put(ParamsKey.DATA_SIZE, dataSize);
        //门编号
        String doorNum = dataSource.get(ParamsKey.DOOR_NUM).toString();
        adviseMap.put(ParamsKey.DOOR_NUM, doorNum);
        //文件类型
        String fileType = dataSource.get(ParamsKey.FILE_TYPE).toString();
        adviseMap.put(ParamsKey.FILE_TYPE, fileType);
        //告警编码
        String alarmCode = dataSource.get(ParamsKey.ALARM_CODE).toString();
        adviseMap.put(ParamsKey.DATA_CLASS, alarmCode);
        //图片时间
        Long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString());
        adviseMap.put(ParamsKey.TIME, String.valueOf(time * 1000));
        //拼接redis中key值
        String key = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + equipmentId;
        RedisUtils.hSet(key, index, adviseMap);
    }


    /**
     * 处理箱门状态变化事件
     *
     * @param controlParam 主控信息
     * @param dataSource   数据源
     * @param deviceLog    设施日志
     * @param equipmentId  主控id
     * @param serialNumber 流水号
     */
    private void resolveDoorStateChange(ControlParam controlParam, Map<String, Object> dataSource,
                                        DeviceLog deviceLog, String equipmentId, Integer serialNumber) {
        long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendNullDataResponse(controlParam, CmdId.DOOR_STATE_CHANGE, serialNumber);
        String doorNum = dataSource.get(ParamsKey.DOOR_NUM).toString();
        String doorState = dataSource.get(ParamsKey.DOOR_STATE).toString();
        //拼接附加信息
        if (Constant.OPEN_STATE.equals(doorState)) {
            setRemarks(deviceLog, doorNum, systemLanguageUtil.getI18nString(OceanConnectI18n.OPEN_DOOR_MSG));
        } else {
            setRemarks(deviceLog, doorNum, systemLanguageUtil.getI18nString(OceanConnectI18n.CLOSE_DOOR_MSG));
        }
        //设置时间
        deviceLog.setCurrentTime(time);
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.DOOR_STATE_CHANGE_EVENT),
                DeviceEventType.DOOR_STATE_CHANGE_EVENT);
        //判断是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.DOOR_STATE_CHANGE, time)) {
            log.info("[receiveMsg-doorChangeEvent] : the cmd of the control {} is not the newest", equipmentId);
            return;
        }
        //更新门状态
        Lock lock = setDoorInfo(equipmentId, doorNum, doorState, time);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }


    /**
     * 处理关锁上报
     *
     * @param serialNumber 流水号
     * @param dataSource   数据源
     * @param deviceLog    设施日志
     * @param equipmentId  主控id
     * @param controlParam 主控信息
     */
    private void resolveCloseLockUpload(ControlParam controlParam, Map<String, Object> dataSource,
                                        DeviceLog deviceLog, String equipmentId, Integer serialNumber) {
        long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendNullDataResponse(controlParam, CmdId.CLOSE_LOCK_UPLOAD, serialNumber);
        String lockState = dataSource.get(ParamsKey.LOCK_STATE).toString();
        String lockNum = dataSource.get(ParamsKey.LOCK_NUM).toString();
        //拼接附加信息
        setRemarks(deviceLog, lockNum, systemLanguageUtil.getI18nString(OceanConnectI18n.CLOSE_LOCK_MSG));
        //设置时间
        deviceLog.setCurrentTime(time);
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.CLOSE_LOCK_EVENT),
                DeviceEventType.CLOSE_LOCK_EVENT);
        //判断是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.CLOSE_LOCK_UPLOAD, time)) {
            log.info("[receiveMsg-closeLockEvent] : the cmd of the control {} is not the newest", equipmentId);
            return;
        }
        //更新门锁状态
        Lock lock = setDoorLockInfo(equipmentId, lockNum, lockState, time);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }

    /**
     * 处理开锁响应
     *
     * @param dataSource   数据源
     * @param serialNumber 流水号
     * @param equipmentId  主控id
     */
    private void resolveUnlockResponse(Map<String, Object> dataSource, String equipmentId, Integer serialNumber) {
        //清除redis中重发指令
        String key = equipmentId + Constant.SEPARATOR + serialNumber;
        RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
        String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        RedisUtils.hRemove(unlockKey, key);
        List<Map<String, Object>> paramList = (List<Map<String, Object>>) dataSource.get(ParamsKey.PARAMS_KEY);
        if (paramList != null && paramList.size() > 0) {
            //拼接推送提示信息
            StringBuilder stringBuilder = new StringBuilder();
            String msg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_RESULT);
            stringBuilder.append(msg);
            //网管推送消息
            List<WebSocketUnlockResult> unlockResults = new ArrayList<>();
            for (Map<String, Object> paramMap : paramList) {
                WebSocketUnlockResult unlockResult = new WebSocketUnlockResult();
                //门编号
                String slotNum = paramMap.get(ParamsKey.SLOT_NUM).toString();
                //结果
                String result = paramMap.get(ParamsKey.RESULT).toString();
                String suffix;
                unlockResult.setDoorNum(slotNum);
                unlockResult.setUnlockResult(result);
                suffix = getUnlockResultString(slotNum, result);
                unlockResults.add(unlockResult);
                stringBuilder.append(suffix);
            }
            //组装result
            Result result = ResultUtils.success(WebSocketCode.NLOCKED_RESULT, null, unlockResults);
            String socketMsg = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")).toString();
            Result appResult = ResultUtils.success(ResultCode.SUCCESS, socketMsg, unlockResults);
            socketMsg = JSONObject.toJSONString(appResult);
            commonUtil.unLockMessagePush(equipmentId, socketMsg, key, result);
        }
    }

    /**
     * 获取拼接开锁结果信息
     *
     * @param slotNum 门编号
     * @param result  开锁结果
     * @return 开锁结果信息
     */
    private String getUnlockResultString(String slotNum, String result) {
        String unlockMsg;
        String suffix;
        switch (result) {
            //成功信息
            case LockResult.SUCCESS:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_SUCCESS);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.KEY_NOT_CONFIG:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_KEY_NOT_CONFIG);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.PARAMETER_ERROR:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_PARAMETER_ERROR);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.LOCK_ON:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_LOCK_ON);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.ACTIVE_CODE_ERROR:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_ACTIVE_CODE_ERROR);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.LOCK_NOT_CONFIG:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_LOCK_NOT_CONFIG);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.OTHER:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_OTHER);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.REPEAT_CMD:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_REPEAT_CMD);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            case LockResult.STOP:
                unlockMsg = systemLanguageUtil.getI18nString(OceanConnectI18n.UNLOCK_STOP);
                suffix = slotNum + ":" + unlockMsg + ",";
                break;
            default:
                log.warn("lock result is unknown");
                suffix = "";
                break;
        }
        return suffix;
    }

    /**
     * 处理休眠事件
     *
     * @param deviceLog    设施日志
     * @param control      主控信息
     * @param dataSource   元数据
     * @param equipmentId  主控id
     * @param serialNumber 流水号
     */
    private void resolveSleep(ControlParam control, Map<String, Object> dataSource,
                              DeviceLog deviceLog, String equipmentId, Integer serialNumber) {
        //重置redis中流水号
        commonUtil.clearRedisSerialNum(equipmentId);
        //删除升级设施数量
        OceanConnectUtil.deleteUpgradeDeviceCount(equipmentId);
        //删除图片信息
        OceanConnectUtil.deletePicMap(equipmentId);
        //时间
        long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendNullDataResponse(control, CmdId.SLEEP, serialNumber);
        deviceLog.setCurrentTime(time);
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.SLEEP_EVENT),
                DeviceEventType.SLEEP_EVENT);
        //判断时间是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.SLEEP, time)) {
            log.info("[receiveMsg-sleepEvent] : the cmd of the control {} is not the newest", equipmentId);
            return;
        }
        //更新主控时间
        control.setCurrentTime(time);
        //更新传感值
        updateSleepControlInfo(control, dataSource);
        //更新门锁状态
        updateDoorLockState(equipmentId, dataSource, time);
    }


    /**
     * 处理开锁上报
     *
     * @param control      主控信息
     * @param deviceLog    设施日志
     * @param dataSource   数据源
     * @param serialNumber 流水号
     * @param equipmentId  主控id
     */
    private void resolveOpenLockUpload(ControlParam control, Map<String, Object> dataSource, DeviceLog deviceLog,
                                       String equipmentId, Integer serialNumber) {
        //时间
        long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendNullDataResponse(control, CmdId.OPEN_LOCK_UPLOAD, serialNumber);
        String lockState = dataSource.get(ParamsKey.LOCK_STATE).toString();
        String lockNum = dataSource.get(ParamsKey.LOCK_NUM).toString();
        //拼接附加信息
        setRemarks(deviceLog, lockNum, systemLanguageUtil.getI18nString(OceanConnectI18n.OPEN_LOCK_MSG));
        //设置日志信息
        deviceLog.setCurrentTime(time);
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.OPEN_LOCK_EVENT),
                DeviceEventType.OPEN_LOCK_EVNET);
        //判断是否需要处理
        if (isNeedToResolve(equipmentId, CmdId.OPEN_LOCK_UPLOAD, time)) {
            log.info("[receiveMsg-unlockEvent] : the cmd of the control {} is not the newest", equipmentId);
            return;
        }
        //更新门锁状态
        Lock lock = setDoorLockInfo(equipmentId, lockNum, lockState, time);
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }


    /**
     * 拼接设施日志附加信息
     *
     * @param deviceLog 设施日志对象
     * @param lockNum   门编号
     * @param msg       事件信息
     */
    private void setRemarks(DeviceLog deviceLog, String lockNum, String msg) {
        String remarks = msg.replace(Constant.REMARK_PLACEHOLDER, lockNum);
        deviceLog.setRemarks(remarks);
    }


    /**
     * 处理配置策略响应
     *
     * @param equipmentId         主控id
     * @param currentSerialNumber 流水号
     */
    private void resolveSetConfig(String equipmentId, Integer currentSerialNumber) {
        //清除redis中重发指令
        String key = equipmentId + CmdId.SET_CONFIG;
        Map<String, Object> dataMap = (Map<String, Object>) RedisUtils.hGet(RedisKey.CMD_RESEND_BUFFER, key);
        if (dataMap == null || dataMap.size() == 0) {
            log.error("[receiveMsg-setConfig] : the control {} dataMap is null", equipmentId);
            return;
        }
        //获取流水号
        Integer serialNumber = (Integer) dataMap.get(ParamsKey.SERIAL_NUMBER);
        //如果当前流水号大于或等于请求帧流水号,则将该命令移除
        if (currentSerialNumber >= serialNumber) {
            //将同步状态改为同步
            ControlParam controlParam = new ControlParam();
            controlParam.setHostId(equipmentId);
            controlParam.setSyncStatus(Constant.SYNC);
            controlFeign.updateControlParam(controlParam);
            RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
            log.info("[receiveMsg-setConfig] : the control {} set config success", equipmentId);
        } else {
            log.error("[receiveMsg-setConfig] : currentSerialNum {} ,cmd serialNum {}", currentSerialNumber, serialNumber);
        }
    }

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志
     * @param logType   设施日志类型
     * @param logName   设施日志名称
     * @param type      事件类型
     */
    private void saveDeviceLog(DeviceLog deviceLog, String logType, String logName, String type) {
        deviceLog.setLogName(logName);
        deviceLog.setLogType(logType);
        deviceLog.setType(type);
        deviceLogFeign.saveDeviceLog(deviceLog);
    }

    /**
     * 解析激活事件元数据
     *
     * @param deviceLog    设施日志
     * @param equipmentId  主控id
     * @param control      主控信息
     * @param dataSource   元数据信息
     * @param serialNumber 流水号
     */
    private void resolveActive(String equipmentId, ControlParam control,
                               Map<String, Object> dataSource, DeviceLog deviceLog, Integer serialNumber) {
        //消息时间
        long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendActiveResponse(equipmentId, control, serialNumber);
        //唤醒原因
        String rebootReason = dataSource.get(ParamsKey.REBOOT_REASON).toString();
        deviceLog.setCurrentTime(time);
        //保存设施日志
        saveDeviceLog(deviceLog, DeviceLogType.EVENT,
                systemLanguageUtil.getI18nString(DeviceLogNameI18n.ACTIVE_EVENT),
                DeviceEventType.ACTIVE_EVENT);
        if (isNeedToResolve(equipmentId, CmdId.ACTIVE, time)) {
            log.info("[receiveMsg-active] : the control : {} ,cmd : {} is not the newest", equipmentId, CmdId.ACTIVE);
            return;
        }
        //获取软硬件版本
        String softwareVersion = dataSource.get(ParamsKey.SOFTWARE_VERSION).toString().trim();
        String hardwareVersion = dataSource.get(ParamsKey.HARDWARE_VERSION).toString().trim();
        //获取imei imsi号
        String imei = dataSource.get(ParamsKey.IMEI).toString().trim();
        String imsi = dataSource.get(ParamsKey.IMSI).toString().trim();
        //更新传感值
        control.setHardwareVersion(hardwareVersion);
        control.setSoftwareVersion(softwareVersion);
        control.setImei(imei);
        control.setImsi(imsi);
        //更新主控时间
        control.setCurrentTime(time);
        //更新主控信息
        updateActiveControlInfo(control, dataSource);
        //更新门锁状态
        updateDoorLockState(equipmentId, dataSource, time);
        //判断是否需要升级
        isNeedUpgrade(equipmentId, rebootReason, control);
    }


    /**
     * 判断是否需要升级
     *
     * @param equipmentId  设施序列id
     * @param rebootReason 唤醒原因
     * @param controlParam 主控信息
     */
    private void isNeedUpgrade(String equipmentId, String rebootReason, ControlParam controlParam) {
        //判断是否是自启动
        if (RebootReason.START_SELF.equals(rebootReason)) {
            //判断是否超过最大升级数量
            Set<String> deviceUpgradeSet = (Set<String>) RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
            if (deviceUpgradeSet != null && deviceUpgradeSet.size() >= maxUpgradeCount) {
                log.info("[receiveMsg-active] : the control {} over the max upgrade device count", equipmentId);
                return;
            }
            String softwareVersion = controlParam.getSoftwareVersion();
            //截取软件版本
            String software = softwareVersion.substring(0, softwareVersion.indexOf("."));
            //查询是否有新版本升级包
            UpgradeConfig upgradeConfig = new UpgradeConfig();
            if (checkUpgradeFile(controlParam, upgradeConfig)) {
                log.info("[receiveMsg-active] : the control {} is not need to upgrade", equipmentId);
                return;
            }
            int packageSum = getPackageSum(upgradeConfig.getHexBinFile(), maxUpgradeLen);
            Map<String, String> upgradeMap = (Map<String, String>) RedisUtils.hGet(RedisKey.DEVICE_UPGRADE, equipmentId);
            if (upgradeMap == null) {
                upgradeMap = new HashMap<>(64);
                //设置包序号
                upgradeMap.put(ParamsKey.PACKAGE_NUM, "1");
                //设置文件唯一标识
                upgradeMap.put(ParamsKey.FILE_KEY, software);
            }
            //设置总包数
            upgradeMap.put(ParamsKey.PACKAGE_SUM, String.valueOf(packageSum));
            //存到redis中
            RedisUtils.hSet(RedisKey.DEVICE_UPGRADE, equipmentId, upgradeMap);
            //发送电子锁升级控制
            sendUpgradeAdvise(equipmentId, packageSum, controlParam, upgradeConfig.getSha256());
            //将升级设施计数器+1
            addUpgradeDeviceCount(equipmentId);
        }
    }

    /**
     * 获取总包数
     *
     * @param hexBinFile 16进制文件
     * @param maxData    每个包最大值
     * @return 总包数
     */
    private int getPackageSum(String hexBinFile, int maxData) {
        //获取升级包总字节数
        int packageContentTotal = hexBinFile.length() / 2;
        //计算需要拆分的包数
        return (packageContentTotal - 1) / maxData + 1;
    }

    /**
     * 将升级设施数量加一
     *
     * @param equipmentId 主控id
     */
    private void addUpgradeDeviceCount(String equipmentId) {
        Set<String> deviceUpgradeSet = (Set<String>) RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
        if (deviceUpgradeSet == null) {
            deviceUpgradeSet = new HashSet<>();
            deviceUpgradeSet.add(equipmentId);
            RedisUtils.set(RedisKey.UPGRADE_DEVICE_COUNT, deviceUpgradeSet);
        } else {
            deviceUpgradeSet.add(equipmentId);
            RedisUtils.set(RedisKey.UPGRADE_DEVICE_COUNT, deviceUpgradeSet);
        }
    }

    /**
     * 校验是否需要升级
     *
     * @param controlParam  主控信息
     * @param upgradeConfig 升级信息
     */
    private boolean checkUpgradeFile(ControlParam controlParam, UpgradeConfig upgradeConfig) {
        String softwareVersion = controlParam.getSoftwareVersion();
        String hardwareVersion = controlParam.getHardwareVersion();
        //截取软件版本
        String software = softwareVersion.substring(0, softwareVersion.indexOf("."));
        String key = RedisKey.UPGRADE_FILE_PREFIX + software;
        //从redis获取升级包
        UpgradeConfig upgradeConfigTmp = (UpgradeConfig) RedisUtils.get(key);
        //redis为空,从ftp获取
        if (upgradeConfigTmp == null) {
            upgradeConfigTmp = getUpgradeFileFromFtp(software);
            RedisUtils.set(key, upgradeConfigTmp);
        }
        //是否与升级包中软件版本一致
        if (softwareVersion.equals(upgradeConfigTmp.getSoftwareVersion())) {
            return true;
        }
        //依赖硬件版本是否包含硬件版本
        if (!upgradeConfigTmp.getDependentHardVersion().contains(hardwareVersion)) {
            return true;
        }
        //依赖软件版本是否包含软件版本
        if (!upgradeConfigTmp.getDependentSoftVersion().contains(softwareVersion)) {
            return true;
        }
        copyUpgradeConfig(upgradeConfig, upgradeConfigTmp);
        return false;
    }

    /**
     * copy UpgradeConfig 属性
     *
     * @param upgradeConfig    目标
     * @param upgradeConfigTmp 来源
     */
    private void copyUpgradeConfig(UpgradeConfig upgradeConfig, UpgradeConfig upgradeConfigTmp) {
        upgradeConfig.setSoftwareVersion(upgradeConfigTmp.getSoftwareVersion());
        upgradeConfig.setDependentSoftVersion(upgradeConfigTmp.getDependentSoftVersion());
        upgradeConfig.setDependentHardVersion(upgradeConfigTmp.getDependentHardVersion());
        upgradeConfig.setSha256(upgradeConfigTmp.getSha256());
        upgradeConfig.setHexBinFile(upgradeConfigTmp.getHexBinFile());
        upgradeConfig.setGenerationTime(upgradeConfigTmp.getGenerationTime());
    }

    /**
     * 发送电子锁升级控制
     *
     * @param equipmentId  设施序列id
     * @param packageSum   总包数
     * @param controlParam 主控信息
     * @param check        sha256
     */
    private void sendUpgradeAdvise(String equipmentId, int packageSum, ControlParam controlParam, String check) {
        Map<String, Object> params = new HashMap<>(64);
        //命令码,开始升级
        params.put(ParamsKey.UPDATE_CODE, Constant.START_UPDATE);
        //数据包总数
        params.put(ParamsKey.PACKAGE_SUM, packageSum);
        //sha256
        params.put(ParamsKey.CHECK, check);
        //发送指令
        sendRequest(equipmentId, CmdId.UPGRADE_ADVISE, params, controlParam);
    }


    /**
     * 解析参数上报元数据
     *
     * @param deviceId     设施id
     * @param control      控制器信息
     * @param dataSource   元数据
     * @param equipmentId  主控id
     * @param serialNumber 流水号
     */
    private void resolveParamUpload(String deviceId, ControlParam control, Map<String, Object> dataSource,
                                    String equipmentId, Integer serialNumber) {
        //获取上报时间
        Long time = Long.parseLong(dataSource.get(ParamsKey.TIME).toString()) * 1000;
        //发送回包
        sendNullDataResponse(control, CmdId.PARAMS_UPLOAD, serialNumber);
        //处理参数上报元数据
        String actualValueJson = control.getActualValue();
        Map<String, Object> result;
        //判断主控json是否为空
        if (StringUtils.isEmpty(actualValueJson)) {
            result = new HashMap<>(64);
        } else {
            result = JSONObject.parseObject(actualValueJson, Map.class);
        }
        //获取参数list集合
        List<Map<String, Object>> paramList = (List<Map<String, Object>>) dataSource.get(ParamsKey.PARAMS_KEY);
        //构造告警参数信息
        Map<String, Object> alarmMap = new HashMap<>(64);
        alarmMap.put(ParamsKey.DEVICE_ID, deviceId);
        alarmMap.put(ParamsKey.TIME, time);
        alarmMap.put(ParamsKey.PARAMS_KEY, paramList);
        alarmMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        //解析告警数据存入主控实际参数值
        resolveAlarmValue(paramList, equipmentId, time, result);
        try {
            if (result.size() > 0) {
                String resultJson = JSONObject.toJSONString(result);
                //更新主控信息
                ControlParam controlParam = new ControlParam();
                controlParam.setHostId(control.getHostId());
                controlParam.setActualValue(resultJson);
                controlParam.setCurrentTime(time);
                controlParam.setDeviceId(control.getDeviceId());
                Result updateResult = controlFeign.updateControlParam(controlParam);
                if (updateResult != null && updateResult.getCode() == 0) {
                    //更新redis主控信息
                    control.setActualValue(resultJson);
                    updateRedisControl(control);
                }
            }
        } catch (Exception e) {
            log.info("[receiveMsg-paramsUpload] : update control failed {}", equipmentId);
        }
        log.info("[receiveMsg-paramsUpload] : send alarm {}", alarmMap);
        //将告警消息发送到kafka
        streamSender.sendAlarm(alarmMap);
    }

    /**
     * 更新reids主控信息
     *
     * @param controlParam 主控对象信息
     */
    private void updateRedisControl(ControlParam controlParam) {
        RedisUtils.hSet(RedisKey.CONTROL_INFO, controlParam.getHostId(), controlParam);
    }

    /**
     * 解析告警数据存入主控实际参数值
     *
     * @param paramList   参数集合
     * @param equipmentId 主控id
     * @param time        时间
     * @param result      实际参数值
     */
    private void resolveAlarmValue(List<Map<String, Object>> paramList, String equipmentId, Long time, Map<String, Object> result) {
        for (Map<String, Object> paramMap : paramList) {
            //获取dataClass
            String key = paramMap.get(ParamsKey.DATA_CLASS).toString();
            Object data = paramMap.get(ParamsKey.DATA);
            String alarmFlag = paramMap.get(ParamsKey.ALARM_FLAG).toString();
            //未关锁，未关门告警，应急开锁，撬门，撬锁，非法关门，更新门锁状态
            if (ParamsKey.UNLOCK.equals(key)
                    || ParamsKey.NOT_CLOSED.equals(key)
                    || ParamsKey.PRY_LOCK.equals(key)
                    || ParamsKey.PRY_DOOR.equals(key)
                    || ParamsKey.VIOLENCE_CLOSE.equals(key)
                    || ParamsKey.EMERGENCEY_LOCK.equals(key)) {
                //更新电子锁状态
                batchSaveLockStatus(data, equipmentId, time);
            } else if (ParamsKey.HIGH_TEMPERATURE.equals(key)
                    || ParamsKey.LOW_TEMPERATURE.equals(key)) {
                result.put(ParamsKey.TEMPERATURE, getDataMap(data, alarmFlag));
            } else {
                result.put(key, getDataMap(data, alarmFlag));
            }
        }
    }

    /**
     * 获取dataMap
     *
     * @param data      元数据
     * @param alarmFlag 告警标识
     * @return dataMap
     */
    private Map<String, Object> getDataMap(Object data, String alarmFlag) {
        Map<String, Object> dataMap = new HashMap<>(64);
        dataMap.put(ParamsKey.DATA, data);
        dataMap.put(ParamsKey.ALARM_FLAG, alarmFlag);
        return dataMap;
    }

    /**
     * 判断该命令时间是否大于上次时间
     *
     * @param equipmentId 主控id
     * @param cmdId       指令id
     * @param currentTime 当前时间
     * @return 结果
     */
    private boolean isNeedToResolve(String equipmentId, String cmdId, long currentTime) {
        //拼接key值
        String key = equipmentId + Constant.SEPARATOR + cmdId;
        //从redis获取该指令的时间
        String lastTimeStr = (String) RedisUtils.hGet(RedisKey.CMD_TIME, key);
        String currentTimeStr = String.valueOf(currentTime);
        if (StringUtils.isEmpty(lastTimeStr)) {
            RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTimeStr);
            return false;
        } else {
            Long lastTime = Long.parseLong(lastTimeStr);
            //该指令时间大于上次时间,执行操作
            if (currentTime > lastTime) {
                RedisUtils.hSet(RedisKey.CMD_TIME, key, currentTimeStr);
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 更新电子锁状态
     *
     * @param data        元数据
     * @param equipmentId 主控id
     * @param updateTime  更新时间
     */
    private void batchSaveLockStatus(Object data, String equipmentId, long updateTime) {
        List<Lock> locks = new ArrayList<>();
        Map<String, String> dataMap = (Map<String, String>) data;
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            //锁具编号
            String lockNum = entry.getKey();
            //门状态:   锁状态:  0:无效 1:开 2:关
            String status = entry.getValue();
            Lock lock = setDoorLockInfo(equipmentId, lockNum, status, updateTime);
            locks.add(lock);
        }
        //更新电子锁状态
        lockFeign.updateLockStatus(locks);
    }

    /**
     * 设置电子锁门状态信息
     *
     * @param equipmentId 主控id
     * @param doorNum     门编号
     * @param doorState   门状态
     * @param updateTime  更新时间
     * @return 锁信息
     */
    private Lock setDoorInfo(String equipmentId, String doorNum, String doorState, long updateTime) {
        Lock lock = new Lock();
        lock.setControlId(equipmentId);
        lock.setDoorNum(doorNum);
        lock.setDoorStatus(doorState);
        lock.setUpdateTime(updateTime);
        return lock;
    }


    /**
     * 设置电子锁和门状态信息
     *
     * @param equipmentId 主控id
     * @param doorNum     门编号
     * @param status      门锁状态
     * @param updateTime  更新时间
     * @return 锁信息
     */
    private Lock setDoorLockInfo(String equipmentId, String doorNum, String status, long updateTime) {
        Lock lock = new Lock();
        //获取门状态
        String doorStatus = String.valueOf(status.charAt(0));
        //获取锁状态
        String lockStatus = String.valueOf(status.charAt(1));
        lock.setControlId(equipmentId);
        lock.setDoorNum(doorNum);
        lock.setDoorStatus(doorStatus);
        lock.setLockStatus(lockStatus);
        lock.setUpdateTime(updateTime);
        return lock;
    }

    /**
     * 休眠事件更新主控信息
     *
     * @param control    主控信息
     * @param dataSource 数据源
     */
    private void updateSleepControlInfo(ControlParam control, Map<String, Object> dataSource) {
        String actualValueJson = control.getActualValue();
        Map<String, Object> result;
        //判断主控json是否为空
        if (StringUtils.isEmpty(actualValueJson)) {
            result = new HashMap<>(64);
        } else {
            result = JSONObject.parseObject(actualValueJson, Map.class);
        }
        //电量
        putActiveResultMap(result, dataSource, ParamsKey.ELECTRICITY);
        //温度
        putActiveResultMap(result, dataSource, ParamsKey.TEMPERATURE);
        //湿度
        putActiveResultMap(result, dataSource, ParamsKey.HUMIDITY);
        //倾斜
        putActiveResultMap(result, dataSource, ParamsKey.LEAN);
        //施工状态
        result.put(ParamsKey.CONSTRUCTED_STATE, dataSource.get(ParamsKey.CONSTRUCTED_STATE));
        //将map转成json
        String resultJson = JSONObject.toJSONString(result);
        //更新主控信息
        updateSleepOrActiveControl(control, resultJson, Constant.SLEEP_STATUS);
    }


    /**
     * 激活事件更新主控信息
     *
     * @param control    主控信息
     * @param dataSource 数据源
     */
    private void updateActiveControlInfo(ControlParam control, Map<String, Object> dataSource) {
        String actualValueJson = control.getActualValue();
        Map<String, Object> result;
        //判断主控json是否为空
        if (StringUtils.isEmpty(actualValueJson)) {
            result = new HashMap<>(64);
        } else {
            result = JSONObject.parseObject(actualValueJson, Map.class);
        }
        //通信模式
        putActiveResultMap(result, dataSource, ParamsKey.MODULE_TYPE);
        //电量
        putActiveResultMap(result, dataSource, ParamsKey.ELECTRICITY);
        //温度
        putActiveResultMap(result, dataSource, ParamsKey.TEMPERATURE);
        //湿度
        putActiveResultMap(result, dataSource, ParamsKey.HUMIDITY);
        //倾斜
        putActiveResultMap(result, dataSource, ParamsKey.LEAN);
        //水浸
        putActiveResultMap(result, dataSource, ParamsKey.LEACH);
        //无线信号值
        putActiveResultMap(result, dataSource, ParamsKey.WIRELESS_MODULE_SIGNAL);
        //供电方式
        putActiveResultMap(result, dataSource, ParamsKey.SUPPLY_ELECTRICITY_WAY);
        //运营商
        putActiveResultMap(result, dataSource, ParamsKey.OPERATOR);
        //将map转成json
        String resultJson = JSONObject.toJSONString(result);
        updateSleepOrActiveControl(control, resultJson, Constant.ACTIVE_STATUS);
    }

    /**
     * 更新激活和休眠主控信息
     *
     * @param control      主控对象信息
     * @param resultJson   实际参数值json
     * @param activeStatus 激活状态
     */
    private void updateSleepOrActiveControl(ControlParam control, String resultJson, String activeStatus) {
        //更新redis主控信息
        //更新主控信息
        ControlParam controlParam = new ControlParam();
        controlParam.setHostId(control.getHostId());
        controlParam.setActualValue(resultJson);
        controlParam.setActiveStatus(activeStatus);
        controlParam.setCurrentTime(control.getCurrentTime());
        controlParam.setDeviceId(control.getDeviceId());
        Result result = controlFeign.updateControlParam(controlParam);
        if (result != null && result.getCode() == 0) {
            control.setActiveStatus(activeStatus);
            control.setActualValue(resultJson);
            updateRedisControl(control);
        }
    }

    /**
     * 处理激活实际传感值
     *
     * @param result 实际值map
     * @param key    key值
     */
    private void putActiveResultMap(Map<String, Object> result, Map<String, Object> dataSource, String key) {
        Object data = dataSource.get(key);
        if (StringUtils.isEmpty(data)) {
            log.info("the property {} is null", key);
            return;
        }
        Map<String, Object> dataMap = (Map<String, Object>) result.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<>(64);
            dataMap.put(ParamsKey.DATA, data);
            dataMap.put(ParamsKey.ALARM_FLAG, Constant.CANCEL_ALARM);
            result.put(key, dataMap);
        } else {
            dataMap.put(ParamsKey.DATA, data);
        }
        result.put(key, dataMap);
    }


    /**
     * 更新门锁状态
     *
     * @param equipmentId 主控id
     * @param dataSource  数据源
     * @param updateTime  更新时间
     */
    private void updateDoorLockState(String equipmentId, Map<String, Object> dataSource, long updateTime) {
        //更新电子锁状态
        Object doorLockState = dataSource.get(ParamsKey.DOOR_LOCK_STATE);
        batchSaveLockStatus(doorLockState, equipmentId, updateTime);
    }

    /**
     * 空消息体回包
     *
     * @param control      主控信息
     * @param cmdId        命令id
     * @param serialNumber 流水号
     */
    private void sendNullDataResponse(ControlParam control, String cmdId, Integer serialNumber) {
        sendResponse(control, cmdId, null, serialNumber);
    }


    /**
     * 发送指令
     *
     * @param control      主控信息
     * @param cmdId        命令id
     * @param params       参数信息
     * @param serialNumber 流水号
     */
    private void sendResponse(ControlParam control, String cmdId, Map<String, Object> params, Integer serialNumber) {
        sender.sendInstruct(commonUtil.getResponseParams(control, cmdId, params, serialNumber));
    }

    /**
     * 发送指令
     *
     * @param equipmentId  设施序列id
     * @param cmdId        命令id
     * @param params       参数信息
     * @param controlParam 主控信息
     */
    private void sendRequest(String equipmentId, String cmdId, Map<String, Object> params, ControlParam controlParam) {
        FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams = new FiLinkReqOceanConnectParams();
        //设置指令类型
        fiLinkReqOceanConnectParams.setCmdType(CmdType.REQUEST_TYPE);
        //设置指令id
        fiLinkReqOceanConnectParams.setCmdId(cmdId);
        //设置设施id
        fiLinkReqOceanConnectParams.setEquipmentId(equipmentId);
        //设置参数信息
        fiLinkReqOceanConnectParams.setParams(params);
        //设置平台id
        fiLinkReqOceanConnectParams.setOceanConnectId(controlParam.getPlatformId());
        //设置产品id
        fiLinkReqOceanConnectParams.setAppId(controlParam.getProductId());
        sender.sendInstruct(fiLinkReqOceanConnectParams);
    }

    /**
     * 激活事件回包
     *
     * @param equipmentId  设施序列id
     * @param control      主控信息
     * @param serialNumber 流水号
     */
    private void sendActiveResponse(String equipmentId, ControlParam control, Integer serialNumber) {
        //获取主控配置策略
        String configValue = control.getConfigValue();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> config = mapper.readValue(configValue, Map.class);
            //设置当前时间
            config.put(ParamsKey.CURRENT_TIME, String.valueOf(System.currentTimeMillis() / 1000));
            //将配置转换成指令所需参数
            List<Map<String, Object>> configList = new ArrayList<>();
            for (Map.Entry<String, String> entry : config.entrySet()) {
                String value = entry.getValue();
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                Map<String, Object> param = new HashMap<>(64);
                param.put(ParamsKey.DATA_CLASS, entry.getKey());
                param.put(ParamsKey.DATA, value);
                configList.add(param);
            }
            Map<String, Object> params = new HashMap<>(64);
            params.put(ParamsKey.PARAMS_KEY, configList);
            //发送指令
            sendResponse(control, CmdId.ACTIVE, params, serialNumber);
        } catch (IOException e) {
            throw new ResponseException("active response config value is error: " + equipmentId);
        }
    }
}
