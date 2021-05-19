package com.fiberhome.filink.rfid.req.statistics.opticable;

import java.util.List;

/**
 * <p>
 * 光缆段统计请求
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class OpticCableInfoSectionStatisticsReq {
    /**
     *设施id
     */
    private List<String> facilities;
    /**
     *区域id
     */
    private List<String> areas;

    /**
     * 光缆段数组
     */
    private List<String> opticalCableSegment;

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public List<String> getOpticalCableSegment() {
        return opticalCableSegment;
    }

    public void setOpticalCableSegment(List<String> opticalCableSegment) {
        this.opticalCableSegment = opticalCableSegment;
    }
}
