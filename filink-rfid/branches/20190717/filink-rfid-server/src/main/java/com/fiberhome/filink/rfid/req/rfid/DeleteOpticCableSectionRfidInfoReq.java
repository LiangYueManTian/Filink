package com.fiberhome.filink.rfid.req.rfid;

import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;

import java.util.Set;

/**
 * <p>
 * 删除光缆段gis信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-07-03
 */
public class DeleteOpticCableSectionRfidInfoReq extends OpticCableSectionRfidInfo {

    /**
     * gisIdList
     */
    private Set<String> opticStatusIdList;

    public Set<String> getOpticStatusIdList() {
        return opticStatusIdList;
    }

    public void setOpticStatusIdList(Set<String> opticStatusIdList) {
        this.opticStatusIdList = opticStatusIdList;
    }
}
