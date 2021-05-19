package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask;

import com.fiberhome.filink.bean.CheckInputString;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.utils.common.InspectionCommonValidate;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskValidate;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 新增巡检任务请求参数信息
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 11:32
 */
@Data
public class InsertInspectionTaskReq {

    /**
     * 巡检任务名称 1-32位字符
     */
    private String inspectionTaskName;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检周期  1-36 月
     */
    private Integer taskPeriod;

    /**
     * 计划完成时间 单位为天
     */
    private Integer procPlanDate;

    /**
     * 巡检任务开始时间
     */
    private Long taskStartDate;

    /**
     * 巡检任务结束时间
     */
    private Long taskEndDate;


    /**
     * 巡检区域编号
     */
    private String inspectionAreaId;


    /**
     * 巡检任务关联单位
     */
    private List<InspectionTaskDepartment> departmentList;

    /**
     * 巡检任务关联设施
     */
    private List<InspectionTaskDevice> deviceList;

    /**
     * 是否开启巡检任务
     */
    private String isOpen;

    /**
     * 是否选择设施全集
     */
    private String isSelectAll;


    /**
     * 新增巡检任务校验
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 17:26
     * @param req 新增巡检任务参数
     * @return Result 返回值
     */
    public static boolean validateInsertInspectionTaskReq(InsertInspectionTaskReq req) {

        //巡检任务名称
        String inspectionTaskName = req.getInspectionTaskName();
        String procTaskName = CheckInputString.nameCheck(inspectionTaskName);
        if (StringUtils.isEmpty(procTaskName)){
            return false;
        }
        req.setInspectionTaskName(inspectionTaskName);


        //巡检周期
        Integer taskPeriod = req.getTaskPeriod();
        if (!InspectionTaskValidate.checkTaskPeriod(taskPeriod)) {
            return false;
        }

        //巡检工单期望用时
        Integer procPlanDate = req.getProcPlanDate();
        if (!InspectionTaskValidate.checkProcPlanDate(procPlanDate)) {
            return false;
        }

        //巡检任务开始和结束时间
        Long taskStartDate = req.getTaskStartDate();
        Long taskEndDate = req.getTaskEndDate();
        if (!InspectionTaskValidate.checkStartTimeAndEndTime(taskStartDate, taskEndDate)) {
            return false;
        }

        //巡检区域编号
        String inspectionAreaId = req.getInspectionAreaId();
        if (!InspectionTaskValidate.checkInspectionAreaId(inspectionAreaId)) {
            return false;
        }

        //巡检任务关联单位
        List<InspectionTaskDepartment> inspectionTaskDepartments = req.getDepartmentList();
        if (!InspectionTaskValidate.checkDepartmentList(inspectionTaskDepartments)) {
            return false;
        }

        //巡检任务设施
        List<InspectionTaskDevice> inspectionTaskDeviceList = req.getDeviceList();
        if (!InspectionTaskValidate.checkDeviceList(inspectionTaskDeviceList)) {
            return  false;
        }

        //是否启用巡检任务
        String isOpen = req.getIsOpen();
        if (!InspectionCommonValidate.checkIsOpen(isOpen)) {
            return false;
        }

        //是否选中全集
        String isSelectAll = req.getIsSelectAll();
        if (!InspectionCommonValidate.checkIsSelectAll(isSelectAll)) {
            return false;
        }
        return true;
    }
}
