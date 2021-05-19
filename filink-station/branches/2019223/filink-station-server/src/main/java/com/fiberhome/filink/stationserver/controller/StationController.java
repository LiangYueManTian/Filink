package com.fiberhome.filink.stationserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.stationserver.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * station控制器
 * @author CongcaiYu
 */
@RestController
public class StationController {

    @Autowired
    private StationService stationService;


    /**
     * 指令下发
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @PostMapping("/sendUdp")
    public Result send(@RequestBody FiLinkReqParamsDto fiLinkReqParamsDto) {
        stationService.sendUdp(fiLinkReqParamsDto);
        return ResultUtils.success();
    }

}
