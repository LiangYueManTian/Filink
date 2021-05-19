package com.fiberhome.filink.rfid.req.rfid.app;

import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
/**
 * <p>
 * 光缆段rfid信息app请求
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-05
 */
public class OpticCableSectionRfidInfoReqApp extends OpticCableSectionRfidInfo {

    /**
     * 半径
     */
    private String radius;

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}
