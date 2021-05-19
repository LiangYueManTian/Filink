package com.fiberhome.filink.rfid.resp.statistics;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;

/**
 * <p>
 * ONT设施跳接关系 返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-10
 */
public class JumpConnectionStatisticsResp extends JumpFiberInfo {
    private String deviceName;

    private String oppositeDeviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOppositeDeviceName() {
        return oppositeDeviceName;
    }

    public void setOppositeDeviceName(String oppositeDeviceName) {
        this.oppositeDeviceName = oppositeDeviceName;
    }
}
