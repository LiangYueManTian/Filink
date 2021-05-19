package com.fiberhome.filink.rfid.service.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportMeltFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportPortTopNumberReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;

import java.util.List;

/**
 * <p>
 * ONT设施资源统计 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */
public interface OdnFacilityResourcesStatisticsService {

    /**
     * 跳纤侧端口统计
     *
     * @param jumpFiberPortStatisticsReq 统计条件
     * @return Result
     */
    Result jumpFiberPortStatistics(JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq);
    /**
     * 跳纤侧端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportJumpFiberPortStatistics(ExportDto<ExportPortStatisticsReq> exportDto);
    /**
     * 熔纤侧端口统计
     *
     * @param meltFiberPortStatisticsReq 统计条件
     * @return Result
     */
    Result meltFiberPortStatistics(MeltFiberPortStatisticsReq meltFiberPortStatisticsReq);
    /**
     * 熔纤侧端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportMeltFiberPortStatistics(ExportDto<ExportMeltFiberPortStatisticsReq> exportDto);
    /**
     * 盘端口统计
     *
     * @param discPortStatisticsReq 统计条件
     * @return Result
     */
    Result discPortStatistics(DiscPortStatisticsReq discPortStatisticsReq);
    /**
     * 盘端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportDiscPortStatistics(ExportDto<ExportPortStatisticsReq> exportDto);
    /**
     * 框端口统计
     *
     * @param framePortStatisticsReq 统计条件
     * @return Result
     */
    Result framePortStatistics(FramePortStatisticsReq framePortStatisticsReq);
    /**
     * 框端口统计导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportFramePortStatistics(ExportDto<ExportPortStatisticsReq> exportDto);
    /**
     * TopN端口使用率导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result portTopNumber(ExportDto<ExportPortTopNumberReq> exportDto);
    /**
     * 单个设施端口状态统计
     *
     * @param deviceId 设施id
     * @return Result
     */
    Result devicePortStatistics(String deviceId);

    /**
     * 单个设施端口使用率统计
     *
     * @param deviceId 设施id
     * @return Result
     */
    Result deviceUsePortStatistics(String deviceId);

    /**
     * 端口使用率计算
     *
     * @param listDeviceId 设备id
     * @return Integer
     */
    Integer portStatistics(List<String> listDeviceId);

}
