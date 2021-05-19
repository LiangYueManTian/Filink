package com.fiberhome.filink.stationserver.controller;

import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.stationserver.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * station控制器
 *
 * @author CongcaiYu
 */
@RestController
@Slf4j
public class StationController {

    @Autowired
    private StationService stationService;


    /**
     * 新增协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/addProtocol")
    public boolean addProtocol(@RequestBody ProtocolDto protocolDto) {
        //参数校验
        if (protocolDto == null || StringUtils.isEmpty(protocolDto.getFileHexData())) {
            log.info("file is null>>>>>>>");
            return false;
        }
        return stationService.addProtocol(protocolDto);
    }

    /**
     * 修改协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/updateProtocol")
    public boolean updateProtocol(@RequestBody ProtocolDto protocolDto) {
        //参数校验
        if (StringUtils.isEmpty(protocolDto.getFileHexData())
                || StringUtils.isEmpty(protocolDto.getHardwareVersion())
                || StringUtils.isEmpty(protocolDto.getSoftwareVersion())) {
            log.info("file or softwareVersion or hardwareVersion is null>>>>>>");
            return false;
        }
        return stationService.updateProtocol(protocolDto);
    }

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @PostMapping("/deleteProtocol")
    public boolean deleteProtocol(@RequestBody List<ProtocolDto> protocolDtoList) {
        //参数校验
        if (protocolDtoList == null || protocolDtoList.size() == 0) {
            log.info("protocol list is null>>>>>>>");
            return false;
        }
        for (ProtocolDto protocolDto : protocolDtoList) {
            if (protocolDto == null
                    || StringUtils.isEmpty(protocolDto.getSoftwareVersion())
                    || StringUtils.isEmpty(protocolDto.getHardwareVersion())) {
                log.info("softwareVersion or hardwareVersion is null>>>>>>>>>");
                return false;
            }
        }
        return stationService.deleteProtocol(protocolDtoList);
    }

}
