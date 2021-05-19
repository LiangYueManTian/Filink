package com.fiberhome.filink.workflowbusinessapi.req.procinspection;

import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDevice;
import lombok.Data;

import java.util.List;

/**
 * 新增巡检工单参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/11 16:55
 */
@Data
public class ProcInspectionReq {

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 流程类型
     */
    private String procType;

    /**
     * 标题
     */
    private String title;

    /**
     * 周期 巡检任务生成的周期，需要在创建时间加上周期就是巡检工单的结束时间（自动生成必填项）
     */
    private Integer taskPeriod;

    /**
     * 工单来源 1 手动新增  2 巡检任务新增 3 告警新增
     */
    private String procResourceType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否选中设施全集 0 不选择设施全集 1 选择设施全集
     */
    private String isSelectAll;

    /**
     * 巡检开始时间
     */
    private Long inspectionStartDate;

    /**
     * 巡检结束时间
     */
    private Long inspectionEndDate;

    /**
     * 巡检区域编号
     */
    private String inspectionAreaId;

    /**
     * 巡检区域名称
     */
    private String inspectionAreaName;

    /**
     * 设施集合
     */
    private List<ProcRelatedDevice> deviceList;

    /**
     * 单位集合
     */
    private List<ProcRelatedDepartment> deptList;

    /**
     * 关联告警id
     */
    private String refAlarm;

    /**
     * 关联告警名称
     */
    private String refAlarmName;
}
