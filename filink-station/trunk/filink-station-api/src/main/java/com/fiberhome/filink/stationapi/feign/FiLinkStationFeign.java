package com.fiberhome.filink.stationapi.feign;

import com.fiberhome.filink.stationapi.bean.ProtocolDto;
import com.fiberhome.filink.stationapi.hystrix.FiLinkStationHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 数据中转feign
 * @author CongcaiYu
 */
@FeignClient(name = "filink-station-server", fallback = FiLinkStationHystrix.class)
public interface FiLinkStationFeign {


    /**
     * 新增协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/addProtocol")
    boolean addProtocol(@RequestBody ProtocolDto protocolDto);


    /**
     * 修改协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/updateProtocol")
    boolean updateProtocol(@RequestBody ProtocolDto protocolDto);


    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @PostMapping("/deleteProtocol")
    boolean deleteProtocol(@RequestBody List<ProtocolDto> protocolDtoList);
}
