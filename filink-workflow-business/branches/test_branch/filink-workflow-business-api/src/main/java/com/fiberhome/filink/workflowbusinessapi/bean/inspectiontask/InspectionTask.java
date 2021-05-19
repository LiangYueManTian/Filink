package com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 巡检任务表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Data
public class InspectionTask {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

    /**
     * 巡检任务状态 1 未巡检 2 巡检中 3 已完成
     */
    private String inspectionTaskStatus;

    /**
     * 巡检任务类型
     */
    private String inspectionTaskType;

    /**
     * 巡检周期
     */
    private Integer taskPeriod;

    /**
     * 巡检工单期望用时
     */
    private Integer procPlanDate;

    /**
     * 巡检任务开始时间
     */
    private Date taskStartTime;

    /**
     * 巡检任务结束时间
     */
    private Date taskEndTime;

    /**
     * 是否启用巡检任务 0 禁用 1 启用
     */
    private String isOpen;

    /**
     * 是否选择全集
     */
    private String isSelectAll;

    /**
     * 巡检区域编号
     */
    private String inspectionAreaId;

    /**
     * 巡检设施总数
     */
    private Integer inspectionDeviceCount;

    /**
     * 是否删除
     */
    private String isDeleted;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;
}
