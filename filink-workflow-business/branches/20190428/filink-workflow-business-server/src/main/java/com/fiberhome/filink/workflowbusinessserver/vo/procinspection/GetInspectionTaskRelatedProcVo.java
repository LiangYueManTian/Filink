package com.fiberhome.filink.workflowbusinessserver.vo.procinspection;

import lombok.Data;

/**
 * 获取巡检任务关联工单
 * @author hedongwei@wistronits.com
 * @date 2019/3/28 17:22
 */
@Data
public class GetInspectionTaskRelatedProcVo {

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 工单名称
     */
    private String title;

    /**
     * 指派
     */
    private String assign;


    /**
     * 单据状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门名称
     */
    private StringBuilder accountabilityDeptName;

    /**
     * 责任人名字
     */
    private String assignName;

    /**
     * 设施类型
     */
    private StringBuilder deviceType;

    /**
     * 设施类型名字
     */
    private StringBuilder deviceTypeName;


    /**
     * 巡检任务开始时间时间戳
     */
    private Long inspectionStartTime;


    /**
     * 巡检任务结束时间时间戳
     */
    private Long inspectionEndTime;


    /**
     * 进度
     */
    private double progressSpeed;
}
