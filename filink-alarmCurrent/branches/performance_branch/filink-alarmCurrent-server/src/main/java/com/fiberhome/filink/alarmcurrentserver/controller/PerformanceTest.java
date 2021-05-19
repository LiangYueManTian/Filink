package com.fiberhome.filink.alarmcurrentserver.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformanceTest {

    /**
     * todo 开锁测试
     */
    public static Map<String, Map<String,String>> alarmMap = new HashMap<>(64);

    /**
     * todo 开锁测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setAlarmTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = alarmMap.get(equipmentId);
        String time;
        if(map == null){
            map = new HashMap<>(64);
        }
        if(currTime == null){
            time = String.valueOf(System.currentTimeMillis());
        }else {
            time = String.valueOf(currTime);
        }
        map.put(key,time);
        alarmMap.put(equipmentId,map);
    }

}
