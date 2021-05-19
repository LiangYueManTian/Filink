package com.fiberhome.filink.rfid.req.statistics.opticable;

import java.util.List;
/**
 * <p>
 * 纤芯统计请求
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class CoreStatisticsReq {
    /**
     * 光缆段数组
     */
    private List<String> opticalCableSegment;

    public List<String> getOpticalCableSegment() {
        return opticalCableSegment;
    }

    public void setOpticalCableSegment(List<String> opticalCableSegment) {
        this.opticalCableSegment = opticalCableSegment;
    }
}
