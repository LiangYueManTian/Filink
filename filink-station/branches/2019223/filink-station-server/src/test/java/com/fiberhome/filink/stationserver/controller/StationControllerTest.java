package com.fiberhome.filink.stationserver.controller;

import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.stationserver.service.StationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * stationController测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class StationControllerTest {

    @InjectMocks
    private StationController stationController;

    @Mock
    private StationService stationService;

    @Before
    public void setUp() {

    }

    @Test
    public void send() {
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
        stationController.send(reqParamsDto);
    }
}