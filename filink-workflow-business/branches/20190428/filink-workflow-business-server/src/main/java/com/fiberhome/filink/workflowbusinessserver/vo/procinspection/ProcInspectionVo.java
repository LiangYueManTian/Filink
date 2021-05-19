package com.fiberhome.filink.workflowbusinessserver.vo.procinspection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
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
     * 设施名称
     */
    private StringBuilder deviceName;

    /**
     * 设施类型
     */
    private StringBuilder deviceType;

    /**
     * 设施类型名字
     */
    private StringBuilder deviceTypeName;

    /**
     * 设施区域id
     */
    private StringBuilder deviceAreaId;

    /**
     * 设施区域名称
     */
    private StringBuilder deviceAreaName;

    /**
     * 部门名称
     */
    private StringBuilder accountabilityDeptName;

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
}
