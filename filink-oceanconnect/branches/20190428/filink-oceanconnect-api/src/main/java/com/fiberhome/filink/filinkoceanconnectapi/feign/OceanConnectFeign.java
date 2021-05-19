package com.fiberhome.filink.filinkoceanconnectapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinkoceanconnectapi.bean.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectapi.bean.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto;
import com.fiberhome.filink.filinkoceanconnectapi.hystrix.OceanConnectHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * oceanConnect远程调用
 *
 * @author CongcaiYu
 */
@FeignClient(name = "filink-oceanconnect-server", path = "oceanConnect", fallback = OceanConnectHystrix.class)
public interface OceanConnectFeign {

    /**
     * 注册设备
     *
     * @param oceanDevice 设施实体
     * @return 操作结果
     */
    @PostMapping("/registry")
    Result registryDevice(@RequestBody OceanDevice oceanDevice);

    /**
     * 删除设施
     *
     * @param oceanDevice 设施信息
     * @return 操作结果
     */
    @PostMapping("/delete")
    Result deleteDevice(@RequestBody OceanDevice oceanDevice);

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

    /**
     * 更新平台地址
     *
     * @param httpsConfig https配置对象
     * @return 操作结果
     */
    @PostMapping("/updateHttpsConfig")
    boolean updateHttpsConfig(@RequestBody HttpsConfig httpsConfig);
}
