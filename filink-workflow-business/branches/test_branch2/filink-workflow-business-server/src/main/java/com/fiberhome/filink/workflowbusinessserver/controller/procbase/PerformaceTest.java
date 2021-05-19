package com.fiberhome.filink.workflowbusinessserver.controller.procbase;

import java.util.HashMap;
import java.util.Map;

public class PerformaceTest {

    /**
     * todo 开锁测试
     */
    public static Map<String, Map<String,String>> workTimeMap = new HashMap<>(64);

    /**
     * todo 开锁测试代码
     * @param equipmentId
     * @param key
     * @param currTime
     */
    public static synchronized void setWorkTime(String equipmentId,String key,Long currTime){
        Map<String, String> map = workTimeMap.get(equipmentId);
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
        workTimeMap.put(equipmentId,map);
    }
}
