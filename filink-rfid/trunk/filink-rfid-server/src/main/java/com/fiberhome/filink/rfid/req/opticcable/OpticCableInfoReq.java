package com.fiberhome.filink.rfid.req.opticcable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.fiberhome.filink.rfid.utils.OperateUtil;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 光缆信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OpticCableInfoReq extends OpticCableInfo {

    /**
     * 光缆id列表
     */
    private Set<String> opticCableIdList;

    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 光缆级别list
     */
    private List<String> opticCableLevels;

    /**
     * 纤芯数操作符
     */
    private String coreNumOperate;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getOpticCableLevels() {
        return opticCableLevels;
    }

    public void setOpticCableLevels(List<String> opticCableLevels) {
        this.opticCableLevels = opticCableLevels;
    }

    public String getCoreNumOperate() {
        return coreNumOperate;
    }

    public void setCoreNumOperate(String coreNumOperate) {
        this.coreNumOperate = coreNumOperate;
    }

    public Set<String> getOpticCableIdList() {
        return opticCableIdList;
    }

    public void setOpticCableIdList(Set<String> opticCableIdList) {
        this.opticCableIdList = opticCableIdList;
    }

    @JsonIgnore
    public String getCoreNumOperateValue() {
        return OperateUtil.getOperateValue(coreNumOperate);
    }

}
