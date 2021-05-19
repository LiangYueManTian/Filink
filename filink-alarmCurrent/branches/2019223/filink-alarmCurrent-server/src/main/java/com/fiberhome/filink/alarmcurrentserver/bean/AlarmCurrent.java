package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * <p>
 * 当前告警实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

@Data
@Document(collection = "alarm_current")
public class AlarmCurrent{

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
    @Field("alarm_source_typeid")
    private String alarmSourceTypeid;

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
     * 工单id
     */
    @Field("order_id")
    private String orderId;

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
    private String alarmobject;

    /**
     * 单位id
     */
    @Field("responsible_department_id")
    private String responsibleDepartmentId;

    /**
     * 负责单位
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
    private String alarmHappenCount;

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

    @Override
    public String toString() {
        return "AlarmCurrent{" +
                "id='" + id + '\'' +
                ", trapOid='" + trapOid + '\'' +
                ", alarmNameId='" + alarmNameId + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", alarmCode='" + alarmCode + '\'' +
                ", alarmContent='" + alarmContent + '\'' +
                ", alarmType=" + alarmType +
                ", alarmSource='" + alarmSource + '\'' +
                ", alarmSourceTypeid='" + alarmSourceTypeid + '\'' +
                ", areaId='" + areaId + '\'' +
                ", areaName='" + areaName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", address='" + address + '\'' +
                ", alarmFixedLevel='" + alarmFixedLevel + '\'' +
                ", alarmobject='" + alarmobject + '\'' +
                ", responsibleDepartmentId='" + responsibleDepartmentId + '\'' +
                ", responsibleDepartment='" + responsibleDepartment + '\'' +
                ", prompt='" + prompt + '\'' +
                ", alarmBeginTime='" + alarmBeginTime + '\'' +
                ", alarmNearTime='" + alarmNearTime + '\'' +
                ", alarmSystemTime='" + alarmSystemTime + '\'' +
                ", alarmSystemNearTime='" + alarmSystemNearTime + '\'' +
                ", alarmContinousTime='" + alarmContinousTime + '\'' +
                ", alarmHappenCount=" + alarmHappenCount +
                ", alarmCleanStatus=" + alarmCleanStatus +
                ", alarmCleanTime='" + alarmCleanTime + '\'' +
                ", alarmCleanType=" + alarmCleanType +
                ", alarmCleanPeopleId='" + alarmCleanPeopleId + '\'' +
                ", alarmCleanPeopleNickname='" + alarmCleanPeopleNickname + '\'' +
                ", alarmConfirmStatus=" + alarmConfirmStatus +
                ", alarmConfirmTime='" + alarmConfirmTime + '\'' +
                ", alarmConfirmPeopleId='" + alarmConfirmPeopleId + '\'' +
                ", alarmConfirmPeopleNickname='" + alarmConfirmPeopleNickname + '\'' +
                ", extraMsg='" + extraMsg + '\'' +
                ", alarmProcessing='" + alarmProcessing + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
