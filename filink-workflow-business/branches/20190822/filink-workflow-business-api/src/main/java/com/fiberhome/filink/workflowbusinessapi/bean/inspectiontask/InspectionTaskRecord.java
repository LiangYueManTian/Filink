package com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 巡检任务记录表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Data
public class InspectionTaskRecord {

    private static final long serialVersionUID = 1L;


    private String inspectionTaskRecordId;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

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
     * 巡检区域编号
     */
    private String inspectionAreaId;


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
