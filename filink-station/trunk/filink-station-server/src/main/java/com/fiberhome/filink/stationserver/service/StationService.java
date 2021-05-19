package com.fiberhome.filink.stationserver.service;

import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;

import java.util.List;

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

    /**
     * 新增协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    boolean addProtocol(ProtocolDto protocolDto);

    /**
     * 更新协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    boolean updateProtocol(ProtocolDto protocolDto);

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输对象
     * @return 操作结果
     */
    boolean deleteProtocol(List<ProtocolDto> protocolDtoList);

}
