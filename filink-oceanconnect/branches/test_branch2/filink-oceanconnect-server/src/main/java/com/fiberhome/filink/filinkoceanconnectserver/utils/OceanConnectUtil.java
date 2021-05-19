package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * 公用方法工具类
 *
 * @author CongcaiYu
 */
@Slf4j
public class OceanConnectUtil {

    /**
     * 清除redis中流水号
     *
     * @param equipmentId 设施id
     */
    public static void clearRedisSerialNum(String equipmentId) {
        log.info("sleep clear redis serialNumber : {}", equipmentId);
        try {
            //请求帧流水号
            String reqKey = equipmentId + Constant.SEPARATOR + CmdType.REQUEST_TYPE;
            //响应帧流水号
            String resKey = equipmentId + Constant.SEPARATOR + CmdType.RESPONSE_TYPE;
            RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, reqKey);
            RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, resKey);
        } catch (Exception e) {
            log.error("clear redis serial num failed : {}", e.getMessage());
        }
    }

    /**
     * 将正在升级设施数量减一
     *
     * @param equipmentId 主控id
     */
    public static void deleteUpgradeDeviceCount(String equipmentId) {
        try {
            Set<String> deviceUpgradeSet = (Set<String>) RedisUtils.get(RedisKey.UPGRADE_DEVICE_COUNT);
            if (deviceUpgradeSet == null) {
                log.error("device upgrade list is null");
            } else {
                deviceUpgradeSet.remove(equipmentId);
                RedisUtils.set(RedisKey.UPGRADE_DEVICE_COUNT, deviceUpgradeSet);
            }
            RedisUtils.hRemove(RedisKey.DEVICE_UPGRADE, equipmentId);
        } catch (Exception e) {
            log.error("delete upgrade info failed : {}", e.getMessage());
        }
    }

    /**
     * 删除redis图片信息
     *
     * @param equipmentId 主控id
     */
    public static void deletePicMap(String equipmentId) {
        try {
            String adviseKey = RedisKey.DEVICE_ADVISE + Constant.SEPARATOR + equipmentId;
            Map<String, Map<String, String>> adviseMap = (Map<String, Map<String, String>>) RedisUtils.get(adviseKey);
            if (adviseMap != null && adviseMap.size() > 0) {
                for (Map.Entry<String, Map<String, String>> entry : adviseMap.entrySet()) {
                    String index = entry.getKey();
                    String key = RedisKey.PIC_UPLOAD + Constant.SEPARATOR + equipmentId + Constant.SEPARATOR + index;
                    RedisUtils.remove(key);
                }
            }
            RedisUtils.remove(adviseKey);
        } catch (Exception e) {
            log.error("delete pic map failed : {}", e.getMessage());
        }
    }

    /**
     * 清除部署状态指令
     *
     * @param equipmentId 主控id
     */
    public static void clearDeployCmd(String equipmentId) {
        try {
            String key = equipmentId + Constant.SEPARATOR + CmdId.DEPLOY_STATUS;
            RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
            RedisUtils.hRemove(RedisKey.DEPLOY_CMD, equipmentId);
        } catch (Exception e) {
            log.error("clear deploy cmd failed : {}", e.getMessage());
        }
    }

    /**
     * 清除事件时间
     *
     * @param equipmentId 主控id
     */
    private static void clearEventTime(String equipmentId) {
        try {
            String active = equipmentId + Constant.SEPARATOR + CmdId.ACTIVE;
            String sleep = equipmentId + Constant.SEPARATOR + CmdId.SLEEP;
            String openLock = equipmentId + Constant.SEPARATOR + CmdId.OPEN_LOCK_UPLOAD;
            String closeLock = equipmentId + Constant.SEPARATOR + CmdId.CLOSE_LOCK_UPLOAD;
            String doorChange = equipmentId + Constant.SEPARATOR + CmdId.DOOR_STATE_CHANGE;
            String upgrade = equipmentId + Constant.SEPARATOR + CmdId.UPGRADE_SUCCESS;
            RedisUtils.hRemove(RedisKey.CMD_TIME, active, sleep, openLock, closeLock, doorChange, upgrade);
        } catch (Exception e) {
            log.error("clear event time failed : {}", e.getMessage());
        }
    }

    /**
     * 清除离线和失联定时器
     *
     * @param equipmentId 主控id
     */
    private static void clearOffLineTime(String equipmentId) {
        try {
            //拼接key值
            String offLineKey = RedisKey.OFF_LINE + Constant.SEPARATOR + equipmentId;
            String outOfConcatKey = RedisKey.OUT_OF_CONCAT + Constant.SEPARATOR + equipmentId;
            RedisUtils.remove(offLineKey);
            RedisUtils.remove(outOfConcatKey);
        } catch (Exception e) {
            log.error("clear offLine time failed : {}", e.getMessage());
        }
    }


    /**
     * 删除主控信息
     *
     * @param equipmentId 主控id
     */
    public static void deleteControlInfo(String equipmentId) {
        //删除主控信息
        RedisUtils.hRemove(RedisKey.CONTROL_INFO, equipmentId);
        //删除离线时间
        clearOffLineTime(equipmentId);
        //清除流水号
        RedisUtils.hRemove(RedisKey.SERIAL_NUM, equipmentId);
        //清除该主控已有流水号
        clearRedisSerialNum(equipmentId);
        //清除事件时间
        clearEventTime(equipmentId);
        //删除升级信息
        deleteUpgradeDeviceCount(equipmentId);
        //删除图片信息
        deletePicMap(equipmentId);
        //删除部署指令
        clearDeployCmd(equipmentId);
    }
}
