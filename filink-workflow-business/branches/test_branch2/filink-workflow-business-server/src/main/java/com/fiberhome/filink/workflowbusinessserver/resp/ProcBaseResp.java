package com.fiberhome.filink.workflowbusinessserver.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.deviceapi.bean.DevicePicResp;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CalculateUtil;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionRecordVo;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 流程主表响应类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-02-18
 */
@Data
public class ProcBaseResp extends ProcBase {

    /**
     * 设施类型名字
     */
    private String deviceTypeName;

    /**
     * 责任人名字
     */
    private String assignName;

    /**
     * 告警名称
     */
    private String alarmName;

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
     * 拼接退单原因（用于列表显示）
     */
    private String concatSingleBackReason;

    /**
     * 拼接故障原因（用于列表显示）
     */
    private String concatErrorReason;

    /**
     * 拼接处理方案（用于列表显示）
     */
    private String concatProcessingScheme;

    /**
     * 剩余天数
     */
    private String lastDays;

    /**
     * 是否选中全集
     */
    private String isSelectAll;

    /**
     * 图片信息（用于首页工单前五显示）
     */
    private List<DevicePicResp> devicePicRespList;

    /**
     * 巡检工单记录信息（用于首页工单前五显示）
     */
    private List<ProcInspectionRecordVo> procInspectionRecordVoList;

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
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1 开始时间
     * @param date2 结束时间
     * @return int
     */
    public int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
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
        String equalsDeviceType = !ObjectUtils.isEmpty(this.getDeviceType()) ? this.getDeviceType() : "";
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
     * 将巡检进度添加到返回类中
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 21:16
     * @param procBaseResp 返回类
     * @return 返回类
     */
    public static ProcBaseResp setProgressSpeedToResp(ProcBaseResp procBaseResp) {
        double progressSpeed = 0;
        if (!ObjectUtils.isEmpty(procBaseResp.getInspectionDeviceCount())) {
            if (!ObjectUtils.isEmpty(procBaseResp.getInspectionCompletedCount()) ) {
                //巡检完成数量
                Integer completedCount = procBaseResp.getInspectionCompletedCount();
                //巡检总数
                Integer inspectionDeviceCount = procBaseResp.getInspectionDeviceCount();
                //巡检完成的百分比
                double percentDouble = CalculateUtil.calculatePercent(completedCount, inspectionDeviceCount);
                progressSpeed = percentDouble;
            } else {
                progressSpeed = 100;
            }
            procBaseResp.setProgressSpeed(progressSpeed);
        }
        return procBaseResp;
    }


    /**
     * 转换时间戳
     *
     * @param procBaseResp 工单返回类
     *
     * @return ProcBaseResp 工单返回类
     */
    public static ProcBaseResp setTimeStamp(ProcBaseResp procBaseResp) {
        // 返回时间戳
        if (!ObjectUtils.isEmpty(procBaseResp.getCreateTime())) {
            procBaseResp.setcTime(procBaseResp.getCreateTime().getTime());
        }
        if (!ObjectUtils.isEmpty(procBaseResp.getUpdateTime())) {
            procBaseResp.setuTime(procBaseResp.getUpdateTime().getTime());
        }
        if (!ObjectUtils.isEmpty(procBaseResp.getExpectedCompletedTime())) {
            procBaseResp.setEcTime(procBaseResp.getExpectedCompletedTime().getTime());
        }
        if (!ObjectUtils.isEmpty(procBaseResp.getRealityCompletedTime())) {
            procBaseResp.setRcTime(procBaseResp.getRealityCompletedTime().getTime());
        }
        if (!ObjectUtils.isEmpty(procBaseResp.getInspectionStartDate())) {
            procBaseResp.setInspectionStartTime(procBaseResp.getInspectionStartDate().getTime());
        }
        if (!ObjectUtils.isEmpty(procBaseResp.getInspectionEndDate())) {
            procBaseResp.setInspectionEndTime(procBaseResp.getInspectionEndDate().getTime());
        }
        return procBaseResp;
    }


    /**
     * 国际化枚举值
     *
     * @param procBaseRespList 工单列表
     *
     * @return List<ProcBaseResp> 列表展示信息
     */
    public static List<ProcBaseResp> internationalizationEnum(List<ProcBaseResp> procBaseRespList) {

        for (ProcBaseResp procBaseResp : procBaseRespList){
            //故障原因
            if (ProcBaseConstants.ERROR_REASON_0.equals(procBaseResp.getErrorReason())){
                //其他
                procBaseResp.setConcatErrorReason(I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_0) + ":" + procBaseResp.getErrorUserDefinedReason());
            } else if (ProcBaseConstants.ERROR_REASON_1.equals(procBaseResp.getErrorReason())){
                //人为损坏
                procBaseResp.setConcatErrorReason(I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_1));
            } else if (ProcBaseConstants.ERROR_REASON_2.equals(procBaseResp.getErrorReason())){
                //道路施工
                procBaseResp.setConcatErrorReason(I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_2));
            } else if (ProcBaseConstants.ERROR_REASON_3.equals(procBaseResp.getErrorReason())){
                //盗穿
                procBaseResp.setConcatErrorReason(I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_3));
            } else if (ProcBaseConstants.ERROR_REASON_4.equals(procBaseResp.getErrorReason())){
                //销障
                procBaseResp.setConcatErrorReason(I18nUtils.getSystemString(ProcBaseI18n.ERROR_REASON_4));
            }

            //退单原因
            if (ProcBaseConstants.SINGLE_BACK_REASON_0.equals(procBaseResp.getSingleBackReason())){
                //其他
                procBaseResp.setConcatSingleBackReason(I18nUtils.getSystemString(ProcBaseI18n.SINGLE_BACK_REASON_0) + ":" + procBaseResp.getSingleBackUserDefinedReason());
            } else if (ProcBaseConstants.SINGLE_BACK_REASON_1.equals(procBaseResp.getSingleBackReason())){
                //误报
                procBaseResp.setConcatSingleBackReason(I18nUtils.getSystemString(ProcBaseI18n.SINGLE_BACK_REASON_1));
            }

            // TODO: 2019/4/16 处理方案枚举值暂定
            //处理方案
            if (ProcBaseConstants.PROCESSING_SCHEME_0.equals(procBaseResp.getProcessingScheme())){
                //其他
                procBaseResp.setConcatProcessingScheme(I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_0) + ":" + procBaseResp.getProcessingUserDefinedScheme());
            } else if (ProcBaseConstants.PROCESSING_SCHEME_1.equals(procBaseResp.getProcessingScheme())){
                //报修
                procBaseResp.setConcatProcessingScheme(I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_1));
            } else if (ProcBaseConstants.PROCESSING_SCHEME_2.equals(procBaseResp.getProcessingScheme())){
                //现场销障
                procBaseResp.setConcatProcessingScheme(I18nUtils.getSystemString(ProcBaseI18n.PROCESSING_SCHEME_2));
            }
        }

        return procBaseRespList;
    }



    /**
     * 巡检工单属性赋值到流程返回类中
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 20:01
     * @param procInspection 流程编号
     * @param processInfo 流程信息
     * @return 流程返回类
     */
    public static ProcBaseResp procInspectionSetAttrToProcBaseResp(ProcInspection procInspection, ProcessInfo processInfo) {
        ProcBaseResp procBaseResp = processInfo.getProcBaseResp();
        procBaseResp = ProcBaseResp.getInspectionProcBaseResp(procInspection, procBaseResp);
        return procBaseResp;
    }

    /**
     * 获取巡检信息参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 21:38
     * @param procInspection 巡检工单信息
     * @param procBaseResp 流程返回类
     * @return 返回工单信息
     */
    public static ProcBaseResp getInspectionProcBaseResp(ProcInspection procInspection ,ProcBaseResp procBaseResp) {
        //设置巡检开始时间
        if (!ObjectUtils.isEmpty(procInspection)) {
            if (!ObjectUtils.isEmpty(procInspection.getInspectionStartTime())) {
                //巡检开始时间
                Date startTime = procInspection.getInspectionStartTime();
                //设置巡检开始时间
                procBaseResp.setInspectionStartDate(startTime);
                //设置巡检开始时间（时间戳）
                procBaseResp.setInspectionStartTime(startTime.getTime());
            }
            //设置巡检结束时间
            if (!ObjectUtils.isEmpty(procInspection.getInspectionEndTime())) {
                //巡检结束时间
                Date endTime = procInspection.getInspectionEndTime();
                //设置巡检结束时间
                procBaseResp.setInspectionEndDate(endTime);
                //设置巡检结束时间（时间戳）
                procBaseResp.setInspectionEndTime(endTime.getTime());
            }
        }
        return procBaseResp;
    }
}
