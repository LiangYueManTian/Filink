package com.fiberhome.filink.rfid.req.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;

import java.util.List;

/**
 * <p>
 * 成端信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-30
 */
public class PortCableCoreInfoReq extends PortCableCoreInfo {

    /**
     * 所属设施id列表（用于批量删除设施）
     */
    private List<String> deviceIds;

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
