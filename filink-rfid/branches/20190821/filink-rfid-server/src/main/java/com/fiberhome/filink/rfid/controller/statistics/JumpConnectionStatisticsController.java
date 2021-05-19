package com.fiberhome.filink.rfid.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionInCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionOutCabinetReq;
import com.fiberhome.filink.rfid.req.statistics.jumpconnection.JumpConnectionStatisticsReq;
import com.fiberhome.filink.rfid.service.statistics.JumpConnectionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ONT设施跳接关系 控制层
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-10
 */
@RestController
@RequestMapping("/jumpConnection")
public class JumpConnectionStatisticsController {

    @Autowired
    private JumpConnectionStatisticsService jumpConnectionStatisticsService;

    /**
     * 柜内跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    @PostMapping("/jumpConnectionInCabinet")
    public Result jumpConnectionInCabinet(@RequestBody JumpConnectionStatisticsReq jumpConnectionStatisticsReq) {
        return jumpConnectionStatisticsService.inCabinet(jumpConnectionStatisticsReq);
    }
    /**
     * 柜内跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportJumpConnectionInCabinet")
    public Result exportJumpConnectionInCabinet(@RequestBody ExportDto<ExportJumpConnectionInCabinetReq> exportDto) {
        return jumpConnectionStatisticsService.exportInCabinet(exportDto);
    }
    /**
     * 柜间跳接
     *
     * @param jumpConnectionStatisticsReq 条件
     * @return Result
     */
    @PostMapping("/jumpConnectionOutCabinet")
    public Result jumpConnectionOutCabinet(@RequestBody JumpConnectionStatisticsReq jumpConnectionStatisticsReq) {
        return jumpConnectionStatisticsService.outCabinet(jumpConnectionStatisticsReq);
    }
    /**
     * 柜间跳接导出
     *
     * @param exportDto 导出信息
     * @return Result
     */
    @PostMapping("/exportJumpConnectionOutCabinet")
    public Result exportJumpConnectionOutCabinet(@RequestBody ExportDto<ExportJumpConnectionOutCabinetReq> exportDto) {
        return jumpConnectionStatisticsService.exportOutCabinet(exportDto);
    }
}
