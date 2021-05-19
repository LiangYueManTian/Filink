package com.fiberhome.filink.fdevice.service.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.dto.*;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/23 19:32
 * @Description: com.fiberhome.filink.fdevice.service.statistics
 * @version: 1.0
 */
public interface StatisticsService {

    /**
     * 根据区域ID和设施类型统计设施数量
     * @param deviceParam
     * @return
     */
    Result queryDeviceCount(DeviceParam deviceParam);

    /**
     * 根据区域ID和设施类型统计设施状态数量
     * @param deviceParam
     * @return
     */
    Result queryDeviceStatusCount(DeviceParam deviceParam);

    /**
     * 根据区域ID和设施类型统计设施部署状态数量
     * @param deviceParam
     * @return
     */
    Result queryDeployStatusCount(DeviceParam deviceParam);

    /**
     * 查询当前用户各类设施数量
     * @return
     */
    Result queryDeviceTypeCount();

    /**
     * 查询指定区域，设施类型开锁次数最多的几条设施
     * @param deviceLogTopNumReq
     * @return
     */
    Result queryUnlockingTopNum(DeviceLogTopNumReq deviceLogTopNumReq);

    /**
     * 查询当前用户权限内开锁次数最多的设施
     * @return
     */
    Result queryUserUnlockingTopNum();

    /**
     * 查询指定区域，设施类型的前几条传感器数据
     * @param sensorTopNumReq
     * @return
     */
    Result queryDeviceSensorTopNum(SensorTopNumReq sensorTopNumReq);

    /**
     * 更新设施传感器极限值
     * @param sensorInfo
     * @return
     */
    Result updateSensorLimit(SensorInfo sensorInfo);

    /**
     * 查询当前用户设施状态数量
     * @param deviceParam
     * @return
     */
    Result queryUserDeviceStatusCount(DeviceParam deviceParam);

    /**
     * 查询当前用户设施数量和设施状态数量
     * @return
     */
    Result queryUserDeviceAndStatusCount();

    /**
     * 查询单个设施一段时间内的开锁次数
     * @param unlockingReq
     * @return
     */
    Result queryUnlockingTimesByDeviceId(UnlockingReq unlockingReq);


    /**
     * 导出设施统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeviceCount(ExportDto exportDto);

    /**
     * 导出设施状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeviceStatusCount(ExportDto exportDto);

    /**
     * 导出部署状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeployStatusCount(ExportDto exportDto);

    /**
     * 导出指定区域，设施类型开锁次数最多的几条设施
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportUnlockingTopNum(ExportDto exportDto);

    /**
     * 导出指定区域，设施类型的前几条传感器数据
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeviceSensorTopNum(ExportDto exportDto);
}
