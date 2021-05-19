package com.fiberhome.filink.commonstation.entity.protocol;

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
     * 主控id
     */
    private String equipmentId;
    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 用户token
     */
    private String token;
    /**
     * 手机id
     */
    private String phoneId;
    /**
     * appKey
     */
    private Long appKey;
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * 请求参数
     */
    private Map<String, Object> params;
    /**
     * 产品id
     */
    private String appId;
    /**
     * imei
     */
    private String imei;
    /**
     * 平台id
     */
    private String plateFormId;
}
