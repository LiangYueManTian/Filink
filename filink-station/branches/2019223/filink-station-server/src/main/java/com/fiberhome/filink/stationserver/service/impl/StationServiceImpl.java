package com.fiberhome.filink.stationserver.service.impl;

import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.stationserver.service.StationService;
import com.fiberhome.filink.stationserver.sender.AbstractInstructSender;
import com.fiberhome.filink.stationserver.util.lockenum.CmdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 中转服务实现类
 * @author CongcaiYu
 */
@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private AbstractInstructSender sender;

    /**
     * 发送udp
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @Override
    public void sendUdp(FiLinkReqParamsDto fiLinkReqParamsDto) {
        //构造请求帧参数
        FiLinkReqParams fiLinkReqParams = new FiLinkReqParams();
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        fiLinkReqParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        fiLinkReqParams.setParams(fiLinkReqParamsDto.getParams());
        fiLinkReqParams.setDeviceId(fiLinkReqParamsDto.getSerialNum());
        sender.sendInstruct(fiLinkReqParams);
    }
}
