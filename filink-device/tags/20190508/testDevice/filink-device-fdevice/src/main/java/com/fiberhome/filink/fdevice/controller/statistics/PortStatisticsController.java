package com.fiberhome.filink.fdevice.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.fdevice.service.statistics.PortStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 设施端口使用率信息表 前端控制器
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
@RestController
@RequestMapping("/portStatistics")
public class PortStatisticsController {

    /**
     * 设施端口使用率服务
     */
    @Autowired
    private PortStatisticsService portStatisticsService;

    /**
     * 新增
     *
     * @param listDevicePortUtilizationRate 设施端口使用率信息
     * @return Integer
     */
    @PostMapping("/addPortStatistics")
    public Integer addPortStatistics(@RequestBody List<DevicePortUtilizationRate> listDevicePortUtilizationRate) {
        return portStatisticsService.addPortStatistics(listDevicePortUtilizationRate);
    }

    /**
     * 端口使用率查询
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/queryPortStatistics/{id}")
    public Result queryPortStatistics(@PathVariable("id") String deviceId) {
        return portStatisticsService.queryPortStatistics(deviceId);
    }

    /**
     * 查询TopN
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return Result
     */
    @PostMapping("/queryPortTopN")
    public Result queryPortTopNo(@RequestBody TopNoPortStatisticsReq topNoPortStatisticsReq) {
        return portStatisticsService.queryPortTopNo(topNoPortStatisticsReq);
    }
}
