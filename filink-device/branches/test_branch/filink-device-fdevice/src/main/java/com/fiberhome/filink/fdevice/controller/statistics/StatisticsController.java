package com.fiberhome.filink.fdevice.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.service.statistics.StatisticsService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/23 11:26
 * @Description: com.fiberhome.filink.fdevice.controller.statistics
 * @version: 1.0
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    /**
     * 根据区域ID和设施类型统计设施数量
     * @param deviceParam
     * @return
     */
    @PostMapping("/queryDeviceCount")
    public Result queryDeviceCount(@RequestBody DeviceParam deviceParam) {
        return statisticsService.queryDeviceCount(deviceParam);
    }

    /**
     * 根据区域ID和设施类型统计设施状态数量
     * @param deviceParam
     * @return
     */
    @PostMapping("/queryDeviceStatusCount")
    public Result queryDeviceStatusCount(@RequestBody DeviceParam deviceParam) {
        return statisticsService.queryDeviceStatusCount(deviceParam);
    }

    /**
     * 根据区域ID和设施类型统计设施部署状态数量
     * @param deviceParam
     * @return
     */
    @PostMapping("/queryDeployStatusCount")
    public Result queryDeployStatusCount(@RequestBody DeviceParam deviceParam) {
        return statisticsService.queryDeployStatusCount(deviceParam);
    }

    /**
     * 查询当前用户各类设施数量
     * @return
     */
    @GetMapping("/queryDeviceTypeCount")
    public Result queryDeviceTypeCount() {
        return statisticsService.queryDeviceTypeCount();
    }

    /**
     * 查询当前用户设施状态数量
     * @return
     */
    @PostMapping("/queryUserDeviceStatusCount")
    public Result queryUserDeviceStatusCount(@RequestBody DeviceParam deviceParam) {
        return statisticsService.queryUserDeviceStatusCount(deviceParam);
    }

    /**
     * 查询当前用户设施状态数量
     * @return
     */
    @GetMapping("/queryUserDeviceStatusCount")
    public Result queryUserDeviceStatusCount() {
        return statisticsService.queryUserDeviceStatusCount(null);
    }

    /**
     * 查询当前用户设施数量和设施状态数量
     * @return
     */
    @GetMapping("/queryUserDeviceAndStatusCount")
    public Result queryUserDeviceAndStatusCount() {
        return statisticsService.queryUserDeviceAndStatusCount();
    }

    /**
     * 查询当前用户权限内开锁次数最多的前10个设施
     * @return
     */
    @GetMapping("/queryUserUnlockingTopNum")
    public Result queryUserUnlockingTopNum() {
        return statisticsService.queryUserUnlockingTopNum();
    }

    /**
     * 查询指定区域，设施类型开锁次数最多的几条设施
     * @param deviceLogTopNumReq
     * @return
     */
    @PostMapping("/queryUnlockingTopNum")
    public Result queryUnlockingTopNum(@RequestBody DeviceLogTopNumReq deviceLogTopNumReq) {
        return statisticsService.queryUnlockingTopNum(deviceLogTopNumReq);
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

    /**
     * 更新设施传感器极限值
     * @param sensorInfo
     */
    @PostMapping("/feign/updateSensorLimit")
    public Result updateSensorLimit(@RequestBody SensorInfo sensorInfo) {
        return statisticsService.updateSensorLimit(sensorInfo);
    }

    /**
     * 查询单个设施一段时间内的开锁次数
     * @param unlockingReq
     * @return
     */
    @PostMapping("/queryUnlockingTimesByDeviceId")
    public Result queryUnlockingTimesByDeviceId(@RequestBody UnlockingReq unlockingReq) {
        return statisticsService.queryUnlockingTimesByDeviceId(unlockingReq);
    }

    /**
     * 导出设施统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeviceCount")
    public Result exportDeviceCount(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return statisticsService.exportDeviceCount(exportDto);
    }

    /**
     * 导出设施状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeviceStatusCount")
    public Result exportDeviceStatusCount(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return statisticsService.exportDeviceStatusCount(exportDto);
    }

    /**
     * 导出部署状态统计数量
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeployStatusCount")
    public Result exportDeployStatusCount(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return statisticsService.exportDeployStatusCount(exportDto);
    }

    /**
     * 导出指定区域，设施类型开锁次数最多的几条设施
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportUnlockingTopNum")
    public Result exportUnlockingTopNum(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return statisticsService.exportUnlockingTopNum(exportDto);
    }

    /**
     * 导出指定区域，设施类型的前几条传感器数据
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeviceSensorTopNum")
    public Result exportDeviceSensorTopNum(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return statisticsService.exportDeviceSensorTopNum(exportDto);
    }

}
