package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;

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

    @TableId("proc_inspection_id")
    private String procInspectionId;

    /**
     * 工单编码
     */
    @TableField("proc_id")
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

    public String getProcInspectionId() {
        return procInspectionId;
    }

    public void setProcInspectionId(String procInspectionId) {
        this.procInspectionId = procInspectionId;
    }
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
        return this.procInspectionId;
    }

    @Override
    public String toString() {
        return "ProcInspection{" +
        "procInspectionId=" + procInspectionId +
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
        //流程uuid
        procInspection.setProcInspectionId(NineteenUUIDUtils.uuid());

        return procInspection;
    }

}
