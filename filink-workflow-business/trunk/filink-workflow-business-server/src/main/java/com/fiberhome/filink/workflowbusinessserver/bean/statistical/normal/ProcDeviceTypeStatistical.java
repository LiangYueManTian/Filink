package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 工单设施类型统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:53
 */
@Data
public class ProcDeviceTypeStatistical extends ProcStatisticalBase {

    /**
     * 设施类型
     */
    private String deviceType;
}
