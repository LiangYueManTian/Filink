package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticalCableSectionStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.FiberOpticsAndCoreStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 光缆及纤芯统计 控制层
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-31
 */
@RestController
@RequestMapping("/fiberOpticsAndCore")
public class FiberOpticsAndCoreStatisticsController {

    @Autowired
    private FiberOpticsAndCoreStatisticsService statisticsService;

    /**
     * 光缆统计
     *
     * @return Result
     */
    @GetMapping("/opticalFiber")
    public Result opticalFiber() {
        return statisticsService.opticalFiberStatistics();
    }

    /**
     * 光缆统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportOpticalFiber")
    public Result exportOpticalFiber(@RequestBody ExportDto<ExportOpticCableInfoStatisticReq> exportDto) {
        return statisticsService.exportOpticalFiberStatistics(exportDto);
    }

    /**
     * 光缆段统计
     *
     * @param opticCableInfoSectionStatisticsReq 查询条件
     * @return Result
     */
    @PostMapping("/opticalFiberSection")
    public Result opticalFiberSection(@RequestBody OpticCableInfoSectionStatisticsReq opticCableInfoSectionStatisticsReq) {
        return statisticsService.opticalFiberSection(opticCableInfoSectionStatisticsReq);
    }

    /**
     * 光缆段统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportOpticalFiberSection")
    public Result exportOpticalFiberSection(@RequestBody ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto) {
        return statisticsService.exportOpticalFiberSection(exportDto);
    }

    /**
     * 纤芯统计
     *
     * @param coreStatisticsReq 查询条件
     * @return Result
     */
    @PostMapping("/coreStatistics")
    public Result coreStatistics(@RequestBody CoreStatisticsReq coreStatisticsReq) {
        return statisticsService.coreStatistics(coreStatisticsReq);
    }

    /**
     * 纤芯统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportCoreStatistics")
    public Result exportCoreStatistics(@RequestBody ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto) {
        return statisticsService.exportCoreStatistics(exportDto);
    }
}
