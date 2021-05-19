package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

/**
 * 主控实体类
 * @author CongcaiYu
 */
@Data
public class Control {
    /**
     * 主控id
     */
    private String controlId;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 设施序列id
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
     * 配置策略值
     */
    private String configValue;
    /**
     * 实际传感值
     */
    private String actualValue;
    /**
     * 同步状态1:未同步 2:同步
     */
    private String syncStatus;
    /**
     * 主控类型
     */
    private String controlType;
    /**
     * 心跳时间
     */
    private Long heartBeatTime;
    /**
     * 更新时间
     */
    private Long updateTime;

}
