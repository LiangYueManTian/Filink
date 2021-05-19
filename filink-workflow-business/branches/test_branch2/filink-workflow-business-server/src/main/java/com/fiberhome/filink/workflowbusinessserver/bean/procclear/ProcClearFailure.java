package com.fiberhome.filink.workflowbusinessserver.bean.procclear;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 销障工单表
 * </p>
 *
 * @author wh1701002@wistronits.com
 * @since 2019-02-15
 */
@TableName("proc_clear_failure")
@Data
public class ProcClearFailure extends Model<ProcClearFailure> {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编码
     */
    @TableId("proc_id")
    private String procId;

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

    /**
     * 将设施信息添加到销障工单信息中
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 8:57
     * @param processInfo 流程信息
     * @param procClearFailure 销障工单信息
     * @return 销障工单信息
     */
    public static ProcClearFailure setDeviceToProcClearFailure(ProcessInfo processInfo, ProcClearFailure procClearFailure) {
        String deviceName = "";
        String deviceType = "";
        String deviceId = "";
        String areaId = "";
        String areaName = "";
        if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDevices())) {
            ProcRelatedDevice procRelatedDevice = processInfo.getProcRelatedDevices().get(0);
            deviceName = procRelatedDevice.getDeviceName();
            deviceType = procRelatedDevice.getDeviceType();
            deviceId = procRelatedDevice.getDeviceId();
            areaId = procRelatedDevice.getDeviceAreaId();
            areaName = procRelatedDevice.getDeviceAreaName();
        }
        procClearFailure.setDeviceAreaName(deviceName);
        procClearFailure.setDeviceType(deviceType);
        procClearFailure.setDeviceId(deviceId);
        procClearFailure.setDeviceAreaId(areaId);
        procClearFailure.setDeviceAreaName(areaName);
        return procClearFailure;
    }



    @Override
    protected Serializable pkVal() {
        return this.procId;
    }

    @Override
    public String toString() {
        return "ProcClearFailure{" +
        ", procId=" + procId +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
