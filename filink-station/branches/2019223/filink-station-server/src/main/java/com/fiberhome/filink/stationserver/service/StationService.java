package com.fiberhome.filink.stationserver.service;

import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParamsDto;

/**
 * 中转服务service
 * @author CongcaiYu
 */
public interface StationService {

    /**
     * 发送udp
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    void sendUdp(FiLinkReqParamsDto fiLinkReqParamsDto);
}
