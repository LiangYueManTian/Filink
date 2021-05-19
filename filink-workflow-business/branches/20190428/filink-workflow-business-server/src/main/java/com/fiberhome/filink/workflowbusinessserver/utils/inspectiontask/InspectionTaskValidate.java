package com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 巡检任务校验类
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 13:55
 */

public class InspectionTaskValidate {


    /**
     * 校验巡检任务编号
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:02
     * @param inspectionTaskId 巡检任务编号
     * @return 返回巡检任务编号校验结果
     */
    public static boolean checkInspectionTaskId(String inspectionTaskId) {
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionTaskId)) {
            return false;
        }

        //巡检任务字段长度
        Integer taskIdMinLength = 6;
        Integer taskIdMaxLength = 32;
        if (!ValidateUtils.validateDataLength(inspectionTaskId, taskIdMinLength, taskIdMaxLength)) {
            return false;
        }
        return true;
    }

    /**
     * 校验任务周期
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:08
     * @param taskPeriod 任务周期
     * @return 任务周期校验结果
     */
    public static boolean checkTaskPeriod(Integer taskPeriod) {
        if (ObjectUtils.isEmpty(taskPeriod)) {
            return false;
        }

        Integer taskPeriodMinValue = 1;
        Integer taskPeriodMaxValue = 36;

        if (taskPeriodMinValue > taskPeriod || taskPeriodMaxValue < taskPeriod) {
            return false;
        }
        return true;
    }

    /**
     * 校验巡检任务开始时间和结束时间
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:12
     * @param taskStartDate 巡检开始时间
     * @param taskEndDate 巡检结束时间
     * @return 校验结果
     */
    public static boolean checkStartTimeAndEndTime(Long taskStartDate, Long taskEndDate) {
        //巡检任务开始时间
        if (!ValidateUtils.validateDataInfoIsEmpty(taskStartDate)) {
            return false;
        }

        //巡检任务结束时间
        if (ValidateUtils.validateDataInfoIsEmpty(taskEndDate) && taskStartDate >= taskEndDate) {
            return false;
        }
        return true;
    }

    /**
     * 校验巡检工单期望用时
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:24
     * @param procPlanDate 巡检工单期望时间
     * @return 校验结果
     */
    public static boolean checkProcPlanDate(Integer procPlanDate) {

        if (ObjectUtils.isEmpty(procPlanDate)) {
            return false;
        }

        Integer procPlanDateMinValue = 1;
        Integer procPlanDateMaxValue = 365;
        if (procPlanDateMinValue > procPlanDate || procPlanDateMaxValue < procPlanDate) {
            return false;
        }
        return true;
    }

    /**
     * 校验巡检区域编号
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:35
     * @param inspectionAreaId 巡检区域编号
     * @return 校验结果
     */
    public static boolean checkInspectionAreaId(String inspectionAreaId) {
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionAreaId)) {
            return false;
        }
        Integer inspectionAreaIdMinLength = 1;
        Integer inspectionAreaIdMaxLength = 32;
        if (!ValidateUtils.validateDataLength(inspectionAreaId, inspectionAreaIdMinLength, inspectionAreaIdMaxLength)) {
            return false;
        }
        return true;
    }

    /**
     * 校验巡检任务关联部门
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:43
     * @param inspectionTaskDepartments 巡检任务关联部门
     * @return 返回校验结果
     */
    public static boolean checkDepartmentList(List<InspectionTaskDepartment> inspectionTaskDepartments) {
        if (ObjectUtils.isEmpty(inspectionTaskDepartments)) {
            return false;
        }
        return true;
    }

    /**
     * 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 14:50
     * @param inspectionTaskDeviceList
     * @return 校验结果
     */
    public static boolean checkDeviceList(List<InspectionTaskDevice> inspectionTaskDeviceList) {
        if (ObjectUtils.isEmpty(inspectionTaskDeviceList)) {
            return false;
        }
        return true;
    }

}
