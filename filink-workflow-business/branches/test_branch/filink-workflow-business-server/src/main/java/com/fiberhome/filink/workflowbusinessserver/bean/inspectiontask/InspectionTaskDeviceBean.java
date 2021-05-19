package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import lombok.Data;

/**
 * 巡检任务关联部门类
 * @author hedongwei@wistronits.com
 * @date 2019/3/5 22:40
 */
@Data
public class InspectionTaskDeviceBean extends InspectionTaskDevice{

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施关联区域名称
     */
    private String deviceAreaName;
}
