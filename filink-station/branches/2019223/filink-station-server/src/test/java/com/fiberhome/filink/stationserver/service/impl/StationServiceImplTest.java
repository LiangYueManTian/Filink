package com.fiberhome.filink.stationserver.service.impl;

import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.stationserver.sender.AbstractInstructSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class StationServiceImplTest {

    @InjectMocks
    private StationServiceImpl stationService;

    @Mock
    private AbstractInstructSender sender;

    @Test
    public void sendUdp() {
        FiLinkReqParamsDto reqParamsDto = new FiLinkReqParamsDto();
        reqParamsDto.setCmdId("0x2201");
        reqParamsDto.setSerialNum("13172750");
        Map<String,Object> params = new HashMap<>();
        List<Map<String,Object>> paramList = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        param.put("operate","0");
        param.put("slotNum","1");
        paramList.add(param);
        params.put("params",paramList);
        reqParamsDto.setParams(params);
        stationService.sendUdp(reqParamsDto);
    }
}