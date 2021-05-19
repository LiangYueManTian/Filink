package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import lombok.Data;

import java.util.Date;

/**
 * 巡检工单特有信息
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 10:18
 */
@Data
public class ProcInspectionSpecial {

    /**
     * 巡检区域
     */
    private String inspectionAreaId;

    /**
     * 巡检区域名称
     */
    private String inspectionAreaName;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检任务记录编号
     */
    private String inspectionTaskRecordId;

    /**
     * 巡检任务开始时间
     */
    private Date inspectionStartTime;

    /**
     * 巡检任务结束时间
     */
    private Date inspectionEndTime;

    /**
     * 巡检数量
     */
    private Integer inspectionDeviceCount;

    /**
     * 巡检完成数量
     */
    private Integer inspectionCompletedCount;

    /**
     * 是否选中设施全集 0 不选择设施全集 1 选择设施全集
     */
    private String isSelectAll;

    /**
     * 是否删除
     */
    private String isDeleted;
}
