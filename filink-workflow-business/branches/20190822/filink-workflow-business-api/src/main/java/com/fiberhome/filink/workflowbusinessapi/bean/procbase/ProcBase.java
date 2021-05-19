package com.fiberhome.filink.workflowbusinessapi.bean.procbase;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 流程主表
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-02-18
 */
@Data
public class ProcBase {

    private static final long serialVersionUID = 1L;

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 流程类型
     */
    private String procType;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 责任人
     */
    private String assign;

    /**
     * 单位类型 0 人 1 部门
     */
    private String deptType;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 退单原因
     */
    private String singleBackReason;

    /**
     * 转派原因
     */
    private String turnReason;

    /**
     * 处理方案
     */
    private String processingScheme;

    /**
     * 期望完工时间
     */
    private Date expectedCompletedTime;

    /**
     * 实际完工时间
     */
    private Date realityCompletedTime;

    /**
     * 故障原因
     */
    private String errorReason;

    /**
     * 工单来源 1 手动新增  2 巡检任务新增 3 告警新增
     */
    private String procResourceType;

    /**
     * 工单超时是否已经通知   0未通知 1 已通知
     */
    private String isTold;

    /**
     * 关联告警编号
     */
    private String refAlarm;

    /**
     * 关联告警名称
     */
    private String refAlarmName;

    /**
     * 确认退单 0 未确认 1 已确认
     */
    private String isCheckSingleBack;

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
