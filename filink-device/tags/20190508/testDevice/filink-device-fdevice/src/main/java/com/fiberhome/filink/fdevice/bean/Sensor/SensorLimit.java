package com.fiberhome.filink.fdevice.bean.Sensor;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 10:23
 * @Description: com.fiberhome.filink.fdevice.bean.Sensor
 * @version: 1.0
 */
@Data
@TableName("device_sensor_limit")
public class SensorLimit {
    /**
     * 设施id
     */
    @TableId("device_id")
    private String deviceId;

    /**
     * 温度最大值
     */
    @TableField("temperature_max")
    private Integer temperatureMax;

    /**
     * 温度最大值取样时间
     */
    @TableField("temperature_max_time")
    private Timestamp temperatureMaxTime;

    /**
     * 温度最小值
     */
    @TableField("temperature_min")
    private Integer temperatureMin;

    /**
     * 温度最小值取样时间
     */
    @TableField("temperature_min_time")
    private Timestamp temperatureMinTime;

    /**
     * 湿度最大值
     */
    @TableField("humidity_max")
    private Integer humidityMax;

    /**
     * 湿度最大值取样时间
     */
    @TableField("humidity_max_time")
    private Timestamp humidityMaxTime;

    /**
     * 湿度最小值
     */
    @TableField("humidity_min")
    private Integer humidityMin;

    /**
     * 湿度最小值取样时间
     */
    @TableField("humidity_min_time")
    private Timestamp humidityMinTime;
}
