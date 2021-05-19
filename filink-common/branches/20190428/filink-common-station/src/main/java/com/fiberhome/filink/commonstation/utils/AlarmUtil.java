package com.fiberhome.filink.commonstation.utils;


import com.fiberhome.filink.commonstation.constant.ParamsKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统告警生成类
 *
 * @author CongcaiYu
 */
public class AlarmUtil {

    /**
     * 生成通信中断告警参数信息
     *
     * @param deviceId    设施id
     * @param alarmStatus 告警状态
     * @param equipmentId 设备id
     */
    public static Map<String, Object> getInterruptAlarmMap(String deviceId, String equipmentId, String alarmStatus) {
        //构造告警参数信息
        Map<String, Object> alarmMap = new HashMap<>(64);
        //设施id
        alarmMap.put(ParamsKey.DEVICE_ID, deviceId);
        //告警时间
        alarmMap.put(ParamsKey.TIME, System.currentTimeMillis());
        List<Map<String, Object>> alarmList = new ArrayList<>();
        Map<String, Object> alarmData = new HashMap<>(64);
        //通讯中断告警
        alarmData.put(ParamsKey.DATA_CLASS, ParamsKey.COMMUNICATION_INTERRUPT);
        alarmData.put(ParamsKey.ALARM_FLAG, alarmStatus);
        alarmData.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        alarmList.add(alarmData);
        alarmMap.put(ParamsKey.PARAMS_KEY, alarmList);
        return alarmMap;
    }
}
