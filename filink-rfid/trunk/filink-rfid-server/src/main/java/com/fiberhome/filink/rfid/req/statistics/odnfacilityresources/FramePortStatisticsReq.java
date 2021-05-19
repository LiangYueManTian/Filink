package com.fiberhome.filink.rfid.req.statistics.odnfacilityresources;

import com.fiberhome.filink.rfid.bean.facility.PortInfoBean;

import java.util.List;

/**
 * <p>
 * ONT设施资源统计请求  框端口统计
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-10
 */
public class FramePortStatisticsReq extends PortInfoBean {
    /**
     * 框号
     */
    private List<String> frameNos;

    public List<String> getFrameNos() {
        return frameNos;
    }

    public void setFrameNos(List<String> frameNos) {
        this.frameNos = frameNos;
    }
}
