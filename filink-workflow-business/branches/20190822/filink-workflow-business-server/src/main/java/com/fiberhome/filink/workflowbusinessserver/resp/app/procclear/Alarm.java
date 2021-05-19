package com.fiberhome.filink.workflowbusinessserver.resp.app.procclear;

/**
 * app销障工单下载关联告警信息
 *
 * @author chaofanrong@wistronits.com
 */
public class Alarm {
    /**
     * 主键id
     */
    private String id;

    /**
     * Trap oid
     */
    private String trapOid;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 告警名称id
     */
    private String alarmNameId;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 告警内容
     */
    private String alarmContent;

    /**
     * 告警类型
     */
    private Integer alarmType;

    /**
     * 告警源(设备id)
     */
    private String alarmSource;

    /**
     * 告警源类型
     */
    private String alarmSourceType;

    /**
     * 告警源类型id
     */
    private String alarmSourceTypeId;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 是否存在关联工单
     */
    private Boolean isOrder;

    /**
     * 地址
     */
    private String address;

    /**
     * 告警级别
     */
    private String alarmFixedLevel;

    /**
     * 告警对象
     */
    private String alarmObject;

    /**
     * 单位id，多个单位ID用逗号隔开
     */
    private String responsibleDepartmentId;

    /**
     * 负责单位名称，多个单位名称用逗号隔开,跟单位ID 按顺序一一对应
     */
    private String responsibleDepartment;

    /**
     * 提示音 0是否 1是有
     */
    private String prompt;

    /**
     * 告警发生时间
     */
    private Long alarmBeginTime;

    /**
     * 最近发生时间
     */
    private Long alarmNearTime;

    /**
     * 网管接收时间
     */
    private Long alarmSystemTime;

    /**
     * 网管最近接收时间
     */
    private Long alarmSystemNearTime;

    /**
     * 告警持续时间
     */
    private Integer alarmContinouTime;

    /**
     * 告警发生次数
     */
    private Integer alarmHappenCount;

    /**
     * 告警清除状态，2是设备清除，1是已清除，3是未清除
     */
    private Integer alarmCleanStatus;

    /**
     * 告警清除时间
     */
    private Long alarmCleanTime;

    /**
     * 告警清除类型
     */
    private Integer alarmCleanType;

    /**
     * 告警清除责任人id
     */
    private String alarmCleanPeopleId;

    /**
     * 告警清除责任人
     */
    private String alarmCleanPeopleNickname;

    /**
     * 告警确认状态,1是已确认，2是未确认
     */
    private Integer alarmConfirmStatus;

    /**
     * 告警确认时间
     */
    private Long alarmConfirmTime;

    /**
     * 告警确认人id
     */
    private String alarmConfirmPeopleId;

    /**
     * 告警确认人
     */
    private String alarmConfirmPeopleNickname;

    /**
     * 附加消息
     */
    private String extraMsg;

    /**
     * 处理信息
     */
    private String alarmProcessing;

    /**
     * 备注
     */
    private String remark;

    /**
     * 门编号
     */
    private String doorNumber;

    /**
     * 门名称
     */
    private String doorName;

    /**
     * 是否存在关联的告警图片
     */
    private Boolean isPicture;

    /**
     * 主控id
     */
    private String controlId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmNameId() {
        return alarmNameId;
    }

    public void setAlarmNameId(String alarmNameId) {
        this.alarmNameId = alarmNameId;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }

    public String getAlarmSourceType() {
        return alarmSourceType;
    }

    public void setAlarmSourceType(String alarmSourceType) {
        this.alarmSourceType = alarmSourceType;
    }

    public String getAlarmSourceTypeId() {
        return alarmSourceTypeId;
    }

    public void setAlarmSourceTypeId(String alarmSourceTypeId) {
        this.alarmSourceTypeId = alarmSourceTypeId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Boolean getOrder() {
        return isOrder;
    }

    public void setOrder(Boolean order) {
        isOrder = order;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlarmFixedLevel() {
        return alarmFixedLevel;
    }

    public void setAlarmFixedLevel(String alarmFixedLevel) {
        this.alarmFixedLevel = alarmFixedLevel;
    }

    public String getAlarmObject() {
        return alarmObject;
    }

    public void setAlarmObject(String alarmObject) {
        this.alarmObject = alarmObject;
    }

    public String getResponsibleDepartmentId() {
        return responsibleDepartmentId;
    }

    public void setResponsibleDepartmentId(String responsibleDepartmentId) {
        this.responsibleDepartmentId = responsibleDepartmentId;
    }

    public String getResponsibleDepartment() {
        return responsibleDepartment;
    }

    public void setResponsibleDepartment(String responsibleDepartment) {
        this.responsibleDepartment = responsibleDepartment;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Long getAlarmBeginTime() {
        return alarmBeginTime;
    }

    public void setAlarmBeginTime(Long alarmBeginTime) {
        this.alarmBeginTime = alarmBeginTime;
    }

    public Long getAlarmNearTime() {
        return alarmNearTime;
    }

    public void setAlarmNearTime(Long alarmNearTime) {
        this.alarmNearTime = alarmNearTime;
    }

    public Long getAlarmSystemTime() {
        return alarmSystemTime;
    }

    public void setAlarmSystemTime(Long alarmSystemTime) {
        this.alarmSystemTime = alarmSystemTime;
    }

    public Long getAlarmSystemNearTime() {
        return alarmSystemNearTime;
    }

    public void setAlarmSystemNearTime(Long alarmSystemNearTime) {
        this.alarmSystemNearTime = alarmSystemNearTime;
    }

    public Integer getAlarmContinouTime() {
        return alarmContinouTime;
    }

    public void setAlarmContinouTime(Integer alarmContinouTime) {
        this.alarmContinouTime = alarmContinouTime;
    }

    public Integer getAlarmHappenCount() {
        return alarmHappenCount;
    }

    public void setAlarmHappenCount(Integer alarmHappenCount) {
        this.alarmHappenCount = alarmHappenCount;
    }

    public Integer getAlarmCleanStatus() {
        return alarmCleanStatus;
    }

    public void setAlarmCleanStatus(Integer alarmCleanStatus) {
        this.alarmCleanStatus = alarmCleanStatus;
    }

    public Long getAlarmCleanTime() {
        return alarmCleanTime;
    }

    public void setAlarmCleanTime(Long alarmCleanTime) {
        this.alarmCleanTime = alarmCleanTime;
    }

    public Integer getAlarmCleanType() {
        return alarmCleanType;
    }

    public void setAlarmCleanType(Integer alarmCleanType) {
        this.alarmCleanType = alarmCleanType;
    }

    public String getAlarmCleanPeopleId() {
        return alarmCleanPeopleId;
    }

    public void setAlarmCleanPeopleId(String alarmCleanPeopleId) {
        this.alarmCleanPeopleId = alarmCleanPeopleId;
    }

    public String getAlarmCleanPeopleNickname() {
        return alarmCleanPeopleNickname;
    }

    public void setAlarmCleanPeopleNickname(String alarmCleanPeopleNickname) {
        this.alarmCleanPeopleNickname = alarmCleanPeopleNickname;
    }

    public Integer getAlarmConfirmStatus() {
        return alarmConfirmStatus;
    }

    public void setAlarmConfirmStatus(Integer alarmConfirmStatus) {
        this.alarmConfirmStatus = alarmConfirmStatus;
    }

    public Long getAlarmConfirmTime() {
        return alarmConfirmTime;
    }

    public void setAlarmConfirmTime(Long alarmConfirmTime) {
        this.alarmConfirmTime = alarmConfirmTime;
    }

    public String getAlarmConfirmPeopleId() {
        return alarmConfirmPeopleId;
    }

    public void setAlarmConfirmPeopleId(String alarmConfirmPeopleId) {
        this.alarmConfirmPeopleId = alarmConfirmPeopleId;
    }

    public String getAlarmConfirmPeopleNickname() {
        return alarmConfirmPeopleNickname;
    }

    public void setAlarmConfirmPeopleNickname(String alarmConfirmPeopleNickname) {
        this.alarmConfirmPeopleNickname = alarmConfirmPeopleNickname;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public String getAlarmProcessing() {
        return alarmProcessing;
    }

    public void setAlarmProcessing(String alarmProcessing) {
        this.alarmProcessing = alarmProcessing;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public String getTrapOid() {
        return trapOid;
    }

    public void setTrapOid(String trapOid) {
        this.trapOid = trapOid;
    }

    public Boolean getPicture() {
        return isPicture;
    }

    public void setPicture(Boolean picture) {
        isPicture = picture;
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }
}
