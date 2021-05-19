package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 电子锁主控信息 for pda 查询电子锁主控信息
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/8
 */
@Data
public class ControlParamForControl {
    /**
     * 主控id
     */
    private String hostId;
    /**
     * 主控名称
     */
    private String hostName;
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
     * 锁类型（主控类型）
     */
    private String hostType;
    /**
     * 供电方式
     */
    private String sourceType;
    /**
     * 产品id
     */
    private String productId;
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
     * 实际传感值
     */
    private String actualValue;

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

    /**
     * 门锁映射关系
     */
    private List<DoorForPda> lockList;

}
