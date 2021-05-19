package com.fiberhome.filink.stationserver.service.impl;

import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.service.StationService;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 中转服务实现类
 *
 * @author CongcaiYu
 */
@Log4j
@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private AbstractInstructSender sender;

    @Autowired
    private ProtocolUtil protocolUtil;

    /**
     * 发送udp
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @Override
    public void sendUdp(FiLinkReqParamsDto fiLinkReqParamsDto) {
        log.info("send udp data>>>>>>>>>>>");
        //构造请求帧参数
        FiLinkReqParams fiLinkReqParams = new FiLinkReqParams();
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        fiLinkReqParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        fiLinkReqParams.setParams(fiLinkReqParamsDto.getParams());
        fiLinkReqParams.setEquipmentId(fiLinkReqParamsDto.getEquipmentId());
        fiLinkReqParams.setToken(fiLinkReqParamsDto.getToken());
        fiLinkReqParams.setPhoneId(fiLinkReqParamsDto.getPhoneId());
        fiLinkReqParams.setAppKey(fiLinkReqParamsDto.getAppKey());
        sender.sendInstruct(fiLinkReqParams);
    }

    /**
     * 新增协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean addProtocol(ProtocolDto protocolDto) {
        //解析文件保存到redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * 更新协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean updateProtocol(ProtocolDto protocolDto) {
        //先清除老版本协议信息
        protocolUtil.deleteProtocolToRedis(protocolDto);
        //将新协议文件解析存入redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean deleteProtocol(List<ProtocolDto> protocolDtoList) {
        //删除协议信息
        for (ProtocolDto protocolDto : protocolDtoList) {
            protocolUtil.deleteProtocolToRedis(protocolDto);
        }
        return true;
    }


}
