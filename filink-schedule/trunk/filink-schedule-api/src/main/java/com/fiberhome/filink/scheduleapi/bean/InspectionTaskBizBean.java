package com.fiberhome.filink.scheduleapi.bean;

import lombok.Data;

import java.util.Date;

/**
 * 巡检任务业务bean
 * @author hedongwei@wistronits.com
 * @date 2019/3/8 19:20
 */
@Data
public class InspectionTaskBizBean {

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    private String jobTaskName;

    /**
     * 1-36 个月
     */
    private Integer taskPeriod;

    /**
     * 巡检任务开始时间
     */
    private Date taskStartTime;

    /**
     * 巡检任务开始时间戳
     */
    private Long startTime;

    /**
     * 任务分组
     */
    private String jobGroup;
}
