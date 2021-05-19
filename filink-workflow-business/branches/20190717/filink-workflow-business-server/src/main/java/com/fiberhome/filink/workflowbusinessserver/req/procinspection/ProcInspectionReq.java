package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import com.fiberhome.filink.bean.CheckInputString;
import com.fiberhome.filink.logapi.bean.LoginUserBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.utils.common.InspectionCommonValidate;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcInspectionConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import com.fiberhome.filink.workflowbusinessserver.utils.procinspection.ProcInspectionValidate;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 新增巡检任务参数类
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
     * 关联告警
     */
    private String refAlarm;

    /**
     * 关联告警名称
     */
    private String refAlarmName;

    /**
     * 告警预计完成时间
     */
    private String alarmCompleteTime;

    /**
     * 周期 巡检任务生成的周期
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
     * 重新生成工单编号
     */
    private String regenerateId;

    /**
     * 新增巡检工单校验
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 17:26
     * @param req 新增巡检工单参数
     * @return Result 返回值
     */
    public static boolean validateProcInspectionReq(ProcInspectionReq req, String operate) {
        //操作为修改时
        if (ProcInspectionConstants.OPERATE_UPDATE.equals(operate)) {
            if (!ProcInspectionValidate.checkOperateUpdate(req.getProcId())) {
                return false;
            }
        }

        //巡检工单名称
        String title = req.getTitle();
        String procTitle = CheckInputString.nameCheck(title);
        if (StringUtils.isEmpty(procTitle)){
            return false;
        }
        req.setTitle(procTitle);

        //校验来源
        if (!ProcInspectionReq.checkByResourceType(req)) {
            return false;
        }



        //是否选中全集
        String isSelectAll = req.getIsSelectAll();
        if (!InspectionCommonValidate.checkIsSelectAll(isSelectAll)) {
            return false;
        }

        //备注
        String remark = req.getRemark();
        if (!ObjectUtils.isEmpty(remark)) {
            String procRemark = CheckInputString.markCheck(remark);
            if (StringUtils.isEmpty(procRemark)){
                return false;
            }
            req.setRemark(procRemark);
        }

        //校验区域
        if (!ProcInspectionReq.checkAreaInfo(req)) {
            return false;
        }

        //巡检任务设施
        List<ProcRelatedDevice> inspectionTaskDeviceList = req.getDeviceList();
        if (ObjectUtils.isEmpty(inspectionTaskDeviceList)) {
            return false;
        }

        //巡检任务开始时间
        Long taskStartDate = req.getInspectionStartDate();
        if (!ValidateUtils.validateDataInfoIsEmpty(taskStartDate)) {
            return false;
        }

        //巡检任务结束时间
        Long taskEndDate = req.getInspectionEndDate();
        if (!ValidateUtils.validateDataInfoIsEmpty(taskEndDate)) {
            return false;
        }
        return true;
    }

    /**
     * 校验区域信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 13:07
     * @param req 参数
     * @return 校验结果
     */
    public static boolean checkAreaInfo(ProcInspectionReq req) {
        //巡检区域编号
        String inspectionAreaId = req.getInspectionAreaId();
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionAreaId)) {
            return false;
        }

        //巡检区域名称
        String inspectionAreaName = req.getInspectionAreaName();
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionAreaName)) {
            return false;
        }
        return true;
    }


    /**
     * 校验工单来源
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 16:31
     * @param req 巡检流程参数
     * @return 返回校验结果
     */
    public static boolean checkByResourceType(ProcInspectionReq req) {

        //单据来源类型
        String procResourceType = req.getProcResourceType();

        if (!ValidateUtils.validateDataInfoIsEmpty(procResourceType)) {
            return false;
        }

        //巡检任务生成巡检工单
        if (ProcInspectionConstants.AUTO_ADD_PROC_INSPECTION.equals(procResourceType)) {
            if (!ProcInspectionReq.inspectionTaskGenerateRequired(req)) {
                return false;
            }
        }

        //告警生成巡检工单
        if (ProcInspectionConstants.AUTO_ADD_PROC_ALARM.equals(procResourceType)) {
            if (!ProcInspectionReq.alarmGenerateRequired(req)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 告警新增必填项校验
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 15:07
     * @param req
     * @return 返回告警参数校验
     */
    public static boolean alarmGenerateRequired(ProcInspectionReq req) {
        req = ProcInspectionReq.getProcInspectionReq(req);

        //告警编号
        String refAlarm = req.getRefAlarm();
        if (!ValidateUtils.validateDataInfoIsEmpty(refAlarm)) {
            return false;
        }

        //告警名称
        String refAlarmName = req.getRefAlarmName();
        if (!ValidateUtils.validateDataInfoIsEmpty(refAlarmName)) {
            return false;
        }

        //告警预计完成时间
        String alarmCompleteTime = req.getAlarmCompleteTime();
        if (!ValidateUtils.validateDataInfoIsEmpty(alarmCompleteTime)) {
            return false;
        }

        return true;
    }


    /**
     * 巡检任务生成必填项
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 10:42
     * @param req 新增巡检任务参数
     */
    public static boolean inspectionTaskGenerateRequired(ProcInspectionReq req) {
        req = ProcInspectionReq.getProcInspectionReq(req);

        //巡检任务编号
        String inspectionTaskId = req.getInspectionTaskId();
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionTaskId)) {
            return false;
        }

        //巡检任务名称
        String inspectionTaskName = req.getInspectionTaskName();
        if (!ValidateUtils.validateDataInfoIsEmpty(inspectionTaskName)) {
            return false;
        }


        //巡检任务关联单位
        List<ProcRelatedDepartment> inspectionTaskDepartments = req.getDeptList();
        if (ObjectUtils.isEmpty(inspectionTaskDepartments)) {
            return false;
        }
        return true;
    }

    /**
     * 获取巡检工单参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 14:36
     * @param req
     * @return 巡检工单参数
     */
    public static ProcInspectionReq getProcInspectionReq(ProcInspectionReq req) {
        String resourceType = req.getProcResourceType();
        if (!StringUtils.isEmpty(resourceType)) {
            if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(resourceType)) {
                return req;
            }
        }
        LoginUserBean loginUserBean = LogProcess.getLoginUserBeanInfo();
        //登录用户编号
        req.setUserId(loginUserBean.getUserId());
        //用户名称
        req.setUserName(loginUserBean.getUserName());
        //用户角色编号
        req.setRoleId(loginUserBean.getRoleId());
        //用户角色名称
        req.setRoleName(loginUserBean.getRoleName());
        return req;
    }

}
