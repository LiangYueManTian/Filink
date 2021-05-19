package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单主表
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-02-18
 */
@TableName("proc_base")
@Data
public class ProcBase extends Model<ProcBase> {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编号
     */
    @TableId("proc_id")
    private String procId;

    /**
     * 工单类型
     */
    @TableField("proc_type")
    private String procType;

    /**
     * 工单标题
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
    @TableField("dept_type")
    private String deptType;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 退单原因
     */
    @TableField("single_back_reason")
    private String singleBackReason;

    /**
     * 自定义退单原因
     */
    @TableField("single_back_user_defined_reason")
    private String singleBackUserDefinedReason;

    /**
     * 转派原因
     */
    @TableField("turn_reason")
    private String turnReason;

    /**
     * 处理方案
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
     * 故障原因
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
     * 确认退单 0 未确认 1 已确认
     */
    @TableField("is_check_single_back")
    private String isCheckSingleBack;

    /**
     * 是否创建告警 0 未创建  1 已创建
     */
    @TableField("is_create_alarm")
    private String isCreateAlarm;

    /**
     * 重新生成工单id
     */
    @TableField("regenerate_id")
    private String regenerateId;

    /**
     * 是否删除
     */
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


    @Override
    protected Serializable pkVal() {
        return this.procId;
    }

    @Override
    public String toString() {
        return "ProcBase{" +
        "procId=" + procId +
        ", procType=" + procType +
        ", title=" + title +
        ", remark=" + remark +
        ", assign=" + assign +
        ", deptType=" + deptType +
        ", status=" + status +
        ", singleBackReason=" + singleBackReason +
        ", processingScheme=" + processingScheme +
        ", expectedCompletedTime=" + expectedCompletedTime +
        ", realityCompletedTime=" + realityCompletedTime +
        ", errorReason=" + errorReason +
        ", turnReason=" + turnReason +
        ", procResourceType=" + procResourceType +
        ", isTold=" + isTold +
        ", refAlarm=" + refAlarm +
        ", isCheckSingleBack=" + isCheckSingleBack +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }

    /**
     * 校验工单名格式
     */
    public boolean checkProcBaseTitle() {
//        String procBaseTitleRegex = "^[a-zA-Z0-9_\\-\\u4e00-\\u9fa5\\\" \"]{0,128}$";
        String procBaseTitleRegex = "^(?!_)[\\w\\s_\\u4e00-\\u9fa5]{1,32}$";
        return this.title.matches(procBaseTitleRegex);
    }

    /**
     * 获取创建月份
     */
    public String getCreateMonth(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (ObjectUtils.isEmpty(this.getCreateTime())){
            //默认取当前月份
            return simpleDateFormat.format(new Date()).substring(5,7);
        } else {
            return simpleDateFormat.format(this.getCreateTime()).substring(5,7);
        }

    }

    /**
     * 保存单位类型工单状态
     * @author hedongwei@wistronits.com
     * @date  2019/4/2 15:30
     * @param deptList 部门集合
     * @param procBase 流程主表
     * @return 流程主表
     */
    public static ProcBase setDeptTypeAndStatus(List<ProcRelatedDepartment> deptList, ProcBase procBase) {
        //单位类型
        if (!ObjectUtils.isEmpty(deptList)) {
            //单位类型为单位
            procBase.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_DEPT);
            //工单状态为待处理
            procBase.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        }
        return procBase;
    }

    /**
     * 国际化工单状态（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationStatus() {
        if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(this.status)){
            //待指派
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_ASSIGNED);
        } else if (ProcBaseConstants.PROC_STATUS_PENDING.equals(this.status)){
            //待处理
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_PENDING);
        } else if (ProcBaseConstants.PROC_STATUS_PROCESSING.equals(this.status)){
            //处理中
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_PROCESSING);
        } else if (ProcBaseConstants.PROC_STATUS_COMPLETED.equals(this.status)){
            //已完成
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_COMPLETED);
        } else if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(this.status)){
            //已退单
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_SINGLE_BACK);
        } else if (ProcBaseConstants.PROC_STATUS_TURN_PROCESSING.equals(this.status)) {
            //已转派
            this.status = I18nUtils.getString(ProcBaseI18n.PROC_STATUS_TURN_PROCESSING);
        }
        return this.status;
    }
}
