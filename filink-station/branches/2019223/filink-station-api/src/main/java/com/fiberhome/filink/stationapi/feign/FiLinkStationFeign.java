package com.fiberhome.filink.stationapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.stationapi.bean.FiLinkReqParamsDto;
import com.fiberhome.filink.stationapi.hystrix.FiLinkStationHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 数据中转feign
 * @author CongcaiYu
 */
@FeignClient(name = "filink-station-server", fallback = FiLinkStationHystrix.class)
public interface FiLinkStationFeign {

    /**
     * 发送指令
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     * @return 请求结果
     */
    @PostMapping("/sendUdp")
    Result sendUdpReq(@RequestBody FiLinkReqParamsDto fiLinkReqParamsDto);
}
