package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

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
@Data
public class ProcBase {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 工单类型
     */
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
     * 自定义退单原因
     */
    private String singleBackUserDefinedReason;

    /**
     * 转派原因
     */
    private String turnReason;

    /**
     * 处理方案
     */
    private String processingScheme;

    /**
     * 自定义处理方案
     */
    private String processingUserDefinedScheme;

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
     * 自定义故障原因
     */
    private String errorUserDefinedReason;

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
     * 关联告警code
     */
    private String refAlarmCode;

    /**
     * 确认退单 0 未确认 1 已确认
     */
    private String isCheckSingleBack;

    /**
     * 是否创建告警 0 未创建  1 已创建
     */
    private String isCreateAlarm;

    /**
     * 重新生成工单id
     */
    private String regenerateId;

    /**
     * 是否删除
     */
    private String isDeleted;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施区域编号
     */
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    private String deviceAreaName;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 责任单位编号
     */
    private String accountabilityDept;

    /**
     * 责任单位名称
     */
    private String accountabilityDeptName;

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
     * 国际化工单状态名称
     */
    @JsonIgnore
    public String getStatusName() {
        String statusName = "";
        if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(this.status)){
            //待指派
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_ASSIGNED);
        } else if (ProcBaseConstants.PROC_STATUS_PENDING.equals(this.status)){
            //待处理
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_PENDING);
        } else if (ProcBaseConstants.PROC_STATUS_PROCESSING.equals(this.status)){
            //处理中
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_PROCESSING);
        } else if (ProcBaseConstants.PROC_STATUS_COMPLETED.equals(this.status)){
            //已完成
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_COMPLETED);
        } else if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(this.status)){
            //已退单
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_SINGLE_BACK);
        } else if (ProcBaseConstants.PROC_STATUS_TURN_PROCESSING.equals(this.status)) {
            //已转派
            statusName = I18nUtils.getSystemString(ProcBaseI18n.PROC_STATUS_TURN_PROCESSING);
        }
        return statusName;
    }

    /**
     * 异常原因名称
     */
    @JsonIgnore
    public String getErrorReasonName() {
        String errorReasonName = "";
        //故障原因
        if (ProcBaseConstants.ERROR_REASON_0.equals(this.errorReason)){
            //其他
            errorReasonName = I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_0);
        } else if (ProcBaseConstants.ERROR_REASON_1.equals(this.errorReason)){
            //人为损坏
            errorReasonName = I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_1);
        } else if (ProcBaseConstants.ERROR_REASON_2.equals(this.errorReason)){
            //道路施工
            errorReasonName = I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_2);
        } else if (ProcBaseConstants.ERROR_REASON_3.equals(this.errorReason)){
            //盗穿
            errorReasonName = I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_3);
        } else if (ProcBaseConstants.ERROR_REASON_4.equals(this.errorReason)){
            //销障
            errorReasonName = I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_4);
        }
        return errorReasonName;
    }

    /**
     * 处理方案名称
     */
    @JsonIgnore
    public String getProcessingSchemeName() {
        String processingSchemeName = "";
        if (ProcBaseConstants.PROCESSING_SCHEME_0.equals(this.processingScheme)){
            //其他
            processingSchemeName = I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_0);
        } else if (ProcBaseConstants.PROCESSING_SCHEME_1.equals(this.processingScheme)){
            //报修
            processingSchemeName = I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_1);
        } else if (ProcBaseConstants.PROCESSING_SCHEME_2.equals(this.processingScheme)){
            //现场销障
            processingSchemeName = I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_2);
        }
        return processingSchemeName;
    }
}
