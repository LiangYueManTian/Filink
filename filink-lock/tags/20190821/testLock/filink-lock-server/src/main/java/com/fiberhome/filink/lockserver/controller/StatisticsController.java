package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.bean.Sensor;
import com.fiberhome.filink.lockserver.bean.SensorStatReq;
import com.fiberhome.filink.lockserver.bean.SensorTopNumReq;
import com.fiberhome.filink.lockserver.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/31 15:34
 * @Description: com.fiberhome.filink.lockserver.controller
 * @version: 1.0
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 查询设施传感器值
     * @param sensorStatReq
     * @return
     */
    @PostMapping("/queryDeviceSensorValues")
    public Result queryDeviceSensorValues(@RequestBody SensorStatReq sensorStatReq) {
        return statisticsService.queryDeviceSensorValues(sensorStatReq);
    }


    /**
     * 查询指定区域，设施类型的前几条传感器数据
     * @param sensorTopNumReq
     * @return
     */
    @PostMapping("/queryDeviceSensorTopNum")
    public Result queryDeviceSensorTopNum(@RequestBody SensorTopNumReq sensorTopNumReq) {
        return statisticsService.queryDeviceSensorTopNum(sensorTopNumReq);
    }
    @Autowired
    private TestAsync testAsync;
    @GetMapping("/test")
    public String addTest() throws Exception{
        for (int i = 0; i < 1000; i++) {
            if (i % 50 == 0 && i != 0) {
                System.out.println("已经有50个线程在跑 休息一下");
                Thread.sleep(30000);
            }
            testAsync.addTest();
            System.out.println("启动" + (i + 1) + "个线程");
        }
        return "插入数据完成";
    }

}
