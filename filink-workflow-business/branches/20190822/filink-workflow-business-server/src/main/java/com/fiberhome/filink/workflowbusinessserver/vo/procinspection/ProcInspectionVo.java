package com.fiberhome.filink.workflowbusinessserver.vo.procinspection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * 巡检工单显示界面
 * @author hedongwei@wistronits.com
 * @date 2019/3/18 10:12
 */
@Data
public class ProcInspectionVo extends ProcBase {

    /**
     * 设施类型名字
     */
    private String deviceTypeName;

    /**
     * 巡检任务开始时间
     */
    private Date inspectionStartDate;

    /**
     * 巡检任务开始时间时间戳
     */
    private Long inspectionStartTime;

    /**
     * 巡检任务结束时间
     */
    private Date inspectionEndDate;

    /**
     * 巡检任务结束时间时间戳
     */
    private Long inspectionEndTime;

    /**
     * 巡检设施总数
     */
    private Integer inspectionDeviceCount;

    /**
     * 巡检完成数量
     */
    private Integer inspectionCompletedCount;

    /**
     * 进度
     */
    private double progressSpeed;

    /**
     * 创建时间戳
     */
    private Long cTime;

    /**
     * 修改时间戳
     */
    private Long uTime;

    /**
     * 期望完工时间戳
     */
    private Long ecTime;

    /**
     * 实际完工时间戳
     */
    private Long rcTime;

    /**
     * 责任人名字
     */
    private String assignName;

    /**
     * 剩余天数
     */
    private String lastDays;

    /**
     * 拼接退单原因（用于列表显示）
     */
    private String concatSingleBackReason;


    public Long getcTime() {
        return cTime;
    }

    public void setcTime(Long cTime) {
        this.cTime = cTime;
    }

    public Long getuTime() {
        return uTime;
    }

    public void setuTime(Long uTime) {
        this.uTime = uTime;
    }



    /**
     * 自定义创建时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationCTime() {
        if (ObjectUtils.isEmpty(this.cTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.cTime);
        }
    }

    /**
     * 自定义修改时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationUTime() {
        if (ObjectUtils.isEmpty(this.uTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.uTime);
        }
    }

    /**
     * 自定义期望完工时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationEcTime() {
        if (ObjectUtils.isEmpty(this.ecTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.ecTime);
        }
    }

    /**
     * 自定义实际完工时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationRcTime() {
        if (ObjectUtils.isEmpty(this.rcTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.rcTime);
        }
    }

    /**
     * 自定义巡检开始时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationInspectionStartTime() {
        if (ObjectUtils.isEmpty(this.inspectionStartTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.inspectionStartTime);
        }
    }

    /**
     * 自定义巡检结束时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationInspectionEndTime() {
        if (ObjectUtils.isEmpty(this.inspectionEndTime)){
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.inspectionEndTime);
        }
    }

    /**
     * 计算剩余天数（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationLastDays() {
        if (ObjectUtils.isEmpty(this.getExpectedCompletedTime())){
            //当期望完工时间为空
            return null;
        } else {
            return String.valueOf(this.differentDaysByMillisecond(new Date(),this.getExpectedCompletedTime()));
        }
    }

    /**
     * 国际化工单状态（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationStatus() {
        String statusName = "";
        if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(this.getStatus())){
            //待指派
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_ASSIGNED);
        } else if (ProcBaseConstants.PROC_STATUS_PENDING.equals(this.getStatus())){
            //待处理
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_PENDING);
        } else if (ProcBaseConstants.PROC_STATUS_PROCESSING.equals(this.getStatus())){
            //处理中
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_PROCESSING);
        } else if (ProcBaseConstants.PROC_STATUS_COMPLETED.equals(this.getStatus())){
            //已完成
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_COMPLETED);
        } else if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(this.getStatus())){
            //已退单
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_SINGLE_BACK);
        } else if (ProcBaseConstants.PROC_STATUS_TURN_PROCESSING.equals(this.getStatus())) {
            //已转派
            statusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), ProcBaseI18n.PROC_STATUS_TURN_PROCESSING);
        }
        return statusName;
    }


    /**
     * 获取故障原因
     */
    @JsonIgnore
    public String getTranslationErrorReason() {
        String errorErrorName = "";
        //故障原因
        if (ProcBaseConstants.ERROR_REASON_0.equals(this.getErrorReason())){
            //其他
            errorErrorName = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.ERROR_REASON_0) + ":" + this.getErrorUserDefinedReason();
        } else if (ProcBaseConstants.ERROR_REASON_1.equals(this.getErrorReason())){
            //人为损坏
            errorErrorName = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.ERROR_REASON_1);
        } else if (ProcBaseConstants.ERROR_REASON_2.equals(this.getErrorReason())){
            //道路施工
            errorErrorName = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.ERROR_REASON_2);
        } else if (ProcBaseConstants.ERROR_REASON_3.equals(this.getErrorReason())){
            //盗穿
            errorErrorName = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.ERROR_REASON_3);
        } else if (ProcBaseConstants.ERROR_REASON_4.equals(this.getErrorReason())){
            //销障
            errorErrorName = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.ERROR_REASON_4);
        }
        return errorErrorName;
    }

    /**
     * 退单原因
     */
    @JsonIgnore
    public String getTranslationSingleBackReason() {
        String singleBackReason = "";
        //退单原因
        if (ProcBaseConstants.SINGLE_BACK_REASON_0.equals(this.getSingleBackReason())){
            //其他
            singleBackReason = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.SINGLE_BACK_REASON_0) + ":" + this.getSingleBackUserDefinedReason();
        } else if (ProcBaseConstants.SINGLE_BACK_REASON_1.equals(this.getSingleBackReason())){
            //误报
            singleBackReason = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.SINGLE_BACK_REASON_1);
        }
        return singleBackReason;
    }

    /**
     * 处理方案
     */
    @JsonIgnore
    public String getTranslationProcessingScheme() {
        String processingScheme = "";
        if (ProcBaseConstants.PROCESSING_SCHEME_0.equals(this.getProcessingScheme())){
            //其他
            processingScheme = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.PROCESSING_SCHEME_0) + ":" + this.getProcessingUserDefinedScheme();
        } else if (ProcBaseConstants.PROCESSING_SCHEME_1.equals(this.getProcessingScheme())){
            //报修
            processingScheme = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.PROCESSING_SCHEME_1);
        } else if (ProcBaseConstants.PROCESSING_SCHEME_2.equals(this.getProcessingScheme())){
            //现场销障
            processingScheme = I18nUtils.getString(ExportApiUtils.getExportLocales() ,ProcBaseI18n.PROCESSING_SCHEME_2);
        }
        return processingScheme;
    }

    /**
     * 设施类型
     */
    @JsonIgnore
    public String getTranslationDeviceType() {
        String deviceType = "";
        String equalsDeviceType = !ObjectUtils.isEmpty(this.getDeviceType()) ? this.getDeviceType().toString() : "";
        //设施类型名字
        if (ProcBaseConstants.DEVICE_TYPE_001.equals(equalsDeviceType)){
            //光交箱
            deviceType = I18nUtils.getString(ExportApiUtils.getExportLocales() , ProcBaseI18n.DEVICE_TYPE_001);
        } else if (ProcBaseConstants.DEVICE_TYPE_030.equals(equalsDeviceType)){
            //人井
            deviceType = I18nUtils.getString(ExportApiUtils.getExportLocales() , ProcBaseI18n.DEVICE_TYPE_030);
        } else if (ProcBaseConstants.DEVICE_TYPE_060.equals(equalsDeviceType)){
            //配线架
            deviceType = I18nUtils.getString(ExportApiUtils.getExportLocales() , ProcBaseI18n.DEVICE_TYPE_060);
        } else if (ProcBaseConstants.DEVICE_TYPE_090.equals(equalsDeviceType)){
            //接头盒
            deviceType = I18nUtils.getString(ExportApiUtils.getExportLocales() , ProcBaseI18n.DEVICE_TYPE_090);
        } else if (ProcBaseConstants.DEVICE_TYPE_210.equals(equalsDeviceType)){
            //室外柜
            deviceType = I18nUtils.getString(ExportApiUtils.getExportLocales() , ProcBaseI18n.DEVICE_TYPE_210);
        }
        return deviceType;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1 开始时间
     * @param date2 结束时间
     * @return int
     */
    public int differentDaysByMillisecond(Date date1,Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
}
