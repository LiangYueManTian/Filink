package com.fiberhome.filink.stationserver.entity.param;

import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import lombok.Data;

import java.util.Map;

/**
 * udp请求帧参数抽象类
 * @author CongcaiYu
 */
@Data
public abstract class AbstractReqParams {
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 协议实体类
     */
    private AbstractProtocolBean protocolBean;
    /**
     * 请求参数
     */
    private Map<String,Object> params;

}
