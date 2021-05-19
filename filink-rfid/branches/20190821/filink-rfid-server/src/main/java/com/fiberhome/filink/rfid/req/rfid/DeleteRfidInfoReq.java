package com.fiberhome.filink.rfid.req.rfid;

import com.fiberhome.filink.rfid.bean.rfid.RfidInfo;

import java.util.Set;

/**
 * <p>
 * rfid信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-12
 */
public class DeleteRfidInfoReq extends RfidInfo {
    private Set<String> rfidIdList;
    private Set<String> rfidCodeList;

    public Set<String> getRfidIdList() {
        return rfidIdList;
    }

    public void setRfidIdList(Set<String> rfidIdList) {
        this.rfidIdList = rfidIdList;
    }

    public Set<String> getRfidCodeList() {
        return rfidCodeList;
    }

    public void setRfidCodeList(Set<String> rfidCodeList) {
        this.rfidCodeList = rfidCodeList;
    }
}
