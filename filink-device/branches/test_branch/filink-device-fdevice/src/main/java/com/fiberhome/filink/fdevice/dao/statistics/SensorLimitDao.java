package com.fiberhome.filink.fdevice.dao.statistics;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.bean.Sensor.SensorLimit;
import com.fiberhome.filink.fdevice.dto.SensorTopNum;
import com.fiberhome.filink.fdevice.dto.SensorTopNumReq;

import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 11:00
 * @Description: com.fiberhome.filink.fdevice.dao.statistics
 * @version: 1.0
 */
public interface SensorLimitDao extends BaseMapper<SensorLimit> {

    /**
     * 查询指定区域，设施类型的前几条传感器数据
     * @param sensorTopNumReq
     * @return
     */
    List<SensorTopNum> queryDeviceSensorTopNum(SensorTopNumReq sensorTopNumReq);

}
