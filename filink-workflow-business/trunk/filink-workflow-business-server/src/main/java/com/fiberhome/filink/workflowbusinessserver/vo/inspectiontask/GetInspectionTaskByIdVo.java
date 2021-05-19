package com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartmentBean;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDeviceBean;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 获取巡检任务详情返回类
 * @author hedongwei@wistronits.com
 * @date 2019/3/5 18:04
 */
@Data
public class GetInspectionTaskByIdVo {

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

    /**
     * 巡检任务状态
     */
    private String inspectionTaskStatus;

    /**
     * 巡检周期
     */
    private Integer taskPeriod;

    /**
     * 计划完成时间
     */
    private Integer procPlanDate;

    /**
     * 巡检开始时间
     */
    private Date taskStartTime;

    /**
     * 巡检开始时间戳
     */
    private Long startTime;

    /**
     * 巡检结束时间
     */
    private Date taskEndTime;

    /**
     * 巡检结束时间戳
     */
    private Long endTime;

    /**
     * 是否开启巡检任务
     */
    private String isOpen;

    /**
     * 是否选中所有设施
     */
    private String isSelectAll;

    /**
     * 巡检区域编号
     */
    private String inspectionAreaId;

    /**
     * 巡检区域名称
     */
    private String inspectionAreaName;

    /**
     * 巡检设施数量
     */
    private String inspectionDeviceCount;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 部门名称
     */
    private String accountabilityDeptName;

    /**
     * 巡检设施集合
     */
    private List<InspectionTaskDeviceBean> deviceList;

    /**
     * 巡检部门集合
     */
    private List<InspectionTaskDepartmentBean> deptList;
}
