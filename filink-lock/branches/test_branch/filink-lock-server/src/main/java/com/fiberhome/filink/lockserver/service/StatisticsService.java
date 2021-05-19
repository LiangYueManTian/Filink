package com.fiberhome.filink.lockserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.SensorStatReq;
import com.fiberhome.filink.lockserver.bean.SensorTopNumReq;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/31 15:55
 * @Description: com.fiberhome.filink.lockserver.service
 * @version: 1.0
 */
public interface StatisticsService {

    /**
     * 查询设施传感器值
     * @param sensorStatReq
     * @return
     */
    Result queryDeviceSensorValues(SensorStatReq sensorStatReq);


    /**
     * 查询指定区域，设施类型的前几条传感器数据
     * @param sensorTopNumReq
     * @return
     */
    Result queryDeviceSensorTopNum(SensorTopNumReq sensorTopNumReq);

    /**
     * 定时任务清除过期数据
     */
    void deleteExpiredData();
}
