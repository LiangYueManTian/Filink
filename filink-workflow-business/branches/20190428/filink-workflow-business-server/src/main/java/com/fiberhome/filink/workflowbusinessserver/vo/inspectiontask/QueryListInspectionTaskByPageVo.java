package com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * 查询巡检任务列表返回数据类
 * @author hedongwei@wistronits.com
 * @date 2019/3/5 14:43
 */
@Data
public class QueryListInspectionTaskByPageVo {

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
     * 巡检任务类型 1 例行巡检
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
     * 巡检任务开始时间时间戳
     */
    private Long startTime;

    /**
     * 巡检任务结束时间
     */
    private Date taskEndTime;

    /**
     * 巡检任务结束时间时间戳
     */
    private Long endTime;

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
     * 巡检区域名称
     */
    private String inspectionAreaName;

    /**
     * 巡检部门名称
     */
    private String accountabilityDeptName;

    /**
     * 巡检设施总数
     */
    private Integer inspectionDeviceCount;


    /**
     * 开始时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationStartTime() {
        if (ObjectUtils.isEmpty(this.startTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.startTime);
        }
    }

    /**
     * 结束时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationEndTime() {
        if (ObjectUtils.isEmpty(this.endTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.endTime);
        }
    }


    /**
     * 巡检任务状态（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationInspectionTaskStatus() {
        String inspectionTaskName = "";
        if (InspectionTaskConstants.INSPECTION_TASK_WAIT.equals(this.inspectionTaskStatus)) {
            //巡检状态未巡检
            inspectionTaskName = I18nUtils.getString(InspectionTaskI18n.INSPECTION_STATUS_WAIT);
        } else if (InspectionTaskConstants.INSPECTION_TASK_DOING.equals(this.inspectionTaskStatus)) {
            //巡检状态巡检中
            inspectionTaskName = I18nUtils.getString(InspectionTaskI18n.INSPECTION_STATUS_DOING);
        } else if (InspectionTaskConstants.INSPECTION_TASK_COMPLETED.equals(this.inspectionTaskStatus)) {
            //巡检状态已完成
            inspectionTaskName = I18nUtils.getString(InspectionTaskI18n.INSPECTION_STATUS_COMPLETED);
        }
        return inspectionTaskName;
    }

    /**
     * 巡检任务是否启用（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationIsOpen() {
        String translationIsOpen = "";
        if (InspectionTaskConstants.IS_OPEN.equals(this.isOpen)) {
            //巡检任务启用
            translationIsOpen = I18nUtils.getString(InspectionTaskI18n.IS_OPEN);
        } else if (InspectionTaskConstants.IS_CLOSE.equals(this.isOpen)) {
            //巡检任务禁用
            translationIsOpen = I18nUtils.getString(InspectionTaskI18n.IS_CLOSE);
        }
        return translationIsOpen;
    }


    /**
     * 巡检任务类型（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationInspectionTaskType() {
        //巡检任务类型
        String translationInspectionTaskType = "";
        if (InspectionTaskConstants.TASK_TYPE_ROUTINE_INSPECTION.equals(this.inspectionTaskType)) {
            //例行巡检
            translationInspectionTaskType = I18nUtils.getString(InspectionTaskI18n.TASK_TYPE_ROUTINE_INSPECTION);
        }
        return translationInspectionTaskType;
    }

}
