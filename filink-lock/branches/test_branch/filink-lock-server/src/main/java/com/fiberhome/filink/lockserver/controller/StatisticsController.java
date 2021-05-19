package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.SensorStatReq;
import com.fiberhome.filink.lockserver.bean.SensorTopNumReq;
import com.fiberhome.filink.lockserver.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
