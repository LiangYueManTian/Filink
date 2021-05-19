package com.fiberhome.filink.filinklockapi.bean;


import lombok.Data;

import java.sql.Timestamp;

/**
 * <p>
 * 电子锁主控信息
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/28
 */
@Data
public class ControlParam {
    /**
     * 主键
     */
    private String controlId;
    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 主控id
     */
    private String hostId;
    /**
     * 主控名称
     */
    private String hostName;
    /**
     * 主控类型  无源锁 - 0 ，有源锁 - 1
     */
    private String hostType;
    /**
     * IP
     */
    private String hostIp;
    /**
     * 端口
     */
    private String hostPort;
    /**
     * 蓝牙mac地址
     */
    private String macAddr;
    /**
     * 供电方式
     */
    private String sourceType;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 云平台id
     */
    private String platformId;
    /**
     * 云平台类型
     */
    private String cloudPlatform;
    /**
     * SIM卡类型
     */
    private String simCardType;
    /**
     * IMEI号
     */
    private String imei;
    /**
     * IMSI号
     */
    private String imsi;
    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 门数量
     */
    private String doors;

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
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 部署状态
     */
    private String deployStatus;

    /**
     * 激活状态
     */
    private String activeStatus;

    /**
     * 版本更新时间
     */
    private Long versionUpdateTime;


}
