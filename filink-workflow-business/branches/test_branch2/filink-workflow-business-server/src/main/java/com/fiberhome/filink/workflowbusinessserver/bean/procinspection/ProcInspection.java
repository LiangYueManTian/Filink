package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 巡检工单表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@TableName("proc_inspection")
public class ProcInspection extends Model<ProcInspection> {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编码
     */
    @TableId("proc_id")
    private String procId;

    /**
     * 巡检区域
     */
    @TableField("inspection_area_id")
    private String inspectionAreaId;

    /**
     * 巡检区域名称
     */
    @TableField("inspection_area_name")
    private String inspectionAreaName;

    /**
     * 巡检任务编号
     */
    @TableField("inspection_task_id")
    private String inspectionTaskId;

    /**
     * 巡检任务记录编号
     */
    @TableField("inspection_task_record_id")
    private String inspectionTaskRecordId;

    /**
     * 巡检任务开始时间
     */
    @TableField("inspection_start_time")
    private Date inspectionStartTime;

    /**
     * 巡检任务结束时间
     */
    @TableField("inspection_end_time")
    private Date inspectionEndTime;

    /**
     * 巡检数量
     */
    @TableField("inspection_device_count")
    private Integer inspectionDeviceCount;

    /**
     * 巡检完成数量
     */
    @TableField("inspection_completed_count")
    private Integer inspectionCompletedCount;

    /**
     * 是否选中设施全集 0 不选择设施全集 1 选择设施全集
     */
    @TableField("is_select_all")
    private String isSelectAll;

    /**
     * 设施编号
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设施名称
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 设施区域编号
     */
    @TableField("device_area_id")
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    @TableField("device_area_name")
    private String deviceAreaName;

    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 责任单位编号
     */
    @TableField("accountability_dept")
    private String accountabilityDept;

    /**
     * 责任单位名称
     */
    @TableField("accountability_dept_name")
    private String accountabilityDeptName;

    /**
     * 工单类型
     */
    @TableField("proc_type")
    private String procType;

    /**
     * 工单名称
     */
    @TableField("title")
    private String title;

    /**
     * 工单责任人
     */
    @TableField("assign")
    private String assign;

    /**
     * 单位类型 0 人 1 部门
     */
    @TableField("dept_type")
    private String deptType;

    /**
     * 单据状态
     * (assigned:待指派;
     * pending:待处理;
     * processing:处理中;
     * completed:已完成;
     * singleBack:已退单 turnProcess 已转派)
     */
    @TableField("status")
    private String status;

    /**
     * 退单原因
     * (0、其他；1、误报)
     */
    @TableField("single_back_reason")
    private String singleBackReason;

    /**
     * 自定义退单原因
     */
    @TableField("single_back_user_defined_reason")
    private String singleBackUserDefinedReason;

    /**
     * 处理方案
     * 0-自定义（对应故障原因-其他），1-报修（对应故障原因-人为损坏，道路施工，盗穿） 2 - 现场销障（对应故障原因-销障）
     */
    @TableField("processing_scheme")
    private String processingScheme;

    /**
     * 自定义处理方案
     */
    @TableField("processing_user_defined_scheme")
    private String processingUserDefinedScheme;

    /**
     * 期望完工时间
     */
    @TableField("expected_completed_time")
    private Date expectedCompletedTime;

    /**
     * 实际完工时间
     */
    @TableField("reality_completed_time")
    private Date realityCompletedTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 故障原因
     * （0，
     * "其他",1、"人为损坏",
     * 2、"道路施工",
     * 3，"盗穿",
     * 4、"销障"）
     */
    @TableField("error_reason")
    private String errorReason;

    /**
     * 自定义故障原因
     */
    @TableField("error_user_defined_reason")
    private String errorUserDefinedReason;

    /**
     * 工单来源 1 手动新增  2 巡检任务新增 3 告警新增
     */
    @TableField("proc_resource_type")
    private String procResourceType;

    /**
     * 转派原因
     */
    @TableField("turn_reason")
    private String turnReason;

    /**
     * 工单超时是否已经通知   0未通知 1 已通知
     */
    @TableField("is_told")
    private String isTold;

    /**
     * 关联告警编号
     */
    @TableField("ref_alarm")
    private String refAlarm;

    /**
     * 关联告警名称
     */
    @TableField("ref_alarm_name")
    private String refAlarmName;

    /**
     * 关联告警code
     */
    @TableField("ref_alarm_code")
    private String refAlarmCode;

    /**
     * 确认退单  0 未确认 1 已确认
     */
    @TableField("is_check_single_back")
    private String isCheckSingleBack;

    /**
     * 是否创建告警 0 未创建  1 已创建
     */
    @TableField("is_create_alarm")
    private String isCreateAlarm;

    /**
     * 重新生成工单id（已重新生成工单，会有新工单id）
     */
    @TableField("regenerate_id")
    private String regenerateId;

    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    public String getProcId() {
        return procId;
    }

    public void setProcId(String procId) {
        this.procId = procId;
    }
    public String getInspectionAreaId() {
        return inspectionAreaId;
    }

    public void setInspectionAreaId(String inspectionAreaId) {
        this.inspectionAreaId = inspectionAreaId;
    }
    public String getInspectionAreaName() {
        return inspectionAreaName;
    }

    public void setInspectionAreaName(String inspectionAreaName) {
        this.inspectionAreaName = inspectionAreaName;
    }
    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskId) {
        this.inspectionTaskId = inspectionTaskId;
    }
    public String getInspectionTaskRecordId() {
        return inspectionTaskRecordId;
    }

    public void setInspectionTaskRecordId(String inspectionTaskRecordId) {
        this.inspectionTaskRecordId = inspectionTaskRecordId;
    }
    public Date getInspectionStartTime() {
        return inspectionStartTime;
    }

    public void setInspectionStartTime(Date inspectionStartTime) {
        this.inspectionStartTime = inspectionStartTime;
    }
    public Date getInspectionEndTime() {
        return inspectionEndTime;
    }

    public void setInspectionEndTime(Date inspectionEndTime) {
        this.inspectionEndTime = inspectionEndTime;
    }

    public Integer getInspectionDeviceCount() {
        return inspectionDeviceCount;
    }

    public void setInspectionDeviceCount(Integer inspectionDeviceCount) {
        this.inspectionDeviceCount = inspectionDeviceCount;
    }

    public Integer getInspectionCompletedCount() {
        return inspectionCompletedCount;
    }

    public void setInspectionCompletedCount(Integer inspectionCompletedCount) {
        this.inspectionCompletedCount = inspectionCompletedCount;
    }

    public String getIsSelectAll() {
        return isSelectAll;
    }

    public void setIsSelectAll(String isSelectAll) {
        this.isSelectAll = isSelectAll;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAreaId() {
        return deviceAreaId;
    }

    public void setDeviceAreaId(String deviceAreaId) {
        this.deviceAreaId = deviceAreaId;
    }

    public String getDeviceAreaName() {
        return deviceAreaName;
    }

    public void setDeviceAreaName(String deviceAreaName) {
        this.deviceAreaName = deviceAreaName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAccountabilityDept() {
        return accountabilityDept;
    }

    public void setAccountabilityDept(String accountabilityDept) {
        this.accountabilityDept = accountabilityDept;
    }

    public String getAccountabilityDeptName() {
        return accountabilityDeptName;
    }

    public void setAccountabilityDeptName(String accountabilityDeptName) {
        this.accountabilityDeptName = accountabilityDeptName;
    }

    public String getProcType() {
        return procType;
    }

    public void setProcType(String procType) {
        this.procType = procType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssign() {
        return assign;
    }

    public void setAssign(String assign) {
        this.assign = assign;
    }

    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSingleBackReason() {
        return singleBackReason;
    }

    public void setSingleBackReason(String singleBackReason) {
        this.singleBackReason = singleBackReason;
    }

    public String getSingleBackUserDefinedReason() {
        return singleBackUserDefinedReason;
    }

    public void setSingleBackUserDefinedReason(String singleBackUserDefinedReason) {
        this.singleBackUserDefinedReason = singleBackUserDefinedReason;
    }

    public String getProcessingScheme() {
        return processingScheme;
    }

    public void setProcessingScheme(String processingScheme) {
        this.processingScheme = processingScheme;
    }

    public String getProcessingUserDefinedScheme() {
        return processingUserDefinedScheme;
    }

    public void setProcessingUserDefinedScheme(String processingUserDefinedScheme) {
        this.processingUserDefinedScheme = processingUserDefinedScheme;
    }

    public Date getExpectedCompletedTime() {
        return expectedCompletedTime;
    }

    public void setExpectedCompletedTime(Date expectedCompletedTime) {
        this.expectedCompletedTime = expectedCompletedTime;
    }

    public Date getRealityCompletedTime() {
        return realityCompletedTime;
    }

    public void setRealityCompletedTime(Date realityCompletedTime) {
        this.realityCompletedTime = realityCompletedTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getErrorUserDefinedReason() {
        return errorUserDefinedReason;
    }

    public void setErrorUserDefinedReason(String errorUserDefinedReason) {
        this.errorUserDefinedReason = errorUserDefinedReason;
    }

    public String getProcResourceType() {
        return procResourceType;
    }

    public void setProcResourceType(String procResourceType) {
        this.procResourceType = procResourceType;
    }

    public String getTurnReason() {
        return turnReason;
    }

    public void setTurnReason(String turnReason) {
        this.turnReason = turnReason;
    }

    public String getIsTold() {
        return isTold;
    }

    public void setIsTold(String isTold) {
        this.isTold = isTold;
    }

    public String getRefAlarm() {
        return refAlarm;
    }

    public void setRefAlarm(String refAlarm) {
        this.refAlarm = refAlarm;
    }

    public String getRefAlarmName() {
        return refAlarmName;
    }

    public void setRefAlarmName(String refAlarmName) {
        this.refAlarmName = refAlarmName;
    }

    public String getRefAlarmCode() {
        return refAlarmCode;
    }

    public void setRefAlarmCode(String refAlarmCode) {
        this.refAlarmCode = refAlarmCode;
    }

    public String getIsCheckSingleBack() {
        return isCheckSingleBack;
    }

    public void setIsCheckSingleBack(String isCheckSingleBack) {
        this.isCheckSingleBack = isCheckSingleBack;
    }

    public String getIsCreateAlarm() {
        return isCreateAlarm;
    }

    public void setIsCreateAlarm(String isCreateAlarm) {
        this.isCreateAlarm = isCreateAlarm;
    }

    public String getRegenerateId() {
        return regenerateId;
    }

    public void setRegenerateId(String regenerateId) {
        this.regenerateId = regenerateId;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.procId;
    }

    @Override
    public String toString() {
        return "ProcInspection{" +
        ", procId=" + procId +
        ", inspectionAreaId=" + inspectionAreaId +
        ", inspectionAreaName=" + inspectionAreaName +
        ", inspectionTaskId=" + inspectionTaskId +
        ", inspectionTaskRecordId=" + inspectionTaskRecordId +
        ", inspectionStartTime=" + inspectionStartTime +
        ", inspectionEndTime=" + inspectionEndTime +
        ", inspectionDeviceCount=" + inspectionDeviceCount +
        ", inspectionCompletedCount=" + inspectionCompletedCount +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }

    /**
     * 设置工单巡检参数到巡检工单参数中
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 10:24
     * @param processInfo 工单参数
     * @return 返回巡检工单参数
     */
    public static ProcInspection setProcInspectionParamToProcInspection(ProcessInfo processInfo) {
        ProcInspection procInspection = new ProcInspection();
        BeanUtils.copyProperties(processInfo.getProcBaseReq(), procInspection);
        //巡检工单
        if (!ObjectUtils.isEmpty(processInfo.getProcInspection())) {
            //巡检工单
            ProcInspection inspectionOne = processInfo.getProcInspection();
            ProcInspectionSpecial procInspectionSpecial = new ProcInspectionSpecial();
            BeanUtils.copyProperties(inspectionOne, procInspectionSpecial);
            BeanUtils.copyProperties(procInspectionSpecial, procInspection);
        }
        return procInspection;
    }

    /**
     * 设置设施信息到巡检工单参数中
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 9:25
     * @param processInfo 工单信息
     * @param procInspection 巡检工信息
     * @return 巡检工单参数
     */
    public static ProcInspection setDeviceToProcInspection(ProcessInfo processInfo, ProcInspection procInspection) {
        String deviceType = "";
        String areaId = "";
        String areaName = "";
        if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDevices())) {
            ProcRelatedDevice procRelatedDevice = processInfo.getProcRelatedDevices().get(0);
            deviceType = procRelatedDevice.getDeviceType();
            areaId = procRelatedDevice.getDeviceAreaId();
            areaName = procRelatedDevice.getDeviceAreaName();
        }
        procInspection.setDeviceAreaName(areaName);
        procInspection.setDeviceType(deviceType);
        procInspection.setDeviceAreaId(areaId);
        return procInspection;
    }

    /**
     * 获取新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:24
     * @param req 新增巡检任务参数
     * @param inspectionEndTime 期望完工时间
     * @param nowDate 当前时间
     * @param userId 用户编号
     * @return 新增巡检工单信息
     */
    public static ProcInspection getInsertProcInspection(ProcInspectionReq req, Date inspectionEndTime, Date nowDate, String userId) {
        ProcInspection procInspection = new ProcInspection();
        String procResourceType = req.getProcResourceType();
        //来源为巡检任务
        String resourceTypeInspection = ProcBaseConstants.PROC_RESOURCE_TYPE_2;
        //来源为告警
        String resourceTypeAlarm = ProcBaseConstants.PROC_RESOURCE_TYPE_3;
        //是否选中设施全集
        procInspection.setIsSelectAll(req.getIsSelectAll());
        if (resourceTypeInspection.equals(procResourceType) || resourceTypeAlarm.equals(procResourceType)) {
            //巡检任务生成巡检工单
            procInspection.setInspectionStartTime(nowDate);
            procInspection.setInspectionEndTime(inspectionEndTime);
            procInspection.setInspectionTaskId(req.getInspectionTaskId());
        } else {
            //不是巡检任务生成的巡检工单
            //开始时间
            if (null != req.getInspectionStartDate()) {
                Long inspectionStartDate = req.getInspectionStartDate();
                procInspection.setInspectionStartTime(new Date(inspectionStartDate));
            }

            //结束时间
            if (null != req.getInspectionEndDate()) {
                Long inspectionEndDate = req.getInspectionEndDate();
                procInspection.setInspectionEndTime(new Date(inspectionEndDate));
            }
        }

        if (null != req.getDeviceList() && 0 < req.getDeviceList().size()) {
            //巡检设施总数
            procInspection.setInspectionDeviceCount(req.getDeviceList().size());
        }
        //创建时间
        procInspection.setCreateTime(nowDate);
        //创建人
        procInspection.setCreateUser(userId);

        //巡检完成设施
        int defaultCompletedCount = 0;
        //巡检完成设施数量
        procInspection.setInspectionCompletedCount(defaultCompletedCount);
        return procInspection;
    }

    /**
     * 获取修改巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:24
     * @param req 修改巡检任务参数
     * @param nowDate 当前时间
     * @param userId 用户编号
     * @return 修改巡检工单信息
     */
    public static ProcInspection getUpdateProcInspection(ProcInspectionReq req, Date nowDate, String userId) {
        ProcInspection procInspection = new ProcInspection();
        if (null != req.getInspectionStartDate()) {
            Long inspectionStartDate = req.getInspectionStartDate();
            procInspection.setInspectionStartTime(new Date(inspectionStartDate));
        }

        //结束时间
        if (null != req.getInspectionEndDate()) {
            Long inspectionEndDate = req.getInspectionEndDate();
            procInspection.setInspectionEndTime(new Date(inspectionEndDate));
        }

        if (null != req.getDeviceList() && 0 < req.getDeviceList().size()) {
            //巡检设施总数
            procInspection.setInspectionDeviceCount(req.getDeviceList().size());
        }

        //是否选中设施全集
        procInspection.setIsSelectAll(req.getIsSelectAll());

        //修改时间
        procInspection.setUpdateTime(nowDate);
        //修改人
        procInspection.setUpdateUser(userId);


        return procInspection;
    }

}
