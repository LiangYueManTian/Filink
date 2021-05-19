package com.fiberhome.filink.lockserver.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {

    /**
     * todo 升级测试
     */
    public static Map<String, Map<String,String>> upgradeTimeMap = new HashMap<>(64);



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
