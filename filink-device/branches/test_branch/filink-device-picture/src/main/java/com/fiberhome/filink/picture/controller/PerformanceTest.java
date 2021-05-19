package com.fiberhome.filink.picture.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * todo 图片测试
 */
public class PerformanceTest {

    public static Map<String,Map<String,String>> picMap = new HashMap<>();

    public static synchronized void setPicTime(String deviceId,String key,Long currentTime){
        Map<String, String> map = picMap.get(deviceId);
        String time;
        if(map == null){
            map = new HashMap<>(64);
        }
        if(currentTime == null){
            time = String.valueOf(System.currentTimeMillis());
        }else {
            time = String.valueOf(currentTime);
        }
        map.put(key,time);
        picMap.put(deviceId,map);
    }
}
