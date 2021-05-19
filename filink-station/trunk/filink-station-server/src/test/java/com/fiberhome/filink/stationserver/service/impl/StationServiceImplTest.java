package com.fiberhome.filink.stationserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * station service测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class StationServiceImplTest {

    @Tested
    private StationServiceImpl stationService;

    @Injectable
    private AbstractInstructSender sender;

    @Injectable
    private ProtocolUtil protocolUtil;

    @Mocked
    private RedisUtils redisUtils;

    @Mocked
    private JSONObject jsonObject;

    @Test
    public void sendUdp() {
        //开锁请求
        Map<String, Object> unlockParam = new HashMap<>(64);
        unlockParam.put("slotNum", "1");
        unlockParam.put("operate", "0");
        Map<String, Object> unlockMap = new HashMap<>(64);
        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(unlockParam);
        unlockMap.put("params", paramList);
        FiLinkReqParamsDto fiLinkReqParamsDto = new FiLinkReqParamsDto();
        fiLinkReqParamsDto.setEquipmentId("0101CFED4C0400000000");
        fiLinkReqParamsDto.setToken("4a2f824d-c813-4451-b340-15348ce09a6e");
        fiLinkReqParamsDto.setCmdId(CmdId.UNLOCK);
        fiLinkReqParamsDto.setParams(unlockMap);
        stationService.sendUdp(fiLinkReqParamsDto);

        //配置策略下发
        //温度
        ControlParam controlParam = new ControlParam();
        Map<String, Object> tmpParam = new HashMap<>(64);
        tmpParam.put("dataClass", "highTemperature");
        tmpParam.put("data", "12");
        //电量
        Map<String, Object> elecParam = new HashMap<>(64);
        elecParam.put("dataClass", "electricity");
        elecParam.put("data", "1563722676");
        //map集合参数
        List<Map<String, Object>> dataParamList = new ArrayList<>();
        dataParamList.add(elecParam);
        dataParamList.add(tmpParam);
        Map<String, Object> dataMap = new HashMap<>(64);
        dataMap.put("params", dataParamList);
        FiLinkReqParamsDto setConfigParamDto = new FiLinkReqParamsDto();
        setConfigParamDto.setEquipmentId("0101CFED4C0400000000");
        setConfigParamDto.setToken("4a2f824d-c813-4451-b340-15348ce09a6e");
        setConfigParamDto.setCmdId(CmdId.SET_CONFIG);
        setConfigParamDto.setParams(dataMap);
        new Expectations() {
            {
                protocolUtil.getControlById("0101CFED4C0400000000");
                result = controlParam;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }

        };
        stationService.sendUdp(setConfigParamDto);
    }

    @Test
    public void addProtocol() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setFileHexData("fileHexData");
        protocolDto.setHardwareVersion("NRF52840_elock");
        protocolDto.setSoftwareVersion("RP9003.004Z.bin");
        new Expectations() {
            {
                protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
            }
        };
        stationService.addProtocol(protocolDto);
    }

    @Test
    public void updateProtocol() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setFileHexData("fileHexData");
        protocolDto.setHardwareVersion("NRF52840_elock");
        protocolDto.setSoftwareVersion("RP9003.004Z.bin");
        new Expectations() {
            {
                protocolUtil.deleteProtocolToRedis(protocolDto);

            }

            {
                protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
            }
        };
        stationService.updateProtocol(protocolDto);
    }

    @Test
    public void deleteProtocol() {
        ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setFileHexData("fileHexData");
        protocolDto.setHardwareVersion("NRF52840_elock");
        protocolDto.setSoftwareVersion("RP9003.004Z.bin");
        List<ProtocolDto> protocolDtoList = new ArrayList<>();
        protocolDtoList.add(protocolDto);
        new Expectations() {
            {
                protocolUtil.deleteProtocolToRedis(protocolDto);
            }
        };
        stationService.deleteProtocol(protocolDtoList);
    }
}