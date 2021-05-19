package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 设施top统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:53
 */
@Data
public class ProcDeviceTopStatistical {
    
    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 区域编号
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 设施数量
     */
    private Integer deviceCount;
}
