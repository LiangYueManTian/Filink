package com.fiberhome.filink.rfid.req.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;

/**
 * <p>
 * 新增成端信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-30
 */
public class InsertPortCableCoreInfoReq extends PortCableCoreInfo {

    /**
     * 端口id（用于更新端口状态）
     */
    private String portId;

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }
}
