package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.lockserver.bean.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/8/19
 */
@Component
public class TestAsync {
    @Autowired
    private MongoTemplate mongoTemplate;



    public void addTest() {
        List<Sensor> deviceLogList = new ArrayList<>();
        for (int j = 0; j < 10000; j++) {
            Sensor sensor = new Sensor();
            sensor.setDeviceId("0008gTFwkU9uEYo9oJK");
            sensor.setControlId("01011EBFDD6E5C118366");
            sensor.setTemperature(26);
            sensor.setHumidity(53);
            sensor.setElectricity(70);
            sensor.setLean(2);
            sensor.setCurrentTime(System.currentTimeMillis()-(j*8*3600*1000));
            deviceLogList.add(sensor);
        }
        mongoTemplate.insertAll(deviceLogList);
        System.out.println("线程"+Thread.currentThread().getName()+"插入数据成功");
    }
}
