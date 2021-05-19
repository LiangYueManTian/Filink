package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.utils.common.OperateUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 流程主表请求类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-02-18
 */
@Data
public class ProcBaseReq extends ProcBase {

    /**
     * 工单ids
     */
    private Set<String> procIds;

    /**
     * 分组字段
     */
    private String groupField;

    /**
     * 工单状态（多选）
     */
    private Set<String> statusList;

    /**
     * 设施ids
     */
    private Set<String> deviceIds;

    /**
     * 关联告警code
     */
    private Set<String> refAlarmCodes;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施名字
     */
    private String deviceName;

    /**
     * 设施类型（多选）
     */
    private List<String> deviceTypes;

    /**
     * 设施区域
     */
    private Set<String> deviceAreaIds;

    /**
     * 设施区域名字
     */
    private String deviceAreaName;

    /**
     * 责任单位编号
     */
    private Set<String> accountabilityDeptList;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

    /**
     * 巡检开始时间
     */
    private List<Long> inspectionStartTimes;

    /**
     * 巡检结束时间
     */
    private List<Long> inspectionEndTimes;

    /**
     * 巡检设施数量
     */
    private Integer inspectionDeviceCount;

    /**
     * 巡检设施数量操作符
     */
    private String inspectionDeviceCountOperate;

    /**
     * 今天开始时间
     */
    private String todayStartTime;

    /**
     * 今天结束时间
     */
    private String todayEndTime;

    /**
     * 拼接退单原因（用于列表查询）
     */
    private String concatSingleBackReason;

    /**
     * 拼接故障原因（用于列表查询）
     */
    private String concatErrorReason;

    /**
     * 拼接处理方案（用于列表查询）
     */
    private String concatProcessingScheme;

    /**
     * 拼接退单原因（多选）（用于列表查询）
     */
    private List<String> concatSingleBackReasons;

    /**
     * 拼接故障原因（用于列表查询）
     */
    private List<String> concatErrorReasons;

    /**
     * 拼接处理方案（用于列表查询）
     */
    private List<String> concatProcessingSchemes;

    /**
     * 期望完工时间戳
     */
    private List<Long> ecTimes;

    /**
     * 实际完工时间戳
     */
    private List<Long> rcTimes;

    /**
     * 创建时间戳
     */
    private List<Long> cTimes;

    /**
     * 剩余天数
     */
    private Integer lastDays;

    /**
     * 剩余天数操作符
     */
    private String lastDaysOperate;

    /**
     * 转派原因
     */
    private String turnReason;

    /**
     * 责任人
     */
    private List<String> assigns;

    /**
     * 批量下载条数
     */
    private Integer batchDownloadProcNum;

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

    /**
     * 权限过滤部门id
     */
    private Set<String> permissionDeptIds;

    /**
     * 权限过滤procIds
     */
    private Set<String> permissionProcIds;

    public Set<String> getProcIds() {
        return procIds;
    }

    public void setProcIds(Set<String> procIds) {
        this.procIds = procIds;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public Set<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(Set<String> statusList) {
        this.statusList = statusList;
    }

    public Set<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(Set<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<String> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public Set<String> getDeviceAreaIds() {
        return deviceAreaIds;
    }

    public void setDeviceAreaIds(Set<String> deviceAreaIds) {
        this.deviceAreaIds = deviceAreaIds;
    }

    public Set<String> getAccountabilityDeptList() {
        return accountabilityDeptList;
    }

    public void setAccountabilityDeptList(Set<String> accountabilityDeptList) {
        this.accountabilityDeptList = accountabilityDeptList;
    }

    public String getInspectionTaskId() {
        return inspectionTaskId;
    }

    public void setInspectionTaskId(String inspectionTaskId) {
        this.inspectionTaskId = inspectionTaskId;
    }

    public List<Long> getInspectionStartTimes() {
        return inspectionStartTimes;
    }

    public void setInspectionStartTimes(List<Long> inspectionStartTimes) {
        this.inspectionStartTimes = inspectionStartTimes;
    }

    public List<Long> getInspectionEndTimes() {
        return inspectionEndTimes;
    }

    public void setInspectionEndTimes(List<Long> inspectionEndTimes) {
        this.inspectionEndTimes = inspectionEndTimes;
    }

    public Integer getInspectionDeviceCount() {
        return inspectionDeviceCount;
    }

    public void setInspectionDeviceCount(Integer inspectionDeviceCount) {
        this.inspectionDeviceCount = inspectionDeviceCount;
    }

    public String getInspectionDeviceCountOperate() {
        return inspectionDeviceCountOperate;
    }

    public void setInspectionDeviceCountOperate(String inspectionDeviceCountOperate) {
        this.inspectionDeviceCountOperate = inspectionDeviceCountOperate;
    }

    /**
     * 获取巡检设施数量筛选的值
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 12:22
     * @return 获取巡检设施数量筛选的值
     */
    @JsonIgnore
    public String getInspectionDeviceCountOperateValue() {
        return OperateUtil.getOperateValue(this.inspectionDeviceCountOperate);
    }



    public String getTodayStartTime() {
        return todayStartTime;
    }

    public void setTodayStartTime(String todayStartTime) {
        this.todayStartTime = todayStartTime;
    }

    public String getTodayEndTime() {
        return todayEndTime;
    }

    public void setTodayEndTime(String todayEndTime) {
        this.todayEndTime = todayEndTime;
    }

    public String getConcatSingleBackReason() {
        return concatSingleBackReason;
    }

    public void setConcatSingleBackReason(String concatSingleBackReason) {
        this.concatSingleBackReason = concatSingleBackReason;
    }

    public String getConcatErrorReason() {
        return concatErrorReason;
    }

    public void setConcatErrorReason(String concatErrorReason) {
        this.concatErrorReason = concatErrorReason;
    }

    public String getConcatProcessingScheme() {
        return concatProcessingScheme;
    }

    public void setConcatProcessingScheme(String concatProcessingScheme) {
        this.concatProcessingScheme = concatProcessingScheme;
    }

    public List<Long> getEcTimes() {
        return ecTimes;
    }

    public void setEcTimes(List<Long> ecTimes) {
        this.ecTimes = ecTimes;
    }

    public List<Long> getRcTimes() {
        return rcTimes;
    }

    public void setRcTimes(List<Long> rcTimes) {
        this.rcTimes = rcTimes;
    }

    public List<Long> getcTimes() {
        return cTimes;
    }

    public void setcTimes(List<Long> cTimes) {
        this.cTimes = cTimes;
    }

    public String getLastDaysOperate() {
        return lastDaysOperate;
    }

    public void setLastDaysOperate(String lastDaysOperate) {
        this.lastDaysOperate = lastDaysOperate;
    }

    /**
     * 剩余天数筛选
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 12:22
     * @return 剩余天数筛选
     */
    @JsonIgnore
    public String getLastDaysOperateValue() {
        return OperateUtil.getOperateValue(this.lastDaysOperate);
    }

    public List<String> getConcatSingleBackReasons() {
        return concatSingleBackReasons;
    }

    public void setConcatSingleBackReasons(List<String> concatSingleBackReasons) {
        this.concatSingleBackReasons = concatSingleBackReasons;
    }

    public List<String> getConcatErrorReasons() {
        return concatErrorReasons;
    }

    public void setConcatErrorReasons(List<String> concatErrorReasons) {
        this.concatErrorReasons = concatErrorReasons;
    }

    public List<String> getConcatProcessingSchemes() {
        return concatProcessingSchemes;
    }

    public void setConcatProcessingSchemes(List<String> concatProcessingSchemes) {
        this.concatProcessingSchemes = concatProcessingSchemes;
    }

    /**
     * 获取查询删除/恢复的参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 13:10
     * @param ids 工单编号
     * @param isDeleted 是否删除
     * @return 获取查询删除/恢复的参数
     */
    public static ProcBaseReq getSearchDeleteProcBaseParam(List<String> ids, String isDeleted) {
        ProcBaseReq procBaseReqParam = new ProcBaseReq();
        Set<String> procBaseSet = new HashSet<>();
        procBaseSet.addAll(ids);
        procBaseReqParam.setProcIds(procBaseSet);
        String isDeletedSearch = ProcBaseUtil.getIsDeletedSearchParam(isDeleted);
        procBaseReqParam.setIsDeleted(isDeletedSearch);
        return procBaseReqParam;
    }
}
