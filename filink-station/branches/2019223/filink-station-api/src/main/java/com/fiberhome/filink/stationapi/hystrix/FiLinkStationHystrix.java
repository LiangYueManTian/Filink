package com.fiberhome.filink.stationapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.stationapi.bean.FiLinkReqParamsDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 * filink数据中转熔断类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class FiLinkStationHystrix implements FiLinkStationFeign {

    /**
     * 指令下发熔断
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @Override
    public Result sendUdpReq(FiLinkReqParamsDto fiLinkReqParamsDto) {
        String msg = "udp send failed: " + fiLinkReqParamsDto.getSerialNum() + "---" + fiLinkReqParamsDto.getCmdId();
        log.info(msg);
        return ResultUtils.warn(ResultCode.FAIL,msg);
    }
}
