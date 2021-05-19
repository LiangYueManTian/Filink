package com.fiberhome.filink.rfid.service.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionInCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionOutCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;

/**
 * <p>
 * ODN跳接关系统计 服务类
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */
public interface JumpConnectionStatisticsService {

    /**
     * 柜内跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    Result inCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq);

    /**
     * 柜内跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportInCabinet(ExportDto<ExportJumpConnectionInCabinetReq> exportDto);

    /**
     * 柜间跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    Result outCabinet(JumpConnectionStatisticsReq jumpConnectionStatisticsReq);

    /**
     * 柜间跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    Result exportOutCabinet(ExportDto<ExportJumpConnectionOutCabinetReq> exportDto);
}
