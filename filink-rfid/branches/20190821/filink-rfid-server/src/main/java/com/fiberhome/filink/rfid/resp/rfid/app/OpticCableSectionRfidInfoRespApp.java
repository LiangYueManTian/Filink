package com.fiberhome.filink.rfid.resp.rfid.app;

import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;

/**
 * <p>
 * 光缆段rfid信息表app返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class OpticCableSectionRfidInfoRespApp extends OpticCableSectionRfidInfo {
    /**
     * 光缆段名称
     */
    private String opticCableSectionName;

    public String getOpticCableSectionName() {
        return opticCableSectionName;
    }

    public void setOpticCableSectionName(String opticCableSectionName) {
        this.opticCableSectionName = opticCableSectionName;
    }


}
