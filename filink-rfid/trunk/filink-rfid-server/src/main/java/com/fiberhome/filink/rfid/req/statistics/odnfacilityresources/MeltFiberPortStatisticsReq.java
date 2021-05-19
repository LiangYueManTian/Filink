package com.fiberhome.filink.rfid.req.statistics.odnfacilityresources;

import java.util.List;

/**
 * <p>
 * ONT设施资源统计请求   熔纤侧端口统计
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-11
 */
public class MeltFiberPortStatisticsReq {
    /**
     * 设施对象
     */
    private List<String> facilities;

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}
