package com.fiberhome.filink.stationserver.entity.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * filink 请求帧参数
 * @author CongcaiYu
 */
@Data
public class FiLinkReqParamsDto implements Serializable {

    /**
     * 设施id
     */
    private String serialNum;
    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 请求参数
     */
    private Map<String, Object> params;
}
