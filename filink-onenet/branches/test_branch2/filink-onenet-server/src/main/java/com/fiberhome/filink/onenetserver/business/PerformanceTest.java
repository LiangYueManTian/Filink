package com.fiberhome.filink.onenetserver.business;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {

    /**
     * todo 升级测试
     */
    public static Map<String, Map<String,String>> upgradeTimeMap = new HashMap<>(64);

    /**
     * todo 图片测试
     */
    public static Map<String,Map<String,String>> picTimeMap = new HashMap<>(64);

    /**
     * todo 开锁测试
     */
    public static Map<String,Map<String,String>> lockTimeMap = new HashMap<>(64);


    /**
     * todo 告警测试
     */
    public static Map<String,Map<String,String>> alarmTimeMap = new HashMap<>(64);

    /**
     * todo 告警数量
     */
    public static Map<String,String> alarmCountMap = new HashMap<>();

    /**
     * todo 告警接收数量
     */
    public static AtomicInteger atomicInteger = new AtomicInteger(0);


    /**
     * todo 告警数量测试代码
     * @param equipmentId
     * @param currTime
     */
    public static synchronized void setAlarmCount(String equipmentId,Long currTime){
        alarmCountMap.put(equipmentId,String.valueOf(currTime));
    }

    /**
     * todo 告警测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setAlarmTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = alarmTimeMap.get(equipmentId);
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
        alarmTimeMap.put(equipmentId,map);
    }

    /**
     * todo 开锁测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setLockTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = lockTimeMap.get(equipmentId);
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
        lockTimeMap.put(equipmentId,map);
    }

    /**
     * todo 图片测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setPicTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = picTimeMap.get(equipmentId);
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
        picTimeMap.put(equipmentId,map);
    }

    /**
     * todo 升级测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setUpgradeTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = upgradeTimeMap.get(equipmentId);
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
        upgradeTimeMap.put(equipmentId,map);
    }
}
