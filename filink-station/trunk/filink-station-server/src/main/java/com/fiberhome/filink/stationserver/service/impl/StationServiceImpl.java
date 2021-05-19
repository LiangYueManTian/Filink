package com.fiberhome.filink.stationserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.service.StationService;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //如果是配置下发指令,更新redis主控信息
        if (CmdId.SET_CONFIG.equals(fiLinkReqParamsDto.getCmdId())) {
            updateRedisControl(fiLinkReqParamsDto.getEquipmentId(), fiLinkReqParamsDto.getParams());
        }
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
     * 更新redis主控信息
     *
     * @param equipmentId 主控id
     * @param params      配置参数信息
     */
    private void updateRedisControl(String equipmentId, Map<String, Object> params) {
        List<Map<String, Object>> paramsList = (List<Map<String, Object>>) params.get(ParamsKey.PARAMS_KEY);
        if (paramsList == null || paramsList.size() == 0) {
            log.error("set config params list is null");
            return;
        }
        Map<String, Object> configMap = new HashMap<>(64);
        for (Map<String, Object> paramMap : paramsList) {
            String dataClass = (String) paramMap.get(ParamsKey.DATA_CLASS);
            Object data = paramMap.get(ParamsKey.DATA);
            configMap.put(dataClass, data);
        }
        ControlParam control = protocolUtil.getControlById(equipmentId);
        control.setConfigValue(JSONObject.toJSONString(configMap));
        RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, control);
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
