package com.fiberhome.filink.rfid.service.statistics;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticalCableSectionStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;

/**
 * <p>
 * 光缆及纤芯统计 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-31
 */
public interface FiberOpticsAndCoreStatisticsService {

    /**
     * 光缆统计
     *
     * @return Result
     */
    Result opticalFiberStatistics();

    /**
     * 光缆统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    Result exportOpticalFiberStatistics(ExportDto<ExportOpticCableInfoStatisticReq> exportDto);

    /**
     * 光缆段统计
     *
     * @param opticCableInfoSectionStatisticsReq 查询条件
     * @return Result
     */
    Result opticalFiberSection(OpticCableInfoSectionStatisticsReq opticCableInfoSectionStatisticsReq);

    /**
     * 光缆段统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    Result exportOpticalFiberSection(ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto);

    /**
     * 纤芯统计
     *
     * @param coreStatisticsReq 查询条件
     * @return Result
     */
    Result coreStatistics(CoreStatisticsReq coreStatisticsReq);
    /**
     * 纤芯统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    Result exportCoreStatistics(ExportDto<ExportOpticalCableSectionStatisticsReq> exportDto);
}
