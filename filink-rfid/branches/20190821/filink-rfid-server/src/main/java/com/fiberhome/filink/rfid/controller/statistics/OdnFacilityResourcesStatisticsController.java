package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportMeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortTopNumberReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.OdnFacilityResourcesStatisticsService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ONT设施资源统计 控制层
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */

@RestController
@RequestMapping("/odnFacilityResources")
@Slf4j
public class OdnFacilityResourcesStatisticsController {
    /**
     * templateService
     */
    @Autowired
    private TemplateService templateService;
    /**
     * ONT设施资源统计 服务类
     */
    @Autowired
    private OdnFacilityResourcesStatisticsService odnFacilityResourcesStatisticsService;

    /**
     * 跳纤侧端口统计
     *
     * @param jumpFiberPortStatisticsReq 统计条件
     * @return Result
     */
    @PostMapping("/jumpFiberPortStatistics")
    public Result jumpFiberPortStatistics(@RequestBody JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq) {
        return odnFacilityResourcesStatisticsService.jumpFiberPortStatistics(jumpFiberPortStatisticsReq);
    }

    /**
     * 跳纤侧端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportJumpFiberPortStatistics")
    public Result exportJumpFiberPortStatistics(@RequestBody ExportDto<ExportPortStatisticsReq> exportDto) {
        return odnFacilityResourcesStatisticsService.exportJumpFiberPortStatistics(exportDto);
    }

    /**
     * 熔纤侧端口统计
     *
     * @param meltFiberPortStatisticsReq 统计条件
     * @return Result
     */
    @PostMapping("/meltFiberPortStatistics")
    public Result meltFiberPortStatistics(@RequestBody MeltFiberPortStatisticsReq meltFiberPortStatisticsReq) {
        return odnFacilityResourcesStatisticsService.meltFiberPortStatistics(meltFiberPortStatisticsReq);
    }

    /**
     * 熔纤侧端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportMeltFiberPortStatistics")
    public Result exportMeltFiberPortStatistics(@RequestBody ExportDto<ExportMeltFiberPortStatisticsReq> exportDto) {
        return odnFacilityResourcesStatisticsService.exportMeltFiberPortStatistics(exportDto);
    }

    /**
     * 盘端口统计
     *
     * @param discPortStatisticsReq 统计条件
     * @return Result
     */
    @PostMapping("/discPortStatistics")
    public Result discPortStatistics(@RequestBody DiscPortStatisticsReq discPortStatisticsReq) {
        return odnFacilityResourcesStatisticsService.discPortStatistics(discPortStatisticsReq);
    }

    /**
     * 盘端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportDiscPortStatistics")
    public Result exportDiscPortStatistics(@RequestBody ExportDto<ExportPortStatisticsReq> exportDto) {
        return odnFacilityResourcesStatisticsService.exportDiscPortStatistics(exportDto);
    }

    /**
     * 框端口统计
     *
     * @param framePortStatisticsReq 统计条件
     * @return Result
     */
    @PostMapping("/framePortStatistics")
    public Result framePortStatistics(@RequestBody FramePortStatisticsReq framePortStatisticsReq) {
        return odnFacilityResourcesStatisticsService.framePortStatistics(framePortStatisticsReq);
    }

    /**
     * 框端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportFramePortStatistics")
    public Result exportFramePortStatistics(@RequestBody ExportDto<ExportPortStatisticsReq> exportDto) {
        return odnFacilityResourcesStatisticsService.exportFramePortStatistics(exportDto);
    }

    /**
     * TopN端口使用率统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportPortTopNumber")
    public Result exportPortTopNumber(@RequestBody ExportDto<ExportPortTopNumberReq> exportDto) {
        return odnFacilityResourcesStatisticsService.portTopNumber(exportDto);
    }

    /**
     * 单个设施端口状态统计
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/devicePortStatistics/{id}")
    public Result devicePortStatistics(@PathVariable("id") String deviceId) {
        // 数据权限 验证
        Boolean rfIdDataAuthInfo = templateService.getRfIdDataAuthInfo(deviceId);
        if (!rfIdDataAuthInfo) {
            log.info("设施id{} 没有权限");
            return ResultUtils.success();
        }
        return odnFacilityResourcesStatisticsService.devicePortStatistics(deviceId);
    }

    /**
     * 单个设施端口使用率统计
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/deviceUsePortStatistics/{id}")
    public Result deviceUsePortStatistics(@PathVariable("id") String deviceId) {
        // 数据权限 验证
        Boolean rfIdDataAuthInfo = templateService.getRfIdDataAuthInfo(deviceId);
        if (!rfIdDataAuthInfo) {
            return ResultUtils.success();
        }
        return odnFacilityResourcesStatisticsService.deviceUsePortStatistics(deviceId);
    }
}
