package com.fiberhome.filink.onenetserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.onenetserver.bean.device.CreateDevice;
import com.fiberhome.filink.onenetserver.bean.device.DeleteDevice;
import com.fiberhome.filink.onenetserver.bean.device.HostBean;

import java.util.List;

/**
 * <p>
 *   oneNet平台Lwm2m协议API服务层
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-25
 */
public interface OneNetService {
    /**
     * 更新平台域名
     *
     * @param hostBean 平台域名
     * @return 操作结果
     */
    boolean updateOneNetHost(HostBean hostBean);
    /**
     * 新增设备
     *
     * @param createDevice 设备信息 title，imei，imsi，productId，accessKey为必填信息
     * @return 设备信息
     */
    Result createDevice(CreateDevice createDevice);
    /**
     * 删除设备
     * @param deleteDevice 设备 deviceId，productId，accessKey必填
     * @return true 成功 false失败
     */
    Result deleteDevice(DeleteDevice deleteDevice);
    /**
     * 新增协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    boolean addProtocol(ProtocolDto protocolDto);

    /**
     * 更新协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    boolean updateProtocol(ProtocolDto protocolDto);

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输对象
     * @return 操作结果
     */
    boolean deleteProtocol(List<ProtocolDto> protocolDtoList);
    /**
     * 下发命令
     * @param fiLinkReqParamsDto imei, objId, objInstId, resId，productId，accessKey必填
     */
    void write(FiLinkReqParamsDto fiLinkReqParamsDto);
}
