package com.fiberhome.filink.onenetapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.onenetapi.bean.CreateDevice;
import com.fiberhome.filink.onenetapi.bean.DeleteDevice;
import com.fiberhome.filink.onenetapi.bean.HostBean;
import com.fiberhome.filink.onenetapi.bean.ProtocolDto;
import com.fiberhome.filink.onenetapi.fallback.OneNetFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 *   oneNet服务全程调用
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@FeignClient(name = "filink-onenet-server", path = "/oneNet", fallback = OneNetFeignFallback.class)
public interface OneNetFeign {
    /**
     * 更新平台域名
     *
     * @param hostBean 平台域名
     * @return 操作结果
     */
    @PostMapping("/updateOneNetHost")
    boolean updateOneNetHost(@RequestBody HostBean hostBean);
    /**
     * 新增设备
     * @param createDevice 设备信息 title，imei，imsi，productId为必填信息
     * @return 设备信息
     */
    @PostMapping("/createDevice")
    Result createDevice(@RequestBody CreateDevice createDevice);
    /**
     * 删除设备
     * @param deleteDevice 设备 deviceId，productId必填
     * @return true 成功 false失败
     */
    @PostMapping("/deleteDevice")
    Result deleteDevice(@RequestBody DeleteDevice deleteDevice);

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
