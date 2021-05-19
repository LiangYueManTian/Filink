package com.fiberhome.filink.rfid.resp.rfid;

import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
/**
 * <p>
 * 光缆段rfid信息表返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class OpticCableSectionRfidInfoResp extends OpticCableSectionRfidInfo {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施类型
     */
    private String deviceType;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
