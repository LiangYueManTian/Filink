package com.fiberhome.filink.stationserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.stationserver.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发送指令消费者
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkRequestReceiverAsync {

    @Autowired
    private StationService stationService;

    /**
     * udp请求消费者
     *
     * @param fiLinkReqParamsDtoListJson 请求参数json
     */
    @Async
    public void sendUdp(String fiLinkReqParamsDtoListJson) {
        log.info("receive udp cmd request : " + fiLinkReqParamsDtoListJson);
        //将json转成list
        List<FiLinkReqParamsDto> fiLinkReqParamsDtoList;
        try {
            fiLinkReqParamsDtoList = JSONObject.parseArray(fiLinkReqParamsDtoListJson, FiLinkReqParamsDto.class);
        } catch (Exception e) {
            log.error("filink reqParams convert failed>>>>>>>>>>>>>>>");
            return;
        }
        if (fiLinkReqParamsDtoList == null || fiLinkReqParamsDtoList.size() == 0) {
            log.info("filink request params list is null>>>>>>>>>>");
            return;
        }
        //循环请求参数发送指令
        for (FiLinkReqParamsDto fiLinkReqParamsDto : fiLinkReqParamsDtoList) {
            if (fiLinkReqParamsDto == null
                    || StringUtils.isEmpty(fiLinkReqParamsDto.getEquipmentId())
                    || StringUtils.isEmpty(fiLinkReqParamsDto.getCmdId())) {
                log.error("fiLinkReqParamsDto is null");
                continue;
            }
            //发送指令
            try {
                stationService.sendUdp(fiLinkReqParamsDto);
            }catch (Exception e){
                log.error("send udp msg failed: {}",e);
            }
        }
    }
}
