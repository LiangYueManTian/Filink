package com.fiberhome.filink.stationapi.hystrix;

import com.fiberhome.filink.stationapi.bean.ProtocolDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * filink数据中转熔断类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class FiLinkStationHystrix implements FiLinkStationFeign {


    /**
     * 新增协议熔断处理
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean addProtocol(ProtocolDto protocolDto) {
        String msg = "add protocol failed: " + protocolDto.getSoftwareVersion()+protocolDto.getHardwareVersion();
        log.info(msg);
        return false;
    }

    /**
     * 更新协议熔断处理
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean updateProtocol(ProtocolDto protocolDto) {
        String msg = "update protocol failed: " + protocolDto.getSoftwareVersion()+protocolDto.getHardwareVersion();
        log.info(msg);
        return false;
    }

    /**
     * 删除协议熔断处理
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean deleteProtocol(List<ProtocolDto> protocolDtoList) {
        String msg = "delete protocol failed";
        log.info(msg);
        return false;
    }
}
