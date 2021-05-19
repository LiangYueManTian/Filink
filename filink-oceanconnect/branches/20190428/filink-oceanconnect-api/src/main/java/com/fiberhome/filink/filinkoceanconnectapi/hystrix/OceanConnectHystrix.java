package com.fiberhome.filink.filinkoceanconnectapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.filinkoceanconnectapi.bean.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectapi.bean.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * oceanConnect熔断类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class OceanConnectHystrix implements OceanConnectFeign {

    /**
     * 注册设备
     *
     * @param oceanDevice 设施实体
     * @return 返回结果
     */
    @Override
    public Result registryDevice(OceanDevice oceanDevice) {
        log.error("registry device hystrix>>>>>>>>");
        return ResultUtils.warn(ResultCode.FAIL, "registry failed");
    }

    /**
     * 删除设施
     *
     * @param oceanDevice 设施信息
     * @return 返回结果
     */
    @Override
    public Result deleteDevice(OceanDevice oceanDevice) {
        log.error("delete device hystrix>>>>>>>>");
        return ResultUtils.warn(ResultCode.FAIL, "registry failed");
    }

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


    /**
     * 更新平台地址
     * @param httpsConfig https配置对象
     * @return 操作结果
     */
    @Override
    public boolean updateHttpsConfig(HttpsConfig httpsConfig) {
        log.error("update https config hystrix>>>>");
        return false;
    }
}
