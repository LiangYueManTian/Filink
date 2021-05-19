package com.fiberhome.filink.stationserver.sender;

import com.fiberhome.filink.stationserver.entity.param.AbstractReqParams;

/**
 * udp请求帧解析接口
 * @author CongcaiYu
 */
public interface RequestResolver {

    /**
     * 解析请求帧
     *
     * @param udpParams 请求帧参数
     * @return 16进制请求帧
     */
    String resolveUdpReq(AbstractReqParams udpParams);
}
