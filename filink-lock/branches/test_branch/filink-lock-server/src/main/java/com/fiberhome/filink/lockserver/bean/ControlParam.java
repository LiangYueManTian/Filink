package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;

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
     * 主控类型  0-无源锁 1-机械锁芯 2-电子锁芯
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
     * 供电方式  适配器”0”，可充电电池”1”，不可充电电池”2”
     */
    private String sourceType;
    /**
     * 太阳能 已安装“0”，”未安装”1”，”不支持”2”
     */
    private String solarCell;
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
     * 门锁映射关系
     */
    private List<Lock> lockList;

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
     * 创建时间
     */
    private Long currentTime;

    /**
     * 检查更新设施状态的参数
     */
    public void checkParamsForUpdateDeviceStatus() {
        if (this == null
                || StringUtils.isEmpty(this.getHostId())
                || StringUtils.isEmpty(this.getDeviceStatus())) {
            throw new FiLinkControlException("controlId or deviceStatus is empty>>>");
        }
    }

    /**
     * 检查更新部署状态的参数
     */
    public void checkParamsForUpdateDeployStatus() {
        if (this == null
                || StringUtils.isEmpty(this.getHostId())
                || StringUtils.isEmpty(this.getDeployStatus())) {
            throw new FiLinkControlException("controlId or deployStatus is empty>>>");
        }
    }

    /**
     * 检查更新主控的参数
     */
    public void checkUpdateParams() {
        if (this == null
                || StringUtils.isEmpty(this.getHostId())) {
            throw new FiLinkControlException("controlId  is empty>>>");
        }
    }
}
