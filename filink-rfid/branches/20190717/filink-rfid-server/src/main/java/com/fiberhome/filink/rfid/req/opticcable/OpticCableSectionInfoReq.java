package com.fiberhome.filink.rfid.req.opticcable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.utils.OperateUtil;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 光缆段请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OpticCableSectionInfoReq extends OpticCableSectionInfo {

    /**
     * 光缆段idList
     */
    private Set<String> opticCableSectionIdList;

    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 光缆名称
     */
    private String opticCableName;

    /**
     * 光缆芯数操作符
     */
    private String coreNumOperate;
    /**
     * 长度缆芯数操作符
     */
    private String lengthOperate;
    /**
     * 起始节点
     */
    private List<String> startNodes;
    /**
     * 起始节点设施类型
     */
    private List<String> startNodeDeviceTypes;
    /**
     * 终止节点
     */
    private List<String> terminationNodes;
    /**
     * 终止节点设施类型
     */
    private List<String> terminationNodeDeviceTypes;
    /**
     * 区域ids
     */
    private List<String> areaIds;

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

    public Set<String> getPermissionDeviceTypes() {
        return permissionDeviceTypes;
    }

    public void setPermissionDeviceTypes(Set<String> permissionDeviceTypes) {
        this.permissionDeviceTypes = permissionDeviceTypes;
    }

    public Set<String> getPermissionAreaIds() {
        return permissionAreaIds;
    }

    public void setPermissionAreaIds(Set<String> permissionAreaIds) {
        this.permissionAreaIds = permissionAreaIds;
    }

    public String getOpticCableName() {
        return opticCableName;
    }

    public void setOpticCableName(String opticCableName) {
        this.opticCableName = opticCableName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getStartNodeDeviceTypes() {
        return startNodeDeviceTypes;
    }

    public void setStartNodeDeviceTypes(List<String> startNodeDeviceTypes) {
        this.startNodeDeviceTypes = startNodeDeviceTypes;
    }

    public String getCoreNumOperate() {
        return coreNumOperate;
    }

    public void setCoreNumOperate(String coreNumOperate) {
        this.coreNumOperate = coreNumOperate;
    }

    public List<String> getTerminationNodeDeviceTypes() {
        return terminationNodeDeviceTypes;
    }

    public void setTerminationNodeDeviceTypes(List<String> terminationNodeDeviceTypes) {
        this.terminationNodeDeviceTypes = terminationNodeDeviceTypes;
    }

    public List<String> getAreaIds() {
        return areaIds;
    }
    public void setAreaIds(List<String> areaIds) {
        this.areaIds = areaIds;
    }

    public List<String> getStartNodes() {
        return startNodes;
    }
    public void setStartNodes(List<String> startNodes) {
        this.startNodes = startNodes;
    }

    public List<String> getTerminationNodes() {
        return terminationNodes;
    }
    public void setTerminationNodes(List<String> terminationNodes) {
        this.terminationNodes = terminationNodes;
    }

    public Set<String> getOpticCableSectionIdList() {
        return opticCableSectionIdList;
    }

    public void setOpticCableSectionIdList(Set<String> opticCableSectionIdList) {
        this.opticCableSectionIdList = opticCableSectionIdList;
    }

    public String getLengthOperate() {
        return lengthOperate;
    }

    public void setLengthOperate(String lengthOperate) {
        this.lengthOperate = lengthOperate;
    }

    @JsonIgnore
    public String getCoreNumOperateValue() {
        return OperateUtil.getOperateValue(coreNumOperate);
    }
    @JsonIgnore
    public String getLengthOperateValue() {
        return OperateUtil.getOperateValue(lengthOperate);
    }
}
