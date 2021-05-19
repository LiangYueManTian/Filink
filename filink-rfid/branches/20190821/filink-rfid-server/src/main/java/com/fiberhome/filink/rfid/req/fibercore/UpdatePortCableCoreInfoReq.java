package com.fiberhome.filink.rfid.req.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;

/**
 * <p>
 * 删除成端信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-07-02
 */
public class UpdatePortCableCoreInfoReq extends PortCableCoreInfo {

    /**
     * 端口id（用于更新端口状态）
     */
    private String portId;

    /**
     * 端口使用状态（用于更新端口状态）
     */
    private Integer portStatus;

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public Integer getPortStatus() {
        return portStatus;
    }

    public void setPortStatus(Integer portStatus) {
        this.portStatus = portStatus;
    }
}
