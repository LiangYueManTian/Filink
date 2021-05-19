package com.fiberhome.filink.fdevice.controller.devicelog;

import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
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

    @Async
    public void addTest() {
        List<DeviceLog> deviceLogList = new ArrayList<>();
        for (int j = 0; j < 10000; j++) {
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setLogName("井盖变化时间");
            deviceLog.setLogType("1");
            deviceLog.setDeviceType("030");
            deviceLog.setDeviceId("0008gTFwkU9uEYo9oJK");
            deviceLog.setDeviceCode("41610309888439");
            deviceLog.setDeviceName("转储测试设施");
            deviceLog.setNodeObject("测试");
            deviceLog.setAreaId("gis12");
            deviceLog.setAreaName("转储测试区域");
            deviceLog.setCurrentTime(System.currentTimeMillis());
            deviceLog.setRemarks("转储测试");
            deviceLogList.add(deviceLog);
        }
        mongoTemplate.insertAll(deviceLogList);
        System.out.println("线程"+Thread.currentThread().getName()+"插入数据成功");
    }
}
