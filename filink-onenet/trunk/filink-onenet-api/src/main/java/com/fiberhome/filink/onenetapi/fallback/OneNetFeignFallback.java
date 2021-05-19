package com.fiberhome.filink.onenetapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.onenetapi.bean.CreateDevice;
import com.fiberhome.filink.onenetapi.bean.DeleteDevice;
import com.fiberhome.filink.onenetapi.bean.HostBean;
import com.fiberhome.filink.onenetapi.bean.ProtocolDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *   oneNet服务全程调用熔断
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Slf4j
@Component
public class OneNetFeignFallback implements OneNetFeign {

    /**
     * 更新平台域名
     *
     * @param hostBean 平台域名
     * @return 操作结果
     */
    @Override
    public boolean updateOneNetHost(HostBean hostBean) {
        log.warn("update http config hystrix, host:{}", hostBean.getHost());
        return false;
    }

    /**
     * 新增设备
     *
     * @param createDevice 设备信息 title，imei，imsi，productId为必填信息
     * @return 设备信息
     */
    @Override
    public Result createDevice(CreateDevice createDevice) {
        log.warn("create createDevice hystrix, name:{}", createDevice.getTitle());
        return ResultUtils.warn(ResultCode.FAIL, "create failed");
    }

    /**
     * 删除设备
     *
     * @param deleteDevice 设备 deviceId，productId必填
     * @return true 成功 false失败
     */
    @Override
    public Result deleteDevice(DeleteDevice deleteDevice) {
        log.warn("delete device hystrix, device id:{}", deleteDevice.getDeviceId());
        return ResultUtils.warn(ResultCode.FAIL, "delete failed");
    }

    /**
     * 新增协议熔断处理
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean addProtocol(ProtocolDto protocolDto) {
        log.warn("add protocol hystrix, SoftwareVersion:{}, HardwareVersion:{}",
                protocolDto.getSoftwareVersion(), protocolDto.getHardwareVersion());
        return false;
    }

    /**
     * 更新协议熔断处理
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean updateProtocol(ProtocolDto protocolDto) {
        log.warn("update protocol hystrix, SoftwareVersion:{}, HardwareVersion:{}",
                protocolDto.getSoftwareVersion(), protocolDto.getHardwareVersion());
        return false;
    }

    /**
     * 删除协议熔断处理
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @Override
    public boolean deleteProtocol(List<ProtocolDto> protocolDtoList) {
        log.warn("delete protocol hystrix");
        return false;
    }
}
