package com.fiberhome.filink.lockserver.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.filink.lockserver.constant.cmd.FiLinkReqParamsDto;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fiberhome.filink.lockserver.enums.PlatFormType.OceanConnect;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.OneNet;
import static com.fiberhome.filink.lockserver.enums.PlatFormType.UDP;

/**
 * <p>
 * 指令发送类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/6
 */
@Component("codeSender")
@Log4j
public class CodeSender {
    @Autowired
    private UdpSender udpSender;

    @Autowired
    private OneNetSender oneNetSender;

    @Autowired
    private OceanConnectSender oceanConnectSender;

    public void send(List<FiLinkReqParamsDto> reqParamsDtoList) {
        String listJson = "";
        Map<String, List<FiLinkReqParamsDto>> collect = reqParamsDtoList.stream().
                collect(Collectors.groupingBy(FiLinkReqParamsDto::getPlateForm));
        for (Map.Entry<String, List<FiLinkReqParamsDto>> entry : collect.entrySet()) {
            String plateForm = entry.getKey();
            List<FiLinkReqParamsDto> fiLinkReqParamsDtoList = entry.getValue();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                listJson = objectMapper.writeValueAsString(fiLinkReqParamsDtoList);
            } catch (Exception e) {
                log.error("json parse failed:{}", e);
            }
            if (plateForm.equals(OceanConnect.getCode())) {
                oceanConnectSender.send(listJson);
            } else if (plateForm.equals(OneNet.getCode())) {
                oneNetSender.send(listJson);
            } else if (plateForm.equals(UDP.getCode())) {
                udpSender.send(listJson);
            }

        }
    }
}
