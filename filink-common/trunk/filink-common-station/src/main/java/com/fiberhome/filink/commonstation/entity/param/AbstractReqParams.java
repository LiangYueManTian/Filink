package com.fiberhome.filink.commonstation.entity.param;

import com.fiberhome.filink.commonstation.entity.xmlbean.AbstractProtocolBean;
import lombok.Data;

import java.util.Map;

/**
 * udp请求帧参数抽象类
 * @author CongcaiYu
 */
@Data
public abstract class AbstractReqParams {
    /**
     * 主控id
     */
    private String equipmentId;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 请求类型
     */
    private Integer cmdType;
    /**
     * 流水号
     */
    private Integer serialNumber;
    /**
     * 协议实体类
     */
    private AbstractProtocolBean protocolBean;
    /**
     * 请求参数
     */
    private Map<String,Object> params;
    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;

}
