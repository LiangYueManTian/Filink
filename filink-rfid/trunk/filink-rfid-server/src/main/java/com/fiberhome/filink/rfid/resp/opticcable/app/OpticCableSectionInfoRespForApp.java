package com.fiberhome.filink.rfid.resp.opticcable.app;

import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;

/**
 * <p>
 * app光缆段信息表返回类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OpticCableSectionInfoRespForApp extends OpticCableSectionInfo {
    /**
     * 光缆名称
     */
    private String opticCableName;
    /**
     * 所属区域名称
     */
    private String areaName;
    /**
     * 起始节点设备
     */
    private String startNodeName;
    /**
     * 起始节点设施标签ID
     */
    private String startNodeLabel;
    /**
     * 终止节点设备
     */
    private String terminationNodeName;
    /**
     * 终止节点设施标签ID
     */
    private String terminationNodeLabel;

    public String getOpticCableName() {
        return opticCableName;
    }

    public void setOpticCableName(String opticCableName) {
        this.opticCableName = opticCableName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getStartNodeName() {
        return startNodeName;
    }

    public void setStartNodeName(String startNodeName) {
        this.startNodeName = startNodeName;
    }

    public String getTerminationNodeName() {
        return terminationNodeName;
    }

    public void setTerminationNodeName(String terminationNodeName) {
        this.terminationNodeName = terminationNodeName;
    }

    public String getStartNodeLabel() {
        return startNodeLabel;
    }

    public void setStartNodeLabel(String startNodeLabel) {
        this.startNodeLabel = startNodeLabel;
    }

    public String getTerminationNodeLabel() {
        return terminationNodeLabel;
    }

    public void setTerminationNodeLabel(String terminationNodeLabel) {
        this.terminationNodeLabel = terminationNodeLabel;
    }
}
