package com.fiberhome.filink.alarmhistoryserver.bean;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.bean.CheckInputString;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 历史告警实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
@Document(collection = "alarm_history")
public class AlarmHistory {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * Trap oid
     */
    @Field("trap_oid")
    private String trapOid;

    /**
     * 告警名称
     */
    @Field("alarm_name")
    private String alarmName;

    /**
     * 告警名称id
     */
    @Field("alarm_name_id")
    private String alarmNameId;

    /**
     * 告警编码
     */
    @Field("alarm_code")
    private String alarmCode;

    /**
     * 告警内容
     */
    @Field("alarm_content")
    private String alarmContent;

    /**
     * 告警类型
     */
    @Field("alarm_type")
    private Integer alarmType;

    /**
     * 告警源(设备id)
     */
    @Field("alarm_source")
    private String alarmSource;

    /**
     * 告警源类型
     */
    @Field("alarm_source_type")
    private String alarmSourceType;

    /**
     * 告警源类型id
     */
    @Field("alarm_source_type_id")
    private String alarmSourceTypeId;

    /**
     * 区域id
     */
    @Field("area_id")
    private String areaId;

    /**
     * 区域名称
     */
    @Field("area_name")
    private String areaName;

    /**
     * 是否存在关联工单
     */
    @Field("is_order")
    private Boolean isOrder;

    /**
     * 地址
     */
    @Field("address")
    private String address;

    /**
     * 告警级别
     */
    @Field("alarm_fixed_level")
    private String alarmFixedLevel;

    /**
     * 告警对象
     */
    @Field("alarm_object")
    private String alarmObject;

    /**
     * 单位id，多个单位ID用逗号隔开
     */
    @Field("responsible_department_id")
    private String responsibleDepartmentId;

    /**
     * 负责单位名称，多个单位名称用逗号隔开,跟单位ID 按顺序一一对应
     */
    @Field("responsible_department")
    private String responsibleDepartment;

    /**
     * 提示音 0是否 1是有
     */
    @Field("prompt")
    private String prompt;

    /**
     * 告警发生时间
     */
    @Field("alarm_begin_time")
    private Long alarmBeginTime;

    /**
     * 最近发生时间
     */
    @Field("alarm_near_time")
    private Long alarmNearTime;

    /**
     * 网管接收时间
     */
    @Field("alarm_system_time")
    private Long alarmSystemTime;

    /**
     * 网管最近接收时间
     */
    @Field("alarm_system_near_time")
    private Long alarmSystemNearTime;

    /**
     * 告警持续时间
     */
    @Field("alarm_continous_time")
    private Integer alarmContinousTime;

    /**
     * 告警发生次数
     */
    @Field("alarm_happen_count")
    private Integer alarmHappenCount;

    /**
     * 告警清除状态，2是设备清除，1是已清除，3是未清除
     */
    @Field("alarm_clean_status")
    private Integer alarmCleanStatus;

    /**
     * 告警清除时间
     */
    @Field("alarm_clean_time")
    private Long alarmCleanTime;

    /**
     * 告警清除类型
     */
    @Field("alarm_clean_type")
    private Integer alarmCleanType;

    /**
     * 告警清除责任人id
     */
    @Field("alarm_clean_people_id")
    private String alarmCleanPeopleId;

    /**
     * 告警清除责任人
     */
    @Field("alarm_clean_people_nickname")
    private String alarmCleanPeopleNickname;

    /**
     * 告警确认状态,1是已确认，2是未确认
     */
    @Field("alarm_confirm_status")
    private Integer alarmConfirmStatus;

    /**
     * 告警确认时间
     */
    @Field("alarm_confirm_time")
    private Long alarmConfirmTime;

    /**
     * 告警确认人id
     */
    @Field("alarm_confirm_people_id")
    private String alarmConfirmPeopleId;

    /**
     * 告警确认人
     */
    @Field("alarm_confirm_people_nickname")
    private String alarmConfirmPeopleNickname;

    /**
     * 附加消息
     */
    @Field("extra_msg")
    private String extraMsg;

    /**
     * 处理信息
     */
    @Field("alarm_processing")
    private String alarmProcessing;

    /**
     * 备注
     */
    @Field("remark")
    private String remark;

    /**
     * 门编号
     */
    @Field("door_number")
    private String doorNumber;

    /**
     * 门名称
     */
    @Field("door_name")
    private String doorName;

    /**
     * 是否存在关联的告警图片
     */
    @Field("is_picture")
    private Boolean isPicture;

    /**
     * 主控id
     */
    @Field("control_id")
    private String controlId;

    @JsonIgnore
    public String getTranslationAlarmFixedLevel() {
        String alarmFixedLevelName = "";
        if (alarmFixedLevel != null) {
            if (AppConstant.YI.equals(this.alarmFixedLevel)) {
                alarmFixedLevelName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_LEVEL_NAME_ONE);
            } else if (AppConstant.TWO.equals(this.alarmFixedLevel)) {
                alarmFixedLevelName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_LEVEL_NAME_TWO);
            } else if (AppConstant.THREE.equals(this.alarmFixedLevel)) {
                alarmFixedLevelName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_LEVEL_NAME_THREE);
            } else if (AppConstant.FOUR.equals(this.alarmFixedLevel)) {
                alarmFixedLevelName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_LEVEL_NAME_FOUR);
            }
        }
        return alarmFixedLevelName;
    }

    @JsonIgnore
    public String getTranslationAlarmSourceTypeId() {
        String alarmSourceTypeIdName = "";
        if (alarmSourceTypeId != null) {
            if (AppConstant.ALARM_DEVICE_ONE.equals(this.alarmSourceTypeId)) {
                alarmSourceTypeIdName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_DEVICE_NAME_ONE);
            } else if (AppConstant.ALARM_DEVICE_TWO.equals(this.alarmSourceTypeId)) {
                alarmSourceTypeIdName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_DEVICE_NAME_TWO);
            } else if (AppConstant.ALARM_DEVICE_THREE.equals(this.alarmSourceTypeId)) {
                alarmSourceTypeIdName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_DEVICE_NAME_THREE);
            } else if (AppConstant.ALARM_DEVICE_FOUR.equals(this.alarmSourceTypeId)) {
                alarmSourceTypeIdName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_DEVICE_NAME_FOUR);
            } else if (AppConstant.ALARM_DEVICE_FREE.equals(this.alarmSourceTypeId)) {
                alarmSourceTypeIdName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_DEVICE_NAME_FREE);
            }
        }
        return alarmSourceTypeIdName;
    }

    @JsonIgnore
    public String getTranslationAlarmContinousTime() {
        double continousTime = 0;
        String continousTimeStr = "";
        if (this.alarmCleanTime != null && this.alarmCleanTime.longValue() > 0) {
            continousTime = this.alarmCleanTime - this.alarmBeginTime;
        } else {
            continousTime = System.currentTimeMillis() - this.alarmBeginTime;
        }
        if (continousTime < 0) {
            return continousTimeStr;
        }
        long time = (long) Math.ceil(continousTime / 1000 / 60 / 60);
        if (time == 0) {
            continousTimeStr = '1' + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.HOUR);
        } else if (time >= LogFunctionCodeConstant.ALARM_BQLO) {
            // 年
            continousTimeStr = Math.toIntExact(time / 8760) + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.YEAR)
                    + (int) Math.ceil(time % 8760 / 720) + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.MONTH);
        } else if (time >= LogFunctionCodeConstant.ALARM_QLO) {
            // 月
            continousTimeStr = Math.toIntExact(time / 720) + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.MONTH)
                    + (int) Math.ceil(time % 720 / 24) + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.DAY);
        } else if (LogFunctionCodeConstant.ALARM_ES <= time) {
            // 天
            continousTimeStr = Math.toIntExact(time / 24)
                    + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.DAY) + (int) Math.ceil(time % 24)
                    + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.HOUR);
        } else {
            // 时
            continousTimeStr = (int) Math.ceil(time) + I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.HOUR);
        }
        return continousTimeStr;
    }

    @JsonIgnore
    public String getTranslationAlarmConfirmStatus() {
        return LogFunctionCodeConstant.ALARM_STATUS_ONE == alarmConfirmStatus
                ? I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_CONFIRM_NAME_TWO)
                : I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_CONFIRM_NAME_ONE);
    }

    @JsonIgnore
    public String getTranslationAlarmCleanStatus() {
        String alarmCleanStatusName = "";
        if (alarmCleanStatus != null) {
            if (LogFunctionCodeConstant.ALARM_STATUS_ONE == this.alarmCleanStatus) {
                alarmCleanStatusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_CLEAR_NAME_ONE);
            } else if (LogFunctionCodeConstant.ALARM_STATUS_TWO == this.alarmCleanStatus) {
                alarmCleanStatusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_CLEAR_NAME_THREE);
            } else if (LogFunctionCodeConstant.ALARM_STATUS_THREE == this.alarmCleanStatus) {
                alarmCleanStatusName = I18nUtils.getString(ExportApiUtils.getExportLocales(), AppConstant.ALARM_CLEAR_NAME_TWO);
            }
        }
        return alarmCleanStatusName;
    }

    /**
     * 首次发生时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationAlarmBeginTime() {
        if (ObjectUtils.isEmpty(this.alarmBeginTime)) {
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.alarmBeginTime);
        }
    }

    /**
     * 最近发生时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationAlarmNearTime() {
        if (ObjectUtils.isEmpty(this.alarmNearTime)) {
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.alarmNearTime);
        }
    }

    /**
     * 告警清除时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationAlarmCleanTime() {
        if (ObjectUtils.isEmpty(this.alarmCleanTime)) {
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.alarmCleanTime);
        }
    }

    /**
     * 告警确认时间（用于列表导出）
     */
    @JsonIgnore
    public String getTranslationAlarmConfirmTime() {
        if (ObjectUtils.isEmpty(this.alarmConfirmTime)) {
            return null;
        } else {
            return ExportApiUtils.getZoneTime(this.alarmConfirmTime);
        }
    }

    /**
     * 备注校验
     *
     * @return true通过 false不通过
     */
    public boolean checkMark() {
        if (remark != null) {
            remark = CheckInputString.markCheck(remark);
            return !StringUtils.isEmpty(remark);
        }
        return true;
    }
}
