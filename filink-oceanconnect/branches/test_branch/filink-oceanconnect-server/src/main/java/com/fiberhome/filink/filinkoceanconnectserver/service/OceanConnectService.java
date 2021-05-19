package com.fiberhome.filink.filinkoceanconnectserver.service;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.filinkoceanconnectserver.entity.https.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.ReceiveBean;

import java.util.List;

/**
 * 中转服务service
 *
 * @author CongcaiYu
 */
public interface OceanConnectService {

    /**
     * 指令下发
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    void sendCmd(FiLinkReqParamsDto fiLinkReqParamsDto);

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
     * 注册设施
     *
     * @param oceanDevice 设施实体
     * @return 操作结果
     */
    Result registry(OceanDevice oceanDevice);

    /**
     * 接收订阅消息
     *
     * @param receiveBean 接收实体
     */
    void receiveMsg(ReceiveBean receiveBean);

    /**
     * 删除设施
     *
     * @param oceanDevice 设施实体
     * @return 返回结果
     */
    Result deleteDevice(OceanDevice oceanDevice);

    /**
     * 更新平台地址
     *
     * @param httpsConfig https配置对象
     * @return 操作结果
     */
    boolean updateHttpsConfig(HttpsConfig httpsConfig);
}
