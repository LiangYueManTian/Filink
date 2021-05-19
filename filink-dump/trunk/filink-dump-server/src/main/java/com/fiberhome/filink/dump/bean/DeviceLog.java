package com.fiberhome.filink.dump.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 设施日志实体类
 * @author CongcaiYu@wistronits.com
 */
@Data
public class DeviceLog {
    /**
     * 主键
     */
    @Id
    private String logId;
    /**
     * 设施日志名称
     */
    private String logName;
    /**
     * 类型
     */
    private String type;
    /**
     * 日志类型
     */
    private String logType;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 设施编码
     */
    private String deviceCode;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 节点对象
     */
    private String nodeObject;
    /**
     * 区域id
     */
    private String areaId;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 创建时间
     */
    private Long currentTime;
    /**
     * 备注
     */
    private String remarks;

}
